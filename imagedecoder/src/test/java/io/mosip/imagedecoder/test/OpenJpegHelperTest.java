package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.mosip.imagedecoder.model.openjpeg.CodeStreamInfo;
import io.mosip.imagedecoder.model.openjpeg.CompressionContextInfo;
import io.mosip.imagedecoder.model.openjpeg.CompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.DecompressionContextInfo;
import io.mosip.imagedecoder.model.openjpeg.DecompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.JP2CodecFormat;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponent;
import io.mosip.imagedecoder.openjpeg.OpenJpegHelper;

class OpenJpegHelperTest {

	private OpenJpegHelper helper;

	@BeforeEach
	void setUp() {
		helper = new OpenJpegHelper();
	}

	@Test
	void versionIsNonEmpty() {
		assertNotNull(helper.version());
		assertEquals(false, helper.version().isBlank());
	}

	@Test
	void createAndDestroyDecompressionJp2() {
		DecompressionContextInfo dInfo = helper.createDecompression(JP2CodecFormat.CODEC_JP2);
		assertNotNull(dInfo);
		assertEquals(JP2CodecFormat.CODEC_JP2, dInfo.getContextInfo().getCodecFormat());
		assertDoesNotThrow(() -> helper.destroyDecompression(dInfo));
		assertDoesNotThrow(() -> helper.destroyDecompression(null));
	}

	@Test
	void createDecompressionJ2kAndJpt() {
		assertNotNull(helper.createDecompression(JP2CodecFormat.CODEC_J2K));
		assertNotNull(helper.createDecompression(JP2CodecFormat.CODEC_JPT));
	}

	@Test
	void createDecompressionUnknownReturnsNull() {
		assertNull(helper.createDecompression(JP2CodecFormat.CODEC_UNKNOWN));
	}

	@Test
	void setDefaultDecoderParameters() {
		DecompressionParameters params = new DecompressionParameters();
		helper.setDefaultDecoderParameters(params, false);
		assertEquals(0, params.getCpLayer());
		assertEquals(0, params.getCpReduce());
		helper.setDefaultDecoderParameters(params, true);
		assertEquals(0, params.getJpwlCorrect());
		assertDoesNotThrow(() -> helper.setDefaultDecoderParameters(null, true));
	}

	@Test
	void setupDecoderJp2() {
		DecompressionContextInfo dInfo = helper.createDecompression(JP2CodecFormat.CODEC_JP2);
		DecompressionParameters params = new DecompressionParameters();
		helper.setDefaultDecoderParameters(params, false);
		assertDoesNotThrow(() -> helper.setupDecoder(dInfo, params, false));
		assertDoesNotThrow(() -> helper.setupDecoder(null, params, false));
		assertDoesNotThrow(() -> helper.setupDecoder(dInfo, null, false));
		helper.destroyDecompression(dInfo);
	}

	@Test
	void createAndDestroyCompressionJ2k() {
		CompressionContextInfo cInfo = helper.createCompression(JP2CodecFormat.CODEC_J2K);
		assertNotNull(cInfo);
		assertDoesNotThrow(() -> helper.destroyCompression(cInfo));
		assertNull(helper.createCompression(JP2CodecFormat.CODEC_JPT));
	}

	@Test
	void setupEncoderAndEncodeNullSafe() {
		CompressionContextInfo cInfo = helper.createCompression(JP2CodecFormat.CODEC_JP2);
		CompressionParameters params = new CompressionParameters();
		helper.setDefaultEncodeParameters(params, false);
		OpenJpegImage image = new OpenJpegImage();
		image.setNoOfComps(1);
		OpenJpegImageComponent comp = new OpenJpegImageComponent();
		comp.setWidth(8);
		comp.setHeight(8);
		comp.setData(new int[64]);
		image.setComps(new OpenJpegImageComponent[] { comp });
		// setupEncoder needs a fully initialized codec handle — null args stay safe
		assertDoesNotThrow(() -> helper.setupEncoder(null, params, image, false));
		assertDoesNotThrow(() -> helper.setupEncoder(cInfo, null, image, false));
		assertDoesNotThrow(() -> helper.setupEncoder(cInfo, params, null, false));
		assertDoesNotThrow(() -> helper.encode(null, null, null, null, false));
		assertDoesNotThrow(() -> helper.encode(cInfo, null, image, new char[] { 'x' }, false));
		assertDoesNotThrow(() -> helper.encodeWithInfo(null, null, null, null, false));
		helper.destroyCompression(cInfo);
	}

	@Test
	void decodeWithInfoAndDestroyCodeStreamInfo() {
		DecompressionContextInfo dInfo = helper.createDecompression(JP2CodecFormat.CODEC_J2K);
		DecompressionParameters params = new DecompressionParameters();
		helper.setDefaultDecoderParameters(params, true);
		helper.setupDecoder(dInfo, params, true);
		assertNull(helper.decodeWithInfo(dInfo, null, null, false));
		CodeStreamInfo csi = new CodeStreamInfo();
		assertDoesNotThrow(() -> helper.destroyCodeStreamInfo(csi));
		helper.destroyDecompression(dInfo);

		DecompressionContextInfo jpt = helper.createDecompression(JP2CodecFormat.CODEC_JPT);
		helper.setupDecoder(jpt, params, false);
		helper.destroyDecompression(jpt);
	}

	@Test
	void setDefaultEncodeParameters() {
		CompressionParameters params = new CompressionParameters();
		helper.setDefaultEncodeParameters(params, false);
		assertDoesNotThrow(() -> helper.setDefaultEncodeParameters(null, true));
		helper.setDefaultEncodeParameters(params, true);
	}

	@Test
	void destroyCodeStreamInfoNullSafe() {
		assertDoesNotThrow(() -> helper.destroyCodeStreamInfo(null));
	}
}
