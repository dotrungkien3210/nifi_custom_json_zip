package com.nifi.processors.encryptvalue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.nifi.processors.util.FormatStream;

import org.apache.avro.Schema;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.StreamCallback;

@Tags({"encrypt", "hash", "json", "pii", "salt"})
@CapabilityDescription("Encrypts the values of the given fields of a FlowFile. The original value is replaced with the hashed one.")
public class ModifyValue extends AbstractProcessor {

    private List<PropertyDescriptor> descriptors;
    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        final List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
        descriptors.add(FlowfileProperties.FLOW_FORMAT);
        descriptors.add(FlowfileProperties.AVRO_SCHEMA);
        descriptors.add(FlowfileProperties.FIELD_NAMES);
        descriptors.add(FlowfileProperties.ACTION);
        descriptors.add(FlowfileProperties.FIRST_INPUT);
        descriptors.add(FlowfileProperties.SECOND_INPUT);
        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<Relationship>();
        relationships.add(FlowfileRelationships.REL_SUCCESS);
        relationships.add(FlowfileRelationships.REL_FAILURE);
        relationships.add(FlowfileRelationships.REL_BYPASS);
        this.relationships = Collections.unmodifiableSet(relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        FlowFile flowFile = session.get();
        if ( flowFile == null ) {
            return;
        }
        try {
            final String rawFieldNames = context.getProperty(FlowfileProperties.FIELD_NAMES).evaluateAttributeExpressions(flowFile).getValue();
            if ("".equals(rawFieldNames) || rawFieldNames == null) {
                session.transfer(flowFile, FlowfileRelationships.REL_BYPASS);
                return;
            }
            final List<String> fieldNames = Arrays.asList(rawFieldNames.split(","));
            final String flowFormat = context.getProperty(FlowfileProperties.FLOW_FORMAT).getValue();
            final String action = context.getProperty(FlowfileProperties.ACTION).getValue();
            final String firstInput = context.getProperty(FlowfileProperties.FIRST_INPUT).getValue();
            final String secondInput = context.getProperty(FlowfileProperties.SECOND_INPUT).getValue();
            final String schemaString = context.getProperty(FlowfileProperties.AVRO_SCHEMA).evaluateAttributeExpressions(flowFile).getValue();

            session.write(flowFile, new StreamCallback(){
                @Override
                public void process(InputStream in, OutputStream out) throws IOException {
                    JsonFactory jsonFactory = new JsonFactory().setRootValueSeparator(null);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    JsonParser jsonParser;
                    JsonGenerator jsonGen = jsonFactory.createGenerator(baos);

                    Schema schema = null;

                    if (flowFormat.equals("AVRO")) {
                        try {
                            schema = new Schema.Parser().parse(schemaString);
                        } catch (NullPointerException e) {
                            schema = FormatStream.getEmbeddedSchema(in);
                            in.reset();
                        }
                        in = FormatStream.avroToJson(in, schema);
                    }

                    Reader r = new InputStreamReader(in);
                    BufferedReader br = new BufferedReader(r);
                    String line;

                    while ((line = br.readLine()) != null) {
                        jsonParser = jsonFactory.createParser(line);
                        while (jsonParser.nextToken() != null) {
                            jsonGen.copyCurrentEvent(jsonParser);
                            String key = jsonParser.getCurrentName();
                            if(fieldNames.stream().anyMatch(field -> field.equalsIgnoreCase(key))) {
                                jsonParser.nextToken();
                                String valueToHash = jsonParser.getText();
                                if ("null".equals(valueToHash))
                                    jsonGen.writeNull();
                                else {
                                    jsonGen.writeString(valueToHash);
                                }
                            }
                        }
                        jsonGen.writeRaw("\n");
                    }
                    jsonGen.flush();

                    if (flowFormat.equals("AVRO"))
                        baos = FormatStream.jsonToAvro(baos, schema);

                    baos.writeTo(out);
                }
            });

            session.transfer(flowFile, FlowfileRelationships.REL_SUCCESS);

        } catch (ProcessException e) {
            getLogger().error("Something went wrong", e);
            session.transfer(flowFile, FlowfileRelationships.REL_FAILURE);
        }
    }
}
