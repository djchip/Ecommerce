package com.ecommerce.core.service.impl.content_type;

import com.ecommerce.core.service.ContentType;

import java.io.InputStream;

public class TextHTML implements ContentType {

    public TextHTML() {

    }

    @Override
    public void handleContentType(InputStream inputStream) throws Exception {

    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getContent() {
        return null;
    }
}
