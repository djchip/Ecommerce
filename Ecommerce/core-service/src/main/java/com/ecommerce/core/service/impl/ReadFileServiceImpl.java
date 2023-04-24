package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.service.ReadFileService;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ReadFileServiceImpl implements ReadFileService {

    @Override
    public String readFile(InputStream stream) throws IOException, TikaException {
        Tika tika = new Tika();
        String content = tika.parseToString(stream);
        return content;
    }

    @Override
    public String detectDocTypeUsingDetector(InputStream stream) throws IOException {
        Tika tika = new Tika();
        return tika.detect(stream);
    }

    @Override
    public String readImage(BufferedImage bufferedImage) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(ConstantDefine.FILE_PATH.ORC);
        System.out.println(" _________________________________________path file :" + ConstantDefine.FILE_PATH.ORC + "________________________________________________");
        tesseract.setLanguage("vie+eng");
        tesseract.setPageSegMode(1);
        tesseract.setOcrEngineMode(1);
        System.out.println(" _________________________________________path file :" + tesseract.toString() + "________________________________________________");
        return tesseract.doOCR(bufferedImage);
    }

    @Override
    public String readPDF(InputStream stream) throws IOException, TesseractException {
        PdfReader reader = new PdfReader(stream);
        StringBuilder contentFile = new StringBuilder();
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        for (int pageNumber = 1; pageNumber <= reader.getNumberOfPages(); pageNumber++) {
            String contentPage = "";
            ImageDetector imageDetector = new ImageDetector();
            parser.processContent(pageNumber, imageDetector);
            if (imageDetector.imageFound) {
                contentPage = readImage(imageDetector.bufferedImage);
                // There is at least one image rendered on page i
                // Thus, handle it as an image page
            } else {
                // There is no image rendered on page i
                // Thus, handle it as a no-image page
                contentPage = PdfTextExtractor.getTextFromPage(reader, pageNumber);
            }
            contentFile.append(contentPage);
        }
        reader.close();
        return contentFile.toString();
    }
}