package com.juaby.labs.rpc;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Title: <br>
 * Description: <br>
 * Company: <a href=www.juaby.com>卓贝</a> <br>
 *
 * @author <a href=mailto:yanjiabao@juaby.com>yanjiabao</a> <br>
 * @date Created by yanjiabao on 2015/8/25 16:57.
 */
public class SerializeTool {

    public static <T> byte[] serialize(T object) {
        if (object == null) {
            return null;
        }
        Schema<T> schema = RuntimeSchema.getSchema((Class<T>)object.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(4096); //TODO
        return ProtostuffIOUtil.toByteArray(object, schema, buffer);
    }

    public static <T> T deserialize(byte[] objectBytes, T object) {
        if (objectBytes == null) {
            return null;
        }
        Schema<T> schema = RuntimeSchema.getSchema((Class<T>) object.getClass());
        ProtostuffIOUtil.mergeFrom(objectBytes, object, schema);
        return object;
    }

}
