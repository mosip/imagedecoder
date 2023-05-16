package io.mosip.imagedecoder.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;

public class Base64UrlUtil {
	private static Encoder urlSafeEncoder;

	static {
		urlSafeEncoder = Base64.getUrlEncoder().withoutPadding();
	}

	public static String encodeToURLSafeBase64(byte[] data) {
		if (isNullEmpty(data)) {
			return null;
		}
		return urlSafeEncoder.encodeToString(data);
	}

	public static String encodeToURLSafeBase64(String data) {
		if (isNullEmpty(data)) {
			return null;
		}
		return urlSafeEncoder.encodeToString(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] decodeURLSafeBase64(byte[] data) {
		if (isNullEmpty(data)) {
			return null;
		}
		return Base64.getUrlDecoder().decode(data);
	}

	public static byte[] decodeURLSafeBase64(String data) {
		if (isNullEmpty(data)) {
			return null;
		}
		return Base64.getUrlDecoder().decode(data);
	}

	public static boolean isNullEmpty(byte[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isNullEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
}
