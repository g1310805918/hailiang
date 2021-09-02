package com.yunduan.config;

import cn.hutool.core.convert.Convert;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * 将字符串转为Long
 *
 */
public class LongJsonDeserializer extends JsonDeserializer<Long> {

    private static final transient Logger log = LoggerFactory.getLogger(LongJsonDeserializer.class);

    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.getText();
        try {
            return value == null ? null : Convert.toLong(value);
        } catch (NumberFormatException e) {
            log.error("【主键反序列化】解析长整形错误", e);
            return null;
        }
    }

}
