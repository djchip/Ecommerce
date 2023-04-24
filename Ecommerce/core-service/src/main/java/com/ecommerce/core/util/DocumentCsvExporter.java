package com.ecommerce.core.util;

import com.ecommerce.core.entities.Document;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DocumentCsvExporter extends AbstractExporter{
    public void export(List<Document> listDocument, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv");
        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//        String release = format.format(d.getReleaseDate());

        String[] csvHeader = {"Number", "Name", "Type", "Signer", "Field", "Unit", "Note", "File Name", "Description"};
        String[] fieldMapping = {"documentNumber", "documentName", "documentType", "signer", "field", "unit", "note", "file", "description"};

        csvBeanWriter.writeHeader(csvHeader);
        for (Document d : listDocument){
            csvBeanWriter.write(d, fieldMapping);
        }
        csvBeanWriter.close();
    }
}
