package io.github.ngyewch.twirp.helidon;

import io.github.ngyewch.twirp.Constants;
import io.helidon.common.http.MediaType;

public class MediaTypes {
  public static final MediaType PROTOBUF_MEDIA_TYPE =
      MediaType.parse(Constants.PROTOBUF_CONTENT_TYPE);
  public static final MediaType JSON_MEDIA_TYPE = MediaType.parse(Constants.JSON_CONTENT_TYPE);
}
