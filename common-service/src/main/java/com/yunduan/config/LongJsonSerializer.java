package com.yunduan.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Long 类型字段序列化时转为字符串，避免js丢失精度
 *
 */
public class LongJsonSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long aLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String text = (aLong == null ? null : String.valueOf(aLong));
        if (text != null) {
            jsonGenerator.writeString(text);
        }
    }

}

