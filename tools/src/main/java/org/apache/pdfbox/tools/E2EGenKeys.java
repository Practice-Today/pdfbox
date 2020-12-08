package org.apache.pdfbox.tools;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.pdfbox.tools.encrypt.RSAGen;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;

// REF: https://mkyong.com/java/java-asymmetric-cryptography-example/
// REF: https://www.codesandnotes.be/2018/07/17/openpgp-java-keys-generation/
public class E2EGenKeys {

	public void writeToFile(File file, byte[] key) throws IOException {
		if ( null != file.getParentFile() ) file.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(key);
		fos.flush();
		fos.close();
	}

	public void writeToFile(File path, String key) throws IOException {
		writeToFile(path, key.getBytes());
	}

	public static void main(String[] args) {
		
        if( args.length != 3 )
            usage(args);
        String keyId = args[0];

		try {
	        char pass[] = new String("f5JtBxh7ZwwszuYHZo9").toCharArray();
	        PGPKeyRingGenerator krgen = RSAGen.generateKeyRingGenerator(keyId, pass);

	        if ( args[1].equals("stdout") || args[1].equals("-") ) 
	        	System.out.print(generateArmoredSecretKeyRing(krgen));
	        else
		        Files.write(Paths.get(args[1]), generateArmoredSecretKeyRing(krgen).getBytes());

	        if ( args[2].equals("stdout") || args[2].equals("-") )
	        	System.out.print(generateArmoredPublicKeyRing(krgen));
	        else
		        Files.write(Paths.get(args[2]), generateArmoredPublicKeyRing(krgen).getBytes());
	        	
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

	}
	
    private static String generateArmoredPublicKeyRing(PGPKeyRingGenerator keyRingGenerator) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (
                ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(outputStream);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(armoredOutputStream)
        ) {
            keyRingGenerator.generatePublicKeyRing().encode(bufferedOutputStream, true);
        }
        return outputStream.toString(UTF_8.name());
    }
    
    private static String generateArmoredSecretKeyRing(PGPKeyRingGenerator keyRingGenerator) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (
                ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(outputStream);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(armoredOutputStream)
        ) {
            keyRingGenerator.generateSecretKeyRing().encode(bufferedOutputStream);
        }
        return outputStream.toString(UTF_8.name());
    }
    
    private static void usage(String args[])
    {
    	String crlf = System.getProperty("line.separator");
        String message = "\nUsage: java -jar " + Version.getJarName(Convert.class) + " "+MethodHandles.lookup().lookupClass().getSimpleName()+" <email-as-key-id> <private-filename> <public-filename>" + crlf;
        System.err.println(message);
        System.exit(1);
    }

}
