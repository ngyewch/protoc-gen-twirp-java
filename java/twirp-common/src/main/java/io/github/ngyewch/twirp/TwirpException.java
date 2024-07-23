package io.github.ngyewch.twirp;

import java.util.Map;

public class TwirpException extends RuntimeException {
  private final TwirpError error;

  public TwirpException(TwirpError error) {
    super();

    this.error = error;
  }

  public TwirpException(String code, String msg, Map<String, String> meta) {
    this(TwirpError.from(code, msg, meta));
  }

  public TwirpException(TwirpErrorCode errorCode, String msg, Map<String, String> meta) {
    this(TwirpError.from(errorCode, msg, meta));
  }

  public TwirpError getError() {
    return error;
  }

  @Override
  public String getMessage() {
    final TwirpErrorCode errorCode = TwirpErrorCode.fromCode(error.getCode());
    String s = "";
    if (errorCode != null) {
      s += String.format("%s (%d): ", errorCode.getCode(), errorCode.getHttpStatus());
    } else if (error.getCode() != null) {
      s += String.format("%s: ", error.getCode());
    }
    if (error.getMsg() != null) {
      s += error.getMsg();
    }
    return s;
  }
}
