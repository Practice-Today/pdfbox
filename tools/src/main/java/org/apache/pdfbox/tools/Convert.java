package org.apache.pdfbox.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.util.DatatypeConverterImpl;

/**
 * Convert a file from Base64 to Binary (or vice-versa depending on option).
 *
 * @author  Glenn Wood
 */
public final class Convert
{
	private static int lineSiz = 80;
	private Convert() { }
    
    public static void main(String[] args) throws IOException {
    	
        // suppress the Dock icon on OS X
        System.setProperty("apple.awt.UIElement", "true");

        if ( args.length != 3 ) {
            usage(args);
        }
        else {

        	FileInputStream inStream = null;
        	FileOutputStream outStream = null;
            try {
	
	            inStream = new FileInputStream(args[1]);
	            outStream = new FileOutputStream(args[2]);
	            
	            switch (args[0].toUpperCase()) {
	            case "-BASE64TOBINARY":
	            	base64ToBinary(inStream,outStream);
	            	break;
	            case "-BINARYTOBASE64":
	            	binaryToBase64(inStream,outStream);
	            	break;
	        	default:
	                usage(args);
	            }
	        }        
            finally
            {
                if( inStream != null )
                	inStream.close();
                if( outStream != null )
                	outStream.close();
            }
        }
    }

    private static void base64ToBinary(FileInputStream inStream, FileOutputStream outStream) throws IOException {
        InputStreamReader isReader = new InputStreamReader(inStream);
        BufferedReader reader = new BufferedReader(isReader);
        StringBuffer sb = new StringBuffer();
        String str;
        while ((str = reader.readLine())!= null) {
           sb.append(str);
        }
		String base64 = sb.toString();
    	byte[] binary = DatatypeConverterImpl.parseBase64Binary(base64);
		outStream.write(binary);
		outStream.close();
	}

	private static void binaryToBase64(FileInputStream inStream, FileOutputStream outStream) throws IOException {
		byte[] binary = IOUtils.toByteArray(inStream);
		byte[] bytes = new DatatypeConverterImpl().printBase64Binary(binary).getBytes();
        int len = bytes.length;
		for (int idx=0; idx<len; idx+=lineSiz) {
			outStream.write(bytes,idx,Math.min(len - idx,lineSiz));
			outStream.write(System.getProperty("line.separator").getBytes());
		}
	}

    private static void usage(String args[])
    {
    	String crlf = System.getProperty("line.separator");
        String message = "Usage: java -jar " + Version.getJarName(Convert.class) + " Convert [option] <inputfile> <outputfile>"
                + crlf + crlf + "Options:" + crlf
                + "  -base64ToBinary : Convert the Base64 file into a binary file." + crlf
                + "  -binaryToBase64 : Convert the binary file into a Base64 file.\n";
        System.err.println(message);
        System.exit(1);
    }

}
