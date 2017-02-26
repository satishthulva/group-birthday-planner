/*
 * Crypto.java
 *
 * Product: SCG
 * Strand Clinical Genomics
 *
 * Copyright 2007-2013, Strand Life Sciences
 * 5th Floor, Kirloskar Business Park, 
 * Bellary Road, Hebbal,
 * Bangalore 560024
 * India
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Strand Life Sciences., ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Strand Life Sciences.
 */
package com.novice.hbdr;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * used for obfuscation
 * 
 * @author satishbabu
 */
public class Crypto 
{
	public static final String encode(String data)
    {
    	if(data == null) return null;
    	
    	try
    	{
    		Key key = new SecretKeySpec(new byte[]{47,97,13,17,37,29,71,19},"DES");
	   	 	// Create the cipher
	        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	        // Initialize the cipher for encryption
	        desCipher.init(Cipher.ENCRYPT_MODE, key);
	        // Encrypt the cleartext
	        return toHexString(desCipher.doFinal(data.getBytes()));
    	}
    	catch(Exception ex)
    	{}
    	
    	return null;
    }
	    
    public static final String decode(String data)
    {
    	if(data == null) return null;
    	try
    	{
    		Key key = new SecretKeySpec(new byte[]{47,97,13,17,37,29,71,19},"DES");
	   	 	// Create the cipher
	        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	        // Initialize the cipher for encryption
	        desCipher.init(Cipher.DECRYPT_MODE, key);
	        // deEncrypt the cleartext
	        byte[] decryptedData = desCipher.doFinal(hexToByteArray(data));
	        return new String(decryptedData, "UTF-8");
    	}
    	catch(Exception ex)
    	{}
    	
    	return null;
    }
    
   public static String toHexString(byte[] value) {
        
        if(value == null) return null;

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < value.length; i++) {
            int ithValue = ((int) value[i]) & 0xFF;

            int lowerByte = ithValue & 0xF;
            int higherByte = ithValue >> 4 & 0xF;

            buffer.append(Integer.toHexString(higherByte));
            buffer.append(Integer.toHexString(lowerByte)); // lower case only
        }
        
        return buffer.toString();
    }
    
    public static byte[] hexToByteArray(String hexString) 
    {
        char[] charBuffer = hexString.toCharArray();
        if (charBuffer.length % 2 != 0) 
        {
            throw new IllegalArgumentException("not a valid hex dump, even length expected, found " + charBuffer.length);
        }

        byte[] bytes = new byte[charBuffer.length / 2];
        for (int i = 0, j = 0; j < bytes.length; j++) 
        {
            String jthByte = new String(charBuffer, i, 2);
            bytes[j] = (byte) Integer.parseInt(jthByte, 16);
            i += 2;
        }

        return bytes;
    }
    
    public static void main(String[] args)
    {
    	System.out.println(encode(args[0]));
    }
}
