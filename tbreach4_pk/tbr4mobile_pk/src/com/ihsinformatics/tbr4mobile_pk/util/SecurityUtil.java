
package com.ihsinformatics.tbr4mobile_pk.util;

/**
 * This class provides encryption and decryption utility
 */

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author http://www.java2s.com
 * 
 */
public class SecurityUtil
{
	public static final String	ENCRYPTION_ALGORITHM	= "AES";
	public static final String	CHARACTER_SET			= "UTF-8";
	private static final int	BLOCKS					= 128;
	private static final String	KEY						= "TTetraNNitroTToluene";

	public static byte[] encrypt (String text)
	{
		try
		{
			byte[] rawKey = getRawKey (KEY.getBytes (CHARACTER_SET));
			SecretKeySpec skeySpec = new SecretKeySpec (rawKey, ENCRYPTION_ALGORITHM);
			Cipher cipher = Cipher.getInstance (ENCRYPTION_ALGORITHM);
			cipher.init (Cipher.ENCRYPT_MODE, skeySpec);
			return cipher.doFinal (text.getBytes (CHARACTER_SET));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace ();
		}
		catch (IllegalBlockSizeException e)
		{
			e.printStackTrace ();
		}
		catch (BadPaddingException e)
		{
			e.printStackTrace ();
		}
		catch (InvalidKeyException e)
		{
			e.printStackTrace ();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace ();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace ();
		}
		return null;
	}

	public static String decrypt (byte[] data)
	{
		try
		{
			byte[] rawKey = getRawKey (KEY.getBytes (CHARACTER_SET));
			SecretKeySpec skeySpec = new SecretKeySpec (rawKey, ENCRYPTION_ALGORITHM);
			Cipher cipher = Cipher.getInstance (ENCRYPTION_ALGORITHM);
			cipher.init (Cipher.DECRYPT_MODE, skeySpec);
			byte[] decrypted = cipher.doFinal (data);
			return new String (decrypted);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace ();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace ();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace ();
		}
		catch (InvalidKeyException e)
		{
			e.printStackTrace ();
		}
		catch (IllegalBlockSizeException e)
		{
			e.printStackTrace ();
		}
		catch (BadPaddingException e)
		{
			e.printStackTrace ();
		}
		return null;
	}

	private static byte[] getRawKey (byte[] seed)
	{
		try
		{
			KeyGenerator kgen = KeyGenerator.getInstance (ENCRYPTION_ALGORITHM);
			SecureRandom sr = SecureRandom.getInstance ("SHA1PRNG");
			sr.setSeed (seed);
			kgen.init (BLOCKS, sr);
			SecretKey skey = kgen.generateKey ();
			byte[] raw = skey.getEncoded ();
			return raw;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace ();
		}
		return null;
	}
}
