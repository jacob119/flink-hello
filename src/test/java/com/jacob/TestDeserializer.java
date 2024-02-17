package com.jacob;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;

import java.io.IOException;

public class TestDeserializer implements DeserializationSchema<String>, SerializationSchema<String> {
    @Override
    public String deserialize(byte[] bytes) throws IOException {
        return new String(bytes);
    }

    @Override
    public boolean isEndOfStream(String s) {
        return false;
    }

    @Override
    public byte[] serialize(String s) {
        return s.getBytes();
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return TypeExtractor.getForClass(String.class);
    }
}
