package com.ecommerce.core.service.impl.content_type;

import com.ecommerce.core.service.ContentType;
import net.sourceforge.tess4j.TesseractException;

import java.io.IOException;
import java.io.InputStream;

public class PdfType implements ContentType {
    private String content;
    final String PDF_TYPE = ".pdf";

    @Override
    public void handleContentType(InputStream inputStream) throws IOException, TesseractException {
        String content = readFileService.readPDF(inputStream);
        setContent(content);
    }

    @Override
    public String getType() {
        return PDF_TYPE;
    }

    @Override
    public String getContent() {
        return content;
    }

    private void setContent(String content) {
        this.content = content;
    }
}
