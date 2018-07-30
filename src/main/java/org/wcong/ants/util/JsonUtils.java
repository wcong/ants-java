package org.wcong.ants.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.codehaus.jackson.map.ObjectMapper;
import org.wcong.ants.transport.TransportMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/9
 */
public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseJsonMap(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public static List<Object> parseJsonList(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static Function<TransportMessage, ByteBuf> jsonEncoder = pojo -> {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            objectMapper.writeValue(out, pojo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Unpooled.copiedBuffer(out.toByteArray());
    };
    public static Function<String, TransportMessage> jsonDecoder = s -> {
        try {
            return objectMapper.readValue(s, TransportMessage.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

}
