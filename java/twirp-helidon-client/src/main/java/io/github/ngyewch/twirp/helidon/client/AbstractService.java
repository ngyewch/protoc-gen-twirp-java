package io.github.ngyewch.twirp.helidon.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.github.ngyewch.twirp.TwirpError;
import io.github.ngyewch.twirp.TwirpException;
import io.github.ngyewch.twirp.helidon.MediaTypes;
import io.helidon.common.http.MediaType;
import io.helidon.webclient.WebClient;
import io.helidon.webclient.WebClientException;
import io.helidon.webclient.WebClientResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public abstract class AbstractService {
  private final WebClient webClient;
  private final MediaType contentType;

  protected AbstractService(String baseUri, MediaType contentType) {
    super();

    webClient = WebClient.builder().baseUri(baseUri).build();
    this.contentType = contentType;
  }

  protected void doRequest(String path, Message input, Message.Builder outputBuilder) {
    if (contentType.equals(MediaTypes.PROTOBUF_MEDIA_TYPE)) {
      doProtobufRequest(path, input, outputBuilder);
    } else if (contentType.equals(MediaTypes.JSON_MEDIA_TYPE)) {
      doJsonRequest(path, input, outputBuilder);
    } else {
      throw new IllegalArgumentException("unsupported content type");
    }
  }

  private void doProtobufRequest(String path, Message input, Message.Builder outputBuilder) {
    try {
      webClient
          .post()
          .path(path)
          .contentType(MediaTypes.PROTOBUF_MEDIA_TYPE)
          .submit(input.toByteArray(), byte[].class)
          .map(
              responseBytes -> {
                try {
                  return outputBuilder.mergeFrom(responseBytes);
                } catch (InvalidProtocolBufferException e) {
                  throw new RuntimeException(e);
                }
              })
          .get();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      if (e.getCause() != null) {
        handleException(e.getCause());
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  private void doJsonRequest(String path, Message input, Message.Builder outputBuilder) {
    try {
      final String requestJson = JsonFormat.printer().print(input);
      webClient
          .post()
          .path(path)
          .contentType(MediaTypes.JSON_MEDIA_TYPE)
          .submit(requestJson, String.class)
          .map(
              responseJson -> {
                try {
                  JsonFormat.parser().merge(responseJson, outputBuilder);
                  return outputBuilder;
                } catch (InvalidProtocolBufferException e) {
                  throw new RuntimeException(e);
                }
              })
          .get();
    } catch (InterruptedException | InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      if (e.getCause() != null) {
        handleException(e.getCause());
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  private static void handleException(Throwable t) {
    if (t instanceof WebClientException) {
      final WebClientException wce = (WebClientException) t;
      if (wce.response().isPresent()) {
        final WebClientResponse wcr = wce.response().get();
        try {
          final String json = wcr.content().as(String.class).get();
          final TwirpError twirpError = TwirpError.fromJson(json);
          throw new TwirpException(twirpError);
        } catch (InterruptedException | ExecutionException | IOException e2) {
          // ignore
        }
      }
      throw wce;
    } else if (t instanceof RuntimeException) {
      throw (RuntimeException) t;
    } else {
      throw new RuntimeException(t);
    }
  }
}
