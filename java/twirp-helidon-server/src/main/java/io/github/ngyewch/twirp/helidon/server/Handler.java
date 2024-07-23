package io.github.ngyewch.twirp.helidon.server;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.github.ngyewch.twirp.TwirpError;
import io.github.ngyewch.twirp.TwirpErrorCode;
import io.github.ngyewch.twirp.helidon.MediaTypes;
import io.helidon.common.http.MediaType;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class Handler {
  public static void handleTwirp(
      ServerRequest req,
      ServerResponse res,
      Message.Builder messageBuilder,
      Supplier<Message> serviceInvoker) {
    if (req.headers().contentType().isEmpty()) {
      sendError(res, TwirpErrorCode.INVALID_ARGUMENT, "Content-Type not specified", null);
      return;
    }
    final MediaType contentType = req.headers().contentType().get();
    if (contentType.equals(MediaTypes.PROTOBUF_MEDIA_TYPE)) {
      req.content()
          .as(byte[].class)
          .thenAccept(
              contentBytes -> {
                try {
                  messageBuilder.mergeFrom(contentBytes);
                  final Message response = serviceInvoker.get();
                  res.headers().contentType(contentType);
                  res.send(response.toByteArray());
                } catch (InvalidProtocolBufferException e) {
                  sendError(res, TwirpErrorCode.MALFORMED, "Malformed content", null);
                } catch (Exception e) {
                  sendError(
                          res,
                          TwirpErrorCode.INTERNAL,
                          e.toString(),
                          Collections.singletonMap("stackTrace", ExceptionUtils.getStackTrace(e)));
                }
              });
    } else if (contentType.equals(MediaTypes.JSON_MEDIA_TYPE)) {
      req.content()
          .as(String.class)
          .thenAccept(
              contentString -> {
                try {
                  JsonFormat.parser().merge(contentString, messageBuilder);
                  final Message response = serviceInvoker.get();
                  final String json = JsonFormat.printer().print(response);
                  res.headers().contentType(contentType);
                  res.send(json);
                } catch (InvalidProtocolBufferException e) {
                  sendError(res, TwirpErrorCode.MALFORMED, "Malformed content", null);
                } catch (Exception e) {
                  sendError(
                      res,
                      TwirpErrorCode.INTERNAL,
                      e.toString(),
                      Collections.singletonMap("stackTrace", ExceptionUtils.getStackTrace(e)));
                }
              });
    } else {
      sendError(res, TwirpErrorCode.INVALID_ARGUMENT, "Content-Type not supported", null);
    }
  }

  public static void sendError(
      ServerResponse res, TwirpErrorCode errorCode, String msg, Map<String, String> meta) {
    res.status(errorCode.getHttpStatus());
    try {
      final TwirpError error = new TwirpError();
      error.setCode(errorCode.getCode());
      error.setMsg(msg);
      error.setMeta(meta);
      final String errorJson = TwirpError.toJson(error);
      res.headers().contentType(MediaTypes.JSON_MEDIA_TYPE);
      res.send(errorJson);
    } catch (IOException e) {
      res.send();
    }
  }
}
