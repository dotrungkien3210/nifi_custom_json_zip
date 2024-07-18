package com.nineteen04labs.processors.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class Compression {
    public static byte[] compressString(String inputString) throws IOException {
        byte[] inputBytes = inputString.getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Deflater deflater = new Deflater();
        deflater.setInput(inputBytes);
        deflater.finish();

        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }

        deflater.end();
        outputStream.close();

        return outputStream.toByteArray();
    }

    public static String decompressString(byte[] compressedData) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);

        Inflater inflater = new Inflater();
        InflaterInputStream inflaterInputStream = new InflaterInputStream(inputStream, inflater);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inflaterInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inflaterInputStream.close(); // Close the stream

        return outputStream.toString();
    }
}
