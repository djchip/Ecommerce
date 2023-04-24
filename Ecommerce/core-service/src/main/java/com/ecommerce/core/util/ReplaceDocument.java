package com.ecommerce.core.util;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileOutputStream;
import java.io.IOException;

public class ReplaceDocument {

    public static XWPFDocument replace(XWPFDocument doc, String findText, String replaceText){
        doc.getParagraphs().forEach(p ->{
            p.getRuns().forEach(run -> {
                String text = run.text();
                if(text.contains(findText)) {
                    run.setText(text.replace(findText, replaceText), 0);
                }
            });
        });
        return doc;
    }

    public static void saveWord(String filePath, XWPFDocument doc) throws IOException {
        FileOutputStream out = null;
        try{
            out = new FileOutputStream(filePath);
            doc.write(out);
        }
        finally{
            out.close();
        }
    }
}
