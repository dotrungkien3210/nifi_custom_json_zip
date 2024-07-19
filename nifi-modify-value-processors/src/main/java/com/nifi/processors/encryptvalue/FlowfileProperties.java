package com.nifi.processors.encryptvalue;



import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.util.StandardValidators;

public class FlowfileProperties {

    public static final PropertyDescriptor FLOW_FORMAT = new PropertyDescriptor
            .Builder().name("FLOW_FORMAT")
            .displayName("FlowFile Format")
            .description("Specify the format of the incoming FlowFile. If AVRO, output is automatically Snappy compressed.")
            .required(true)
            .allowableValues("JSON", "AVRO")
            .defaultValue("JSON")
            .build();

    public static final PropertyDescriptor AVRO_SCHEMA = new PropertyDescriptor
            .Builder().name("AVRO_SCHEMA")
            .displayName("Avro Schema")
            .description("Specify the schema if the FlowFile format is Avro.")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(true)
            .build();

    public static final PropertyDescriptor FIELD_NAMES = new PropertyDescriptor
            .Builder().name("FIELD_NAMES")
            .displayName("Field Names")
            .description("Comma separated list of fields whose values to encrypt.")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(true)
            .build();

    public static final PropertyDescriptor ACTION = new PropertyDescriptor
            .Builder().name("ACTION")
            .displayName("Action")
            .description("Comma separated list of fields whose values to encrypt.")
            .required(true)
            .allowableValues("Replace", "Substring")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(true)
            .build();

    public static final PropertyDescriptor FIRST_INPUT = new PropertyDescriptor
            .Builder().name("FIRST_INPUT")
            .displayName("First input")
            .description("The first parameter required to perform the action.")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(true)
            .build();

    public static final PropertyDescriptor SECOND_INPUT = new PropertyDescriptor
            .Builder().name("SECOND_INPUT")
            .displayName("Second input")
            .description("The second parameter required to perform the action .")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .expressionLanguageSupported(true)
            .build();


}
