package org.apache.pdfbox.tools.encrypt;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DecryptTest {

	public String decrypt(String publicKeyStr, String secret, String encryptedToken) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException
	{
	        final Cipher cipher = Cipher.getInstance("RSA");
	        cipher.init(Cipher.DECRYPT_MODE, castStringToPublicKey(publicKeyStr));

	        String password = new String(cipher.doFinal(Base64.getDecoder().decode(secret)));

	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

	        final SecretKey secretKey = new SecretKeySpec(encodedHash, "AES");
	        Cipher decoder = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        decoder.init(Cipher.DECRYPT_MODE, secretKey);

	        return new String(decoder.doFinal(Base64.getDecoder().decode(encryptedToken)));

	    }

	private PublicKey castStringToPublicKey(String publicKeyStrPkcs8) throws InvalidKeySpecException, NoSuchAlgorithmException {
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        String publicKeyStr = publicKeyStrPkcs8
	                .replace("-----BEGIN PUBLIC KEY-----", "")
	                .replace("-----END PUBLIC KEY-----", "")
	                .replaceAll("\\s+", "");

	        byte[] publicKey = Base64.getDecoder().decode((publicKeyStr));
	        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
	        return keyFactory.generatePublic(publicKeySpec);
	    }
}
