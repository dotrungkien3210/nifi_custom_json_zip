///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.nifi.processors.encryptvalue;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import org.apache.nifi.util.MockFlowFile;
//import org.apache.nifi.util.TestRunner;
//import org.apache.nifi.util.TestRunners;
//import org.junit.Test;
//
//public class FlowfileJsonTest {
//
//    private final Path unencryptedFile = Paths.get("src/test/resources/unencrypted.json");
//    private final TestRunner runner = TestRunners.newTestRunner(new ModifyValue());
//
//    @Test
//    public void testSHA512() throws IOException {
//        Path sha512File = Paths.get("src/test/resources/sha512.json");
//        testEncryption(sha512File);
//    }
//
//    @Test
//    public void testNoEncryption() throws IOException {
//        runner.setProperty(FlowfileProperties.FLOW_FORMAT, "JSON");
//
//        runner.enqueue(unencryptedFile);
//
//        runner.run();
//        runner.assertQueueEmpty();
//        runner.assertAllFlowFilesTransferred(FlowfileRelationships.REL_BYPASS, 1);
//
//        final MockFlowFile outFile = runner.getFlowFilesForRelationship(FlowfileRelationships.REL_BYPASS).get(0);
//
//
//    }
//
//    private void testEncryption(final Path encryptedFile) throws IOException {
//        runner.setProperty(FlowfileProperties.FIELD_NAMES, "first_name,last_name,card_number");
//        runner.setProperty(FlowfileProperties.FLOW_FORMAT, "JSON");
//
//        runner.enqueue(unencryptedFile);
//
//        runner.run();
//        runner.assertQueueEmpty();
//        runner.assertAllFlowFilesTransferred(FlowfileRelationships.REL_SUCCESS, 1);
//
//        final MockFlowFile outFile = runner.getFlowFilesForRelationship(FlowfileRelationships.REL_SUCCESS).get(0);
//
//    }
//
//}
