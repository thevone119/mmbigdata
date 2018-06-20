package com.bingo.common.utility;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/14.
 */
public class JsonClass {
    public static <T> String toJSON(T bean) {
        ObjectMapper oMapper = new ObjectMapper();
        try {
            return oMapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> T fromJSON(String json, TypeReference<?> toValueTypeRef) {
        ObjectMapper oMapper = new ObjectMapper();
        oMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        oMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        oMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return oMapper.readValue(json, toValueTypeRef);
        } catch (IOException e) {
            return null;
        }
    }

    public static Map fromJSONToMap(String json) {
        ObjectMapper oMapper = new ObjectMapper();
        oMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        oMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        oMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return oMapper.readValue(json, Map.class);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T fromJSON(String json, Class<T> modelclass) {
        ObjectMapper oMapper = new ObjectMapper();
        oMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return oMapper.readValue(json, modelclass);
        } catch (IOException e) {
            return null;
        }
    }

    public static JsonNode toJSONObject(String str) {
        ObjectMapper oMapper = new ObjectMapper();
        try {
            return oMapper.readTree(str);
        } catch (IOException e) {
            return null;
        }
    }
}
