package com.ecommerce.core.service.impl.content_type;

import com.ecommerce.core.service.ContentType;

import java.io.InputStream;

public class HtmlType implements ContentType {

    final String HTML_TYPE = ".html";

    @Override
    public void handleContentType(InputStream inputStream) {

    }

    @Override
    public String getType() {
        return HTML_TYPE;
    }

    @Override
    public String getContent() {
        return null;
    }


}
