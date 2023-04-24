package com.ecommerce.core.service.impl;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;

public class ImageDetector implements RenderListener {
    @Override
    public void beginTextBlock() {

    }

    @Override
    public void renderText(TextRenderInfo textRenderInfo) {

    }

    @Override
    public void endTextBlock() {

    }

    @SneakyThrows
    @Override
    public void renderImage(ImageRenderInfo imageRenderInfo) {
        imageFound = true;
        this.bufferedImage = imageRenderInfo.getImage().getBufferedImage();
    }
    boolean imageFound = false;
    BufferedImage bufferedImage;
}
