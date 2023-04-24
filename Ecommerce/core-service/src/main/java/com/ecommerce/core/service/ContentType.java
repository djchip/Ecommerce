package com.ecommerce.core.service;

import com.ecommerce.core.service.impl.ReadFileServiceImpl;

import java.io.InputStream;

public interface ContentType {
    ReadFileService readFileService = new ReadFileServiceImpl();
    void handleContentType(InputStream inputStream) throws Exception;
    String getType();
    String getContent();
}
