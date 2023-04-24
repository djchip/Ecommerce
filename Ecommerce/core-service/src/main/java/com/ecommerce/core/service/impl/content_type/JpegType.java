package com.ecommerce.core.service.impl.content_type;

import com.ecommerce.core.service.ContentType;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class JpegType implements ContentType {
    final String TYPE = ".jfif";
    private String content;

    @Override
    public void handleContentType(InputStream inputStream) throws IOException, TesseractException {
        BufferedImage imBuff = ImageIO.read(inputStream);
        String content = readFileService.readImage(imBuff);
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

    private void setContent(String content){
        this.content = content;
    }
}
