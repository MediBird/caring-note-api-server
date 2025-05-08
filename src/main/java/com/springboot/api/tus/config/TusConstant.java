package com.springboot.api.tus.config;

public interface TusConstant {

    String TUS_RESUMABLE_HEADER = "Tus-Resumable";
    String TUS_RESUMABLE_VALUE = "1.0.0";

    String TUS_VERSION_HEADER = "Tus-Version";
    String TUS_VERSION_VALUE = "1.0.0,0.2.2,0.2.1";

    String TUS_EXTENSION_HEADER = "Tus-Extension";

    String UPLOAD_OFFSET_HEADER = "Upload-Offset";

    String UPLOAD_LENGTH_HEADER = "Upload-Length";

    String UPLOAD_DEFER_LENGTH_HEADER = "Upload-Defer-Length";

    String UPLOAD_METADATA = "Upload-Metadata";

    String LOCATION_HEADER = "Location";

    String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    String ACCESS_CONTROL_ALLOW_ORIGIN_VALUE = "*";

    String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";
    String ACCESS_CONTROL_ALLOW_METHODS_VALUE = "GET,PUT,PATCH,POST,DELETE";

    String ACCESS_CONTROL_EXPOSE_HEADER = "Access-Control-Expose-Headers";
    String ACCESS_CONTROL_EXPOSE_OPTIONS_VALUE = "Tus-Resumable, Tus-Version, Tus-Max-Size, Tus-Extension";
    String ACCESS_CONTROL_EXPOSE_POST_VALUE = "Location, Tus-Resumable";

    String CACHE_CONTROL_HEADER = "Cache-Control";
    String CACHE_CONTROL_VALUE = "no-store";

    String URL_PREFIX = "/v1/tus";

    String OFFSET_OCTET_STREAM = "application/offset+octet-stream";

    String AUDIO_WEBM = "audio/webm";
}
