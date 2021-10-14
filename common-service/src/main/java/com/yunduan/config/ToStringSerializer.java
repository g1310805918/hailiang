package com.yunduan.config;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;


public class ToStringSerializer implements ObjectSerializer {

    /**
     * 转换为Json字符串时将Long类型id转换为字符串
     */
    @Override
    public void write(JSONSerializer jsonSerializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {

        SerializeWriter out = jsonSerializer.out;

        if (object == null) {
            out.writeNull();
            return;
        }

        String strVal = object.toString();
        out.writeString(strVal);
    }

}
