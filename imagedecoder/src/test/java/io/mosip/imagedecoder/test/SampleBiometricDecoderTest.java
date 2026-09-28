package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.mosip.imagedecoder.model.DecoderRequestInfo;
import io.mosip.imagedecoder.model.DecoderResponseInfo;
import io.mosip.imagedecoder.model.Response;
import io.mosip.imagedecoder.openjpeg.OpenJpegDecoder;
import io.mosip.imagedecoder.wsq.WsqDecoder;

/**
 * Decodes sample biometric JP2/WSQ fixtures to exercise codec helpers beyond
 * the single embedded base64 samples.
 */
class SampleBiometricDecoderTest {

	static Stream<String> jp2Resources() {
		return Stream.of(
				"biometric/info_face_auth.jp2",
				"biometric/info_face_registration.jp2",
				"biometric/info_finger_left_index_auth.jp2",
				"biometric/info_finger_left_index_registration.jp2",
				"biometric/info_iris_left_auth.jp2",
				"biometric/info_iris_left_registration.jp2");
	}

	static Stream<String> wsqResources() {
		return Stream.of(
				"biometric/info_finger_left_thumb_auth.wsq",
				"biometric/Left_Index.wsq",
				"biometric/Left_Little.wsq",
				"biometric/Left_Middle.wsq",
				"biometric/Left_Ring.wsq",
				"biometric/Left_Thumb.wsq",
				"biometric/Right_Index.wsq",
				"biometric/Right_Little.wsq",
				"biometric/Right_Ring.wsq",
				"biometric/Right_Thumb.wsq");
	}

	@ParameterizedTest(name = "JP2 {0}")
	@MethodSource("jp2Resources")
	void decodeJp2Sample(String resource) throws IOException {
		byte[] data = readResource(resource);
		DecoderRequestInfo req = new DecoderRequestInfo();
		req.setImageData(data);
		req.setBufferedImage(true);
		Response<DecoderResponseInfo> info = new OpenJpegDecoder().decode(req);
		assertEquals(0, info.getStatusCode(), () -> "JP2 decode failed for " + resource + ": " + info.getStatusMessage());
		assertNotNull(info.getResponse());
		assertTrue(Integer.parseInt(info.getResponse().getImageWidth()) > 0);
		assertTrue(Integer.parseInt(info.getResponse().getImageHeight()) > 0);
		assertNotNull(info.getResponse().getBufferedImage());
	}

	@ParameterizedTest(name = "WSQ {0}")
	@MethodSource("wsqResources")
	void decodeWsqSample(String resource) throws IOException {
		byte[] data = readResource(resource);
		DecoderRequestInfo req = new DecoderRequestInfo();
		req.setImageData(data);
		req.setBufferedImage(true);
		Response<DecoderResponseInfo> info = new WsqDecoder().decode(req);
		assertEquals(0, info.getStatusCode(), () -> "WSQ decode failed for " + resource + ": " + info.getStatusMessage());
		assertNotNull(info.getResponse());
		assertTrue(Integer.parseInt(info.getResponse().getImageWidth()) > 0);
		assertTrue(Integer.parseInt(info.getResponse().getImageHeight()) > 0);
		assertNotNull(info.getResponse().getBufferedImage());
	}

	private static byte[] readResource(String name) throws IOException {
		try (InputStream in = SampleBiometricDecoderTest.class.getClassLoader().getResourceAsStream(name)) {
			assertNotNull(in, "missing test resource " + name);
			return in.readAllBytes();
		}
	}
}
