package io.github.ngyewch.twirp.helidon.server;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.github.ngyewch.twirp.helidon.MediaTypes;
import io.helidon.common.http.Http;
import io.helidon.common.http.MediaType;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import java.util.function.Supplier;

public class Handler {
  public static void handleTwirp(
      ServerRequest req,
      ServerResponse res,
      Message.Builder messageBuilder,
      Supplier<Message> serviceInvoker) {
    if (req.headers().contentType().isEmpty()) {
      res.status(Http.Status.BAD_REQUEST_400).send("Content-Type not specified");
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
                  res.status(Http.Status.BAD_REQUEST_400).send("Malformed content");
                } catch (Exception e) {
                  res.status(Http.Status.INTERNAL_SERVER_ERROR_500).send(e.toString());
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
                  res.status(Http.Status.BAD_REQUEST_400).send("Malformed content");
                } catch (Exception e) {
                  res.status(Http.Status.INTERNAL_SERVER_ERROR_500).send(e.toString());
                }
              });
    } else {
      res.status(Http.Status.BAD_REQUEST_400).send("Content-Type not supported");
    }
  }
}
