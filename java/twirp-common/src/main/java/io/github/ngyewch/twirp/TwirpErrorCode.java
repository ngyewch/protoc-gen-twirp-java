package io.github.ngyewch.twirp;

public enum TwirpErrorCode {
  CANCELED("canceled", 408),
  UNKNOWN("unknown", 500),
  INVALID_ARGUMENT("invalid_argument", 400),
  MALFORMED("malformed", 400),
  DEADLINE_EXCEEDED("deadline_exceeded", 408),
  NOT_FOUND("not_found", 404),
  BAD_ROUTE("bad_route", 404),
  ALREADY_EXISTS("already_exists", 409),
  PERMISSION_DENIED("permission_denied", 403),
  UNAUTHENTICATED("unauthenticated", 401),
  RESOURCE_EXHAUSTED("resource_exhausted", 429),
  FAILED_PRECONDITION("failed_precondition", 412),
  ABORTED("aborted", 409),
  OUT_OF_RANGE("out_of_range", 400),
  UNIMPLEMENTED("unimplemented", 501),
  INTERNAL("internal", 500),
  UNAVAILABLE("unavailable", 503),
  DATALOSS("dataloss", 500);

  private final String code;
  private final int httpStatus;

  TwirpErrorCode(String code, int httpStatus) {
    this.code = code;
    this.httpStatus = httpStatus;
  }

  public String getCode() {
    return code;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public static TwirpErrorCode fromCode(String code) {
    for (final TwirpErrorCode errorCode : TwirpErrorCode.values()) {
      if (errorCode.code.equals(code)) {
        return errorCode;
      }
    }
    return null;
  }
}
