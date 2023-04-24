package com.ecommerce.core.service;

import net.sourceforge.tess4j.TesseractException;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public interface ReadFileService {
    String readFile(InputStream file) throws Exception;

    String detectDocTypeUsingDetector(InputStream stream) throws IOException;

    String readImage(BufferedImage bufferedImage) throws TesseractException;

    String readPDF(InputStream stream) throws IOException, TesseractException;

}
