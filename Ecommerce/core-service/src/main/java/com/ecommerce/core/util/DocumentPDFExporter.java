package com.ecommerce.core.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ecommerce.core.dto.ListDocumentDTO;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DocumentPDFExporter extends AbstractExporter{


//    public void export(List<Document> listDocument, HttpServletResponse response) throws IOException {

    public void export(List<ListDocumentDTO> listDocument, HttpServletResponse response) throws IOException, DocumentException {

        super.setResponseHeader(response, "application/pdf", ".pdf");
        com.lowagie.text.Document document = new com.lowagie.text.Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD);
        font.setSize(18);
        font.setColor(13, 22, 196);

        Paragraph paragraph = new Paragraph("List Document", font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(paragraph);

        PdfPTable table = new PdfPTable(13);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);
        table.setWidths(new float[] {1.8f, 3.5f, 2.0f, 2.0f, 2.5f, 2.5f, 2.5f, 2.0f, 2.0f, 3.5f, 3.5f, 3.5f, 3.5f});

        writeTableHeader(table);
        writeTableData(table, listDocument);
        document.add(table);
        document.close();
    }

    private void writeTableData(PdfPTable table, List<ListDocumentDTO> listDocument){
        int i = 0;
        for(ListDocumentDTO d: listDocument){
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String releaseDate = format.format(d.getReleaseDate());
            String createdDate = format.format(d.getCreatedDate());
            String updatedDate = format.format(d.getUpdatedDate());

            table.addCell(String.valueOf(i++));
            table.addCell(d.getDocumentName());
            table.addCell(d.getDocumentTypeName());
            table.addCell(d.getDocumentNumber());
            table.addCell(releaseDate);
            table.addCell(d.getSigner());
            table.addCell(d.getFieldName());
            table.addCell(d.getReleaseByName());
            table.addCell(d.getDescription());
            table.addCell(d.getCreatedBy());
            table.addCell(createdDate);
            table.addCell(d.getUpdatedBy());
            table.addCell(updatedDate);
        }
    }

    private void writeTableHeader(PdfPTable table){
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.GRAY);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("STT", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Tên văn bản", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Loại văn bản", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Số hiệu", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Ngày ban hành", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Người ký", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Lĩnh vực", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Đơn vị phát hành", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Mô tả", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Người tạo", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Ngày tạo", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Người cập nhật", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Ngày cập nhật", font));
        table.addCell(cell);

    }
}
