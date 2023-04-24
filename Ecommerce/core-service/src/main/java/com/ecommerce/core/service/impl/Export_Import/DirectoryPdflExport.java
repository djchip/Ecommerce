package com.ecommerce.core.service.impl.Export_Import;

import com.ecommerce.core.entities.Directory;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class DirectoryPdflExport {


    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Name Directory", font));

        table.addCell(cell);
//
//        cell.setPhrase(new Phrase("Foder", font));
//        table.addCell(cell);

        cell.setPhrase(new Phrase("Describe", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Create By", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Create date", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Update By", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Update Date", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table, List<Directory> listDirectory) {
        for (Directory D : listDirectory) {
            table.addCell(D.getName());
//            table.addCell(D.getFoder());
            table.addCell(D.getDescription());
            table.addCell(D.getCreate_by());
            table.addCell(String.valueOf(D.getCreatedDate()));
            table.addCell(D.getUpdate_by());
            table.addCell(String.valueOf(D.getUpdatedDate()));
        }
    }

    public void export(List<Directory> listDirectory, HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("List of Directory", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {2.5f, 3.5f, 3.0f, 3.0f, 3.5f,3.5f,3.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table,listDirectory);

        document.add(table);

        document.close();

    }
}
