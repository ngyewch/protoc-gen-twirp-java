package io.github.ngyewch.twirp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

public class TwirpError {
  private String code;
  private String msg;
  private Map<String, String> meta;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Map<String, String> getMeta() {
    return meta;
  }

  public void setMeta(Map<String, String> meta) {
    this.meta = meta;
  }

  public static TwirpError from(String code, String msg, Map<String, String> meta) {
    final TwirpError error = new TwirpError();
    error.setCode(code);
    error.setMsg(msg);
    error.setMeta(meta);
    return error;
  }

  public static TwirpError from(TwirpErrorCode errorCode, String msg, Map<String, String> meta) {
    return from(errorCode.getCode(), msg, meta);
  }

  public static TwirpError fromJson(String s) throws IOException {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper.readValue(s, TwirpError.class);
  }

  public static String toJson(TwirpError error) throws IOException {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper.writeValueAsString(error);
  }
}
