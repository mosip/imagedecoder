package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.mosip.imagedecoder.model.DecoderRequestInfo;
import io.mosip.imagedecoder.model.DecoderResponseInfo;
import io.mosip.imagedecoder.model.Response;
import io.mosip.imagedecoder.openjpeg.OpenJpegDecoder;
import io.mosip.imagedecoder.util.Base64UrlUtil;
import io.mosip.imagedecoder.wsq.WsqDecoder;

/**
 * Extra decode paths for JaCoCo 90% gate (bufferedImage=false, error paths).
 */
class DecoderCoverageTest {

	private static byte[] wsqBytes;
	private static byte[] jp2Bytes;

	@BeforeAll
	static void loadSamples() throws IOException {
		wsqBytes = Base64UrlUtil.getInstance().decodeURLSafeBase64(readResource("sample-wsq.b64"));
		jp2Bytes = Base64UrlUtil.getInstance().decodeURLSafeBase64(readResource("sample-jp2.b64"));
	}

	private static String readResource(String name) throws IOException {
		try (InputStream in = DecoderCoverageTest.class.getClassLoader().getResourceAsStream(name)) {
			assertNotNull(in, "missing test resource " + name);
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	@Test
	void wsqDecodeWithoutBufferedImage() {
		DecoderRequestInfo req = new DecoderRequestInfo();
		req.setImageData(wsqBytes);
		req.setBufferedImage(false);
		Response<DecoderResponseInfo> info = new WsqDecoder().decode(req);
		assertEquals(0, info.getStatusCode());
		assertNull(info.getResponse().getBufferedImage());
	}

	@Test
	void wsqDecodeInvalidData() {
		DecoderRequestInfo req = new DecoderRequestInfo();
		req.setImageData(new byte[] { 1, 2, 3, 4 });
		req.setBufferedImage(false);
		Response<DecoderResponseInfo> info = new WsqDecoder().decode(req);
		assertNotEquals(0, info.getStatusCode());
	}

	@Test
	void jp2DecodeWithoutBufferedImage() {
		DecoderRequestInfo req = new DecoderRequestInfo();
		req.setImageData(jp2Bytes);
		req.setBufferedImage(false);
		Response<DecoderResponseInfo> info = new OpenJpegDecoder().decode(req);
		assertEquals(0, info.getStatusCode());
		assertNull(info.getResponse().getBufferedImage());
	}

	@Test
	void jp2DecodeInvalidData() {
		DecoderRequestInfo req = new DecoderRequestInfo();
		req.setImageData(new byte[] { 0, 1, 2, 3 });
		req.setBufferedImage(false);
		Response<DecoderResponseInfo> info = new OpenJpegDecoder().decode(req);
		assertNotEquals(0, info.getStatusCode());
	}

	@Test
	void base64DecodeByteArrayRoundTrip() {
		byte[] raw = "coverage".getBytes(StandardCharsets.UTF_8);
		String enc = Base64UrlUtil.getInstance().encodeToURLSafeBase64(raw);
		byte[] encBytes = enc.getBytes(StandardCharsets.UTF_8);
		assertEquals("coverage",
				new String(Base64UrlUtil.getInstance().decodeURLSafeBase64(encBytes), StandardCharsets.UTF_8));
	}
}
