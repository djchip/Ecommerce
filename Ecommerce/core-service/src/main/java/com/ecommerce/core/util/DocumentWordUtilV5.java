package com.ecommerce.core.util;

import com.ecommerce.core.service.ProofService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
public class DocumentWordUtilV5 {

	@Autowired
	ProofService proofService;

	public static void main(String[] args) {
		try {
//			 String filePath = "d:/app/";
//			 String fileName = "VNUA2022.docx";
//			 String url = "https://192.168.2.8:9980/browser/20c06f5/cool.html?WOPISrc=http://192.168.2.8:8388/assessment/proof/wopi/files/1455#Quyết-định-thành-lập-nhóm-nghiên-cứu-mạnh,-tinh-hoa,-xuất-sắc";
//			 processFileWord(filePath + fileName, filePath + "VNUA2023.docx", "AUN01.01.01.01", url, "Minh chứng");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void readWord(String path) throws InvalidFormatException, IOException{
		XWPFDocument d = new XWPFDocument(OPCPackage.open(path));
		
        List<XWPFParagraph> paragraphList = d.getParagraphs();
	    for (XWPFParagraph p : paragraphList) {
	    	System.out.println("paragrap: " + p.getText());
	    	
	    	List<XWPFRun> runs = p.getRuns();
	    	for (XWPFRun r : runs) {
	    		System.out.println("\t---->run: " + r.text());
	    		
	    	}	
		}
	    
	    
	    for (XWPFTable t : d.getTables()) {
	    	System.out.println("table: " + t.getText());
	    	for(XWPFTableRow row: t.getRows()) {
	    		System.out.println("\t---->row: " + row.toString());
		    	for (XWPFTableCell cell : row.getTableCells()) {
		    		System.out.println("\t\t---->cell: " + cell.getText());
		    		for (XWPFParagraph p : cell.getParagraphs()) {
		    	    	System.out.println("\t\t\t---->cellParagrap: " + p.getText());
		    	    	List<XWPFRun> runs = p.getRuns();
		    	    	for (XWPFRun r : runs) {
		    	    		System.out.println("\t\t\t\t---->run: " + r.text());
		    	    	}	
		    		}
		    	}	
	    	}
		}
	}

	public static void processFileWord(String path, String pathSave, String keyword, String uri, String tooltip) throws InvalidFormatException, IOException{
		
		readWord(path);
		
		XWPFDocument d = new XWPFDocument(OPCPackage.open(path));
	    XWPFDocument d1 = new XWPFDocument(OPCPackage.open(path));
	    
	    XWPFDocument ddhope = xWPFDocumentReplace(d, d1, keyword, uri, tooltip);
	    saveFileWord(pathSave, ddhope);

	}
		
	public static void paragraphReplace(List<XWPFParagraph> listParagrap1, List<XWPFParagraph> listParagrap2, String findText, String uri, String tooltip){
		int paragrapIndex = -1;
		int runIndex;

		for(XWPFParagraph p : listParagrap1) {
			paragrapIndex++;
			XWPFParagraph p2 = listParagrap2.get(paragrapIndex);
			String pText = p.getText();
        	if(pText.contains(findText)) {
        		
        		List<XWPFRun> runsP = p.getRuns();
        		runIndex = -1;
        		for(XWPFRun run : runsP) {
        			runIndex++;
 
        			String text = run.text();

	                if(text.contains(findText)) {
	                	//xoa bo XWPFRun tai vi tri hien tai
	                	p2.removeRun(runIndex);
	                	
	                	XWPFHyperlinkRun link = p2.insertNewHyperlinkRun(runIndex, uri);//createHyperlinkRunAtPosition(p2, uri, runIndex, tooltip);
	                	link.getCTHyperlink().setTooltip(tooltip);
	                	link.setText(text);
	                	link.setUnderline(UnderlinePatterns.SINGLE);
	                	link.setColor("0000FF");
	                }
        		}
        	}
		}
	}
	
	public static XWPFDocument xWPFDocumentReplace(XWPFDocument doc, XWPFDocument doc2, String findText, String uri, String tooltip){
		//Replace in Paragraphs
		paragraphReplace(doc.getParagraphs(), doc2.getParagraphs(), findText, uri, tooltip);

		int tableIndex = -1;
		int rowIndex = -1;
		int cellIndex = -1;
		
		for (XWPFTable t : doc.getTables()) {
			tableIndex++;
			rowIndex = -1;
	    	for(XWPFTableRow row: t.getRows()) {
	    		rowIndex++;
	    		cellIndex = -1;
		    	for (XWPFTableCell cell : row.getTableCells()) {
		    		cellIndex++;
		    		if(cell.getText().contains(findText)) {
			    		List<XWPFParagraph> listParagrap1 = cell.getParagraphs();
			    		List<XWPFParagraph> listParagrap2 = doc2.getTables().get(tableIndex).getRow(rowIndex).getCell(cellIndex).getParagraphs();
			    		paragraphReplace(listParagrap1, listParagrap2, findText, uri, tooltip);
		    		}

		    	}	
	    	}
		}


        return doc2;
    }
    
	public static void saveFileWord(String filePath, XWPFDocument doc) throws IOException {
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
