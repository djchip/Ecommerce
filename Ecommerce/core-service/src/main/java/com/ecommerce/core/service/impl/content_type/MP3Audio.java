package com.ecommerce.core.service.impl.content_type;

import com.ecommerce.core.service.ContentType;

import java.io.InputStream;

public class MP3Audio implements ContentType {
    final String TYPE = ".mp4";

    @Override
    public void handleContentType(InputStream inputStream) {

    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getContent() {
        return null;
    }
}
