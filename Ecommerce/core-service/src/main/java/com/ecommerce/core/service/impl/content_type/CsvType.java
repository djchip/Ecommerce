package com.ecommerce.core.service.impl.content_type;

import com.ecommerce.core.service.ContentType;

import java.io.InputStream;

public class CsvType implements ContentType {
    final String TYPE = ".csv";
    private String content;

    @Override
    public void handleContentType(InputStream inputStream) throws Exception {
        String content = readFileService.readFile(inputStream);
        setContent(content);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getContent() {
        return content;
    }

    private void setContent(String content) {
        this.content = content;
    }
}
