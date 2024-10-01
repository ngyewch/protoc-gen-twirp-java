package io.github.ngyewch.twirp.httpclient.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.github.ngyewch.twirp.*;
import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public abstract class AbstractService {
  public static final ContentType PROTOBUF_CONTENT_TYPE =
      ContentType.create("application/protobuf");
  public static final ContentType JSON_CONTENT_TYPE = ContentType.create("application/json");

  private final URI baseUri;
  private final CloseableHttpClient httpClient;
  private final ContentType contentType;

  protected AbstractService(String baseUri, ContentType contentType) {
    super();

    this.baseUri = URI.create(baseUri);
    this.httpClient = HttpClients.createDefault();
    this.contentType = contentType;
  }

  protected void doRequest(String path, Message input, Message.Builder outputBuilder) {
    final URI uri = baseUri.resolve(path);
    try {
      if (contentType == PROTOBUF_CONTENT_TYPE) {
        doProtobufRequest(uri, input, outputBuilder);
      } else if (contentType == JSON_CONTENT_TYPE) {
        doJsonRequest(uri, input, outputBuilder);
      } else {
        throw new IllegalArgumentException("unsupported content type");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void doProtobufRequest(URI uri, Message input, Message.Builder outputBuilder)
      throws IOException {
    final HttpEntity requestBody = new ByteArrayEntity(input.toByteArray(), PROTOBUF_CONTENT_TYPE);
    final HttpPost request = new HttpPost(uri);
    request.setEntity(requestBody);
    try (final CloseableHttpResponse response = httpClient.execute(request)) {
      final HttpEntity responseBody = response.getEntity();
      try {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
          expectContentType(responseBody, Constants.PROTOBUF_CONTENT_TYPE);
          mergeProtobuf(outputBuilder, EntityUtils.toByteArray(responseBody));
        } else {
          expectContentType(responseBody, Constants.JSON_CONTENT_TYPE);
          throw new TwirpException(TwirpError.fromJson(EntityUtils.toString(responseBody)));
        }
      } finally {
        EntityUtils.consume(responseBody);
      }
    }
  }

  private void doJsonRequest(URI uri, Message input, Message.Builder outputBuilder)
      throws IOException {
    final HttpEntity requestBody =
        new StringEntity(JsonFormat.printer().print(input), ContentType.APPLICATION_JSON);
    final HttpPost request = new HttpPost(uri);
    request.setEntity(requestBody);
    try (final CloseableHttpResponse response = httpClient.execute(request)) {
      final HttpEntity responseBody = response.getEntity();
      try {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
          expectContentType(responseBody, Constants.JSON_CONTENT_TYPE);
          mergeProtobufJson(outputBuilder, EntityUtils.toString(responseBody));
        } else {
          expectContentType(responseBody, Constants.JSON_CONTENT_TYPE);
          throw new TwirpException(TwirpError.fromJson(EntityUtils.toString(responseBody)));
        }
      } finally {
        EntityUtils.consume(responseBody);
      }
    }
  }

  private void expectContentType(HttpEntity responseBody, String expectedContentType) {
    final String contentType =
        (responseBody.getContentType() != null) ? responseBody.getContentType().getValue() : null;
    if (contentType == null) {
      throw new TwirpException(TwirpErrorCode.INVALID_ARGUMENT, "content type not specified", null);
    } else if (!contentType.equals(expectedContentType)) {
      throw new TwirpException(
          TwirpErrorCode.INVALID_ARGUMENT,
          "unexpected content type",
          new Meta().set("Content-Type", contentType).get());
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
