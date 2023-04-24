package com.ecommerce.core.service.impl.content_type;

import com.ecommerce.core.service.ContentType;

import java.io.InputStream;

public class MicrosoftWordOpenXML implements ContentType {
    final String TYPE = ".docx";
    private String content;

    @Override
    public void handleContentType(InputStream inputStream) throws Exception {
//        XWPFDocument document = new XWPFDocument(inputStream);
//        List<XWPFParagraph> paragraphs = document.getParagraphs();
//        for (XWPFParagraph para : paragraphs) {
//            System.out.println(para.getText());
//        }
//        inputStream.close();
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
