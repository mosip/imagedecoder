package io.mosip.imagedecoder.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;

import io.mosip.imagedecoder.constant.DecoderErrorCodes;
import io.mosip.imagedecoder.exceptions.DecoderException;

public class Base64UrlUtil {
	private static Encoder urlSafeEncoder;

	static {
		urlSafeEncoder = Base64.getUrlEncoder().withoutPadding();
	}

	// Static variable reference of singleInstance of type Singleton
    private static Base64UrlUtil singleInstance = null;    
	private Base64UrlUtil()
	{ 
		super ();
	} 
  
	//synchronized method to control simultaneous access 
	public static synchronized Base64UrlUtil getInstance()
	{ 
		if (singleInstance == null)
			singleInstance = new Base64UrlUtil();
  
        return singleInstance;
	}
	
	public String encodeToURLSafeBase64(byte[] data) {
		if (isNullEmpty(data)) {
			return null;
		}
		return urlSafeEncoder.encodeToString(data);
	}

	public String encodeToURLSafeBase64(String data) {
		if (isNullEmpty(data)) {
			return null;
		}
		return urlSafeEncoder.encodeToString(data.getBytes(StandardCharsets.UTF_8));
	}

	public byte[] decodeURLSafeBase64(byte[] data) {
		if (isNullEmpty(data)) {
			throw new DecoderException(DecoderErrorCodes.TECHNICAL_ERROR_EXCEPTION.getErrorCode(), "null");
		}
		return Base64.getUrlDecoder().decode(data);
	}

	public byte[] decodeURLSafeBase64(String data) {
		if (isNullEmpty(data)) {
			throw new DecoderException(DecoderErrorCodes.TECHNICAL_ERROR_EXCEPTION.getErrorCode(), "null");
		}
		return Base64.getUrlDecoder().decode(data);
	}

	public boolean isNullEmpty(byte[] array) {
		return array == null || array.length == 0;
	}

	public boolean isNullEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
}
