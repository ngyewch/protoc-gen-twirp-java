package io.github.ngyewch.twirp.helidon.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.github.ngyewch.twirp.Meta;
import io.github.ngyewch.twirp.TwirpError;
import io.github.ngyewch.twirp.TwirpErrorCode;
import io.github.ngyewch.twirp.TwirpException;
import io.github.ngyewch.twirp.helidon.MediaTypes;
import io.helidon.common.http.Http;
import io.helidon.common.http.MediaType;
import io.helidon.common.reactive.Single;
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
    final Single<Message.Builder> requester;
    if (contentType.equals(MediaTypes.PROTOBUF_MEDIA_TYPE)) {
      requester = doProtobufRequest(path, input, outputBuilder);
    } else if (contentType.equals(MediaTypes.JSON_MEDIA_TYPE)) {
      requester = doJsonRequest(path, input, outputBuilder);
    } else {
      throw new IllegalArgumentException("unsupported content type");
    }
    try {
      requester.get();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      if (e.getCause() != null) {
        if (e.getCause() instanceof RuntimeException) {
          throw (RuntimeException) e.getCause();
        } else {
          throw new RuntimeException(e.getCause());
        }
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  private Single<Message.Builder> doProtobufRequest(
      String path, Message input, Message.Builder outputBuilder) {
    return webClient
        .post()
        .path(path)
        .contentType(MediaTypes.PROTOBUF_MEDIA_TYPE)
        .submit(input.toByteArray())
        .flatMap(this::handleNonSuccessfulResponse)
        .first()
        .flatMap(
            webClientResponse -> expectMediaType(webClientResponse, MediaTypes.PROTOBUF_MEDIA_TYPE))
        .first()
        .flatMap(webClientResponse -> webClientResponse.content().as(byte[].class))
        .first()
        .map(bytes -> mergeProtobuf(outputBuilder, bytes));
  }

  private Single<Message.Builder> doJsonRequest(
      String path, Message input, Message.Builder outputBuilder) {
    try {
      final String requestJson = JsonFormat.printer().print(input);
      return webClient
          .post()
          .path(path)
          .contentType(MediaTypes.JSON_MEDIA_TYPE)
          .submit(requestJson)
          .flatMap(this::handleNonSuccessfulResponse)
          .first()
          .flatMap(
              webClientResponse -> expectMediaType(webClientResponse, MediaTypes.JSON_MEDIA_TYPE))
          .first()
          .flatMap(webClientResponse -> webClientResponse.content().as(String.class))
          .first()
          .map(responseJson -> mergeProtobufJson(outputBuilder, responseJson));
    } catch (InvalidProtocolBufferException e) {
      throw new TwirpException(TwirpErrorCode.INTERNAL, e);
    }
  }

  private Single<WebClientResponse> handleNonSuccessfulResponse(
      WebClientResponse webClientResponse) {
    if (webClientResponse.status().code() < Http.Status.MOVED_PERMANENTLY_301.code()) {
      return Single.just(webClientResponse);
    }
    final MediaType mediaType = webClientResponse.headers().contentType().orElse(null);
    if ((mediaType != null) && mediaType.equals(MediaTypes.JSON_MEDIA_TYPE)) { // Twirp error
      return webClientResponse
          .content()
          .as(String.class)
          .map(
              s -> {
                try {
                  throw new TwirpException(TwirpError.fromJson(s));
                } catch (IOException e) {
                  throw new WebClientException(
                      "Request failed with code " + webClientResponse.status().code());
                }
              });
    } else {
      return Single.error(
          new WebClientException("Request failed with code " + webClientResponse.status().code()));
    }
  }

  private Single<WebClientResponse> expectMediaType(
      WebClientResponse webClientResponse, MediaType expectedMediaType) {
    final MediaType mediaType = webClientResponse.headers().contentType().orElse(null);
    if ((mediaType == null) || !mediaType.equals(expectedMediaType)) {
      return Single.error(
          new TwirpException(
              TwirpErrorCode.INVALID_ARGUMENT,
              "unexpected content type",
              (mediaType != null)
                  ? new Meta().set("Content-Type", mediaType.toString()).get()
                  : null));
    } else {
      return Single.just(webClientResponse);
    }
  }

  private Message.Builder mergeProtobufJson(Message.Builder messageBuilder, String json) {
    try {
      JsonFormat.parser().merge(json, messageBuilder);
      return messageBuilder;
    } catch (InvalidProtocolBufferException e) {
      throw new TwirpException(TwirpErrorCode.MALFORMED, e, true);
    }
  }

  private Message.Builder mergeProtobuf(Message.Builder messageBuilder, byte[] data) {
    try {
      return messageBuilder.mergeFrom(data);
    } catch (InvalidProtocolBufferException e) {
      throw new TwirpException(TwirpErrorCode.MALFORMED, e, true);
    }
  }
}
