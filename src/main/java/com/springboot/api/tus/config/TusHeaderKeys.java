package com.springboot.api.tus.config;

public final class TusHeaderKeys {
    private TusHeaderKeys() { // 인스턴스화 방지
    }

    // --- Header Keys ---
    public static final String TUS_RESUMABLE = "Tus-Resumable";
    public static final String TUS_VERSION = "Tus-Version";
    public static final String TUS_EXTENSION = "Tus-Extension";
    public static final String UPLOAD_OFFSET = "Upload-Offset";
    public static final String UPLOAD_LENGTH = "Upload-Length";
    public static final String UPLOAD_DEFER_LENGTH = "Upload-Defer-Length";
    public static final String UPLOAD_METADATA = "Upload-Metadata";
    public static final String LOCATION = "Location";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CONTENT_TYPE = "Content-Type"; // General Content-Type key
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String X_RECORDING_DURATION = "X-Recording-Duration";

    // --- Common Header Values ---
    // Tus-Resumable Default Value
    public static final String TUS_RESUMABLE_VALUE = "1.0.0";
    // Tus-Version Supported Values
    public static final String TUS_VERSION_VALUE = "1.0.0,0.2.2,0.2.1";
    // Tus-Extension Supported Values
    public static final String TUS_EXTENSION_VALUE = "creation,expiration,termination,concatenation";
    // Access-Control-Allow-Origin Default Value
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN_VALUE = "*";
    // Access-Control-Allow-Methods Default Value
    public static final String ACCESS_CONTROL_ALLOW_METHODS_VALUE = "GET,PUT,PATCH,POST,DELETE";
    // Access-Control-Expose-Headers Values for specific scenarios
    public static final String ACCESS_CONTROL_EXPOSE_OPTIONS_VALUE = "Tus-Resumable,Tus-Version,Tus-Max-Size,Tus-Extension";
    public static final String ACCESS_CONTROL_EXPOSE_POST_VALUE = "Location,Tus-Resumable";
    // Cache-Control Default Value
    public static final String CACHE_CONTROL_VALUE = "no-store";

    // --- Specific Content-Type Values ---
    public static final String CONTENT_TYPE_OFFSET_OCTET_STREAM = "application/offset+octet-stream";
    public static final String CONTENT_TYPE_AUDIO_WEBM = "audio/webm";

    // --- Other TUS Related Constants ---
    public static final String API_URL_PREFIX = "/v1/tus";
} 