package org.apache.pdfbox.examples.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;


/**
 *
 * @author Glenn Wood
 */
public class PDFlatten {

    private PDFlatten()
    {
    }

    /**
     * This will print the documents text page by page.
     *
     * @param args The command line arguments.
     *
     * @throws IOException If there is an error parsing or extracting the document.
     */
    public static void main(String[] args) throws IOException
    {
        if (args.length != 2)
        {
            usage();
        }
        
     	File pdfFile = new File(args[0]);/*"/usr/share/nginx/uploads/test/companydata/ctocompany/forms/pdf_summary_1001_Doe_John_FORMS-2.pdf")*/;
    	
    	PDDocument pDDocument = PDDocument.load(pdfFile);    
    	PDAcroForm pDAcroForm = pDDocument.getDocumentCatalog().getAcroForm();
    	
    	pDAcroForm.setNeedAppearances(true);
    	pDAcroForm.refreshAppearances();
        List<PDField> fields = new ArrayList<PDField>();
        for (PDField field: pDAcroForm.getFieldTree())
        {
            fields.add(field);
        }
    	pDAcroForm.flatten(fields,true);
    	pDAcroForm.refreshAppearances();
    	
    	pDDocument.save(args[1]);/*"/tmp/pdf_summary_1001_Doe_John_FORMS-2.pdf");*/
    	pDDocument.close();
    }

    /**
     * This will print the usage for this document.
     */
    private static void usage()
    {
        System.err.println("Usage: java " + PDFlatten.class.getName() + " <input-pdf> <output-pdf>");
        System.exit(-1);
    }
}
