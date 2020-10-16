package org.apache.pdfbox.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.util.DatatypeConverterImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Convert a file from Base64 to Binary (or vice-versa depending on option).
 *
 * @author  Glenn Wood
 */
public final class Convert
{
	private static int lineSiz = 80;
	private Convert() { }
    
    public static void main(String[] args) throws Exception {
    	
        // suppress the Dock icon on OS X
        System.setProperty("apple.awt.UIElement", "true");

        if ( args.length < 3 || args.length > 4 ) {
            usage(args);
        }
        else {

        	InputStream inStream = null;
        	OutputStream outStream = null;
        	
            try {
	
            	if (args[1].endsWith(".xml")) {
            		File file = new File(args[1]);  
            		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
            		DocumentBuilder db = dbf.newDocumentBuilder();  
            		Document doc = db.parse(file);  
            		doc.getDocumentElement().normalize();
            		
            		XPath xPath = XPathFactory.newInstance().newXPath();
            		String expression = "/xfdf/fields/field[@name='SIG_IMAGE']";
            		Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
            		
            		String sig_image = node.getTextContent();
            		System.out.println("Root element: " + sig_image);  
            		inStream =new ByteArrayInputStream(sig_image.getBytes());
            	}
            	else
            		inStream = new FileInputStream(args[1]);
	            
	            switch (args[0].toUpperCase()) {
	            case "-BASE64TOBINARY":
    	            outStream = new FileOutputStream(args[2]);
	            	base64ToBinary(inStream,outStream);
	            	break;
	            	
	            case "-BINARYTOBASE64":
    	            
    	            if (args[2].endsWith(".xml")) {
    	            	
    	            	String inXml = args[2];
    	            	String outXml = (args.length == 4)?args[3]:args[2];
    	            	
                		File file = new File(inXml);  
                		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
                		DocumentBuilder db = dbf.newDocumentBuilder();  
                		Document doc = db.parse(file);
                		doc.getDocumentElement().normalize();                  		
                		XPath xPath = XPathFactory.newInstance().newXPath();
                		String expression = "/xfdf/fields/field[@name='SIG_IMAGE']";
                		Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
                		if ( node == null )
                			throw new Exception("Can't find "+expression+" in "+inXml);
                		
                		byte[] bytes = binaryToBase64(inStream);
                		node.getParentNode().setNodeValue("<![CDATA[["+new String(bytes)+"]]>");

                	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
        	    	    DOMSource source = new DOMSource(doc);
        	    	    StreamResult result = new StreamResult(new File(outXml));
        	    	    transformer.transform(source, result);   	    

                	}
    	            else {
        	            outStream = new FileOutputStream(args[2]);
    	            	binaryToBase64(inStream,outStream);
    	            }
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

    private static void base64ToBinary(InputStream inStream, OutputStream outStream) throws IOException {
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

	private static byte[] binaryToBase64(InputStream inStream) throws IOException {
		byte[] binary = IOUtils.toByteArray(inStream);
		byte[] bytes = new DatatypeConverterImpl().printBase64Binary(binary).getBytes();
		return bytes;
	}

	private static void binaryToBase64(InputStream inStream, OutputStream outStream) throws IOException {
		byte[] bytes = binaryToBase64(inStream);
        int len = bytes.length;
		for (int idx=0; idx<len; idx+=lineSiz) {
			outStream.write(bytes, idx, Math.min(len - idx,lineSiz));
			outStream.write(System.getProperty("line.separator").getBytes());
		}
	}

    private static void usage(String args[])
    {
    	String cmd = "       java -jar " + Version.getJarName(Convert.class)+" Convert ";
    	String crlf = System.getProperty("line.separator");
        String message = 
        		"Usage: "+ crlf +
crlf + cmd + "[option] <inputfile> <outputfile> [<real-outputfile>]" + crlf +
crlf + "Options:" + crlf
+ "  -base64ToBinary : Convert Base64 to binary." + crlf
+ "  -binaryToBase64 : Convert binary to Base64.\n" + crlf +
crlf +"Read hexadecimal from text file, write to binary file:" + crlf +
cmd + "-base64ToBinary <inputFile> <outputfile>" + crlf +
crlf +"Read hexadecimal from XML file, write to binary file:" + crlf +
cmd + "-base64ToBinary <inputXmlFile.xml> <outputfile>" + crlf +
crlf +"Read binary from binary file, write hexadecimal to text file:" + crlf +
cmd + "-binaryToBase64 <inputXmlFile.xml> <existingXmlFile.xml>" + crlf +
crlf +"Read binary from binary file, replace hexadecimal in XML file:" + crlf +
cmd + "-binaryToBase64 <inputXmlFile.xml> <existingXmlFile.xml>" + crlf +
crlf +"Read binary from binary file, write hexadecimal into a new XML file:" + crlf +
cmd + "-binaryToBase64 <inputXmlFile.xml> <existingXmlFile.xml> <newXmlFile.xml>" + crlf;
        System.err.println(message);
        System.exit(1);
    }

}
