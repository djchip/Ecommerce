package com.ecommerce.core.service.impl.content_type;

import com.ecommerce.core.service.ContentType;
import net.sourceforge.tess4j.TesseractException;

import java.io.IOException;
import java.io.InputStream;

public class TextType implements ContentType {
    final String TYPE = ".txt";
    private String content;

    @Override
    public void handleContentType(InputStream inputStream) throws IOException, TesseractException {
        String content = readFileService.readPDF(inputStream);
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
