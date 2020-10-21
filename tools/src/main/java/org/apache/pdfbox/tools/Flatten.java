/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.tools;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.fdf.FDFDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

/**
 * This example will take a PDF document and fill the fields with data from the
 * FDF fields.
 *
 * @author Ben Litchfield
 */
public class Flatten
{
    /**
     * Creates a new instance of ImportFDF.
     */
    public Flatten()
    {
    }

    /**
     * This will takes the values from the fdf document and import them into the
     * PDF document.
     *
     * @param pdfDocument The document to put the fdf data into.
     * @param fdfDocument The FDF document to get the data from.
     *
     * @throws IOException If there is an error setting the data in the field.
     */
    public void flatten( PDDocument pdfDocument ) throws Exception
    {
        PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        if (acroForm == null)
            return;
    	acroForm.setNeedAppearances(true);
    	acroForm.flatten(true);
    }

    /**
     * This will import an fdf document and write out another pdf.
     * <br>
     * see usage() for commandline
     *
     * @param args command line arguments
     *
     * @throws IOException If there is an error importing the FDF document.
     */
    public static void main(String[] args) throws Exception
    {
        // suppress the Dock icon on OS X
        System.setProperty("apple.awt.UIElement", "true");

        Flatten importer = new Flatten();
        importer.importFDF( args );
    }

    private void importFDF( String[] args ) throws Exception
    {
        PDDocument pdf = null;

        try
        {
            if( args.length < 2 )
                usage(args);

            Flatten importer = new Flatten();
            pdf = PDDocument.load( new File(args[0]) );
            importer.flatten( pdf );
            pdf.save( args[1] );
        }
        finally
        {
            pdf.close();
        }
    }

    private static void usage(String args[])
    {
    	String crlf = System.getProperty("line.separator");
        String message = "Usage: java -jar " + Version.getJarName(Convert.class) + " Flatten <input-pdf-file> <output-pdf-file>" + crlf;
        System.err.println(message);
        System.exit(1);
    }

}
