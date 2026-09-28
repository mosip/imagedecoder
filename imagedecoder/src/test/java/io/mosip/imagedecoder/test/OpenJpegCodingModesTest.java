package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.CompressionContextInfo;
import io.mosip.imagedecoder.model.openjpeg.CompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.DecompressionContextInfo;
import io.mosip.imagedecoder.model.openjpeg.DecompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.JP2CodecFormat;
import io.mosip.imagedecoder.model.openjpeg.Jp2ColorSpace;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponentParameters;
import io.mosip.imagedecoder.openjpeg.CioHelper;
import io.mosip.imagedecoder.openjpeg.ImageHelper;
import io.mosip.imagedecoder.openjpeg.OpenJpegHelper;

/**
 * Code-block styles, SOP/EPH markers, signed / mixed-precision components and
 * decoder reduce / layer limits.
 */
class OpenJpegCodingModesTest {

	private OpenJpegHelper codec;

	@BeforeEach
	void setUp() {
		codec = new OpenJpegHelper();
	}

	@ParameterizedTest(name = "cblk style {0}")
	@ValueSource(ints = { OpenJpegConstant.J2K_CCP_CBLKSTY_LAZY, OpenJpegConstant.J2K_CCP_CBLKSTY_RESET,
			OpenJpegConstant.J2K_CCP_CBLKSTY_TERMALL, OpenJpegConstant.J2K_CCP_CBLKSTY_VSC,
			OpenJpegConstant.J2K_CCP_CBLKSTY_PTERM, OpenJpegConstant.J2K_CCP_CBLKSTY_SEGSYM,
			OpenJpegConstant.J2K_CCP_CBLKSTY_LAZY | OpenJpegConstant.J2K_CCP_CBLKSTY_TERMALL,
			OpenJpegConstant.J2K_CCP_CBLKSTY_LAZY | OpenJpegConstant.J2K_CCP_CBLKSTY_PTERM, 0x3f })
	void encodeDecodeCodeBlockStyles(int mode) {
		OpenJpegImage image = image(32, 32, 1, 8, 0);
		CompressionParameters params = baseParams();
		params.setMode(mode);
		byte[] encoded = encode(JP2CodecFormat.CODEC_J2K, image, params);
		assertTrue(encoded.length > 20);
		assertNotNull(decode(JP2CodecFormat.CODEC_J2K, encoded, 0, 0));
	}

	@ParameterizedTest(name = "coding style {0}")
	@ValueSource(ints = { OpenJpegConstant.J2K_CP_CSTY_SOP, OpenJpegConstant.J2K_CP_CSTY_EPH,
			OpenJpegConstant.J2K_CP_CSTY_SOP | OpenJpegConstant.J2K_CP_CSTY_EPH })
	void encodeDecodeSopEph(int style) {
		OpenJpegImage image = image(24, 24, 3, 8, 0);
		CompressionParameters params = baseParams();
		params.setCodingStyle(style);
		params.setTcpNoOfLayers(2);
		params.setTcpRates(new float[] { 20f, 0f });
		byte[] encoded = encode(JP2CodecFormat.CODEC_JP2, image, params);
		assertNotNull(decode(JP2CodecFormat.CODEC_JP2, encoded, 0, 0));
	}

	@ParameterizedTest(name = "prec {0} signed {1}")
	@CsvSource({ "12,0", "16,0", "8,1", "12,1", "4,0" })
	void encodeDecodePrecisionAndSign(int prec, int sgnd) {
		OpenJpegImage image = image(16, 16, 1, prec, sgnd);
		byte[] encoded = encode(JP2CodecFormat.CODEC_JP2, image, baseParams());
		OpenJpegImage decoded = decode(JP2CodecFormat.CODEC_JP2, encoded, 0, 0);
		assertNotNull(decoded);
		assertEquals(prec, decoded.getComps()[0].getPrec());
	}

	@Test
	void encodeMixedPrecisionWritesBpccBox() {
		OpenJpegImageComponentParameters[] parms = { component(16, 16, 8, 0), component(16, 16, 12, 0),
				component(16, 16, 8, 1) };
		OpenJpegImage image = ImageHelper.getInstance().imageCreate(3, parms, Jp2ColorSpace.CLRSPC_SRGB);
		frame(image, 16, 16);
		CompressionParameters params = baseParams();
		params.setTcpMct(0);
		byte[] encoded = encode(JP2CodecFormat.CODEC_JP2, image, params);
		assertNotNull(decode(JP2CodecFormat.CODEC_JP2, encoded, 0, 0));
	}

	@Test
	void encodeSubsampledComponents() {
		OpenJpegImageComponentParameters luma = component(32, 32, 8, 0);
		OpenJpegImageComponentParameters cb = component(16, 16, 8, 0);
		cb.setDx(2);
		cb.setDy(2);
		OpenJpegImageComponentParameters cr = component(16, 16, 8, 0);
		cr.setDx(2);
		cr.setDy(2);
		OpenJpegImage image = ImageHelper.getInstance().imageCreate(3,
				new OpenJpegImageComponentParameters[] { luma, cb, cr }, Jp2ColorSpace.CLRSPC_SYCC);
		frame(image, 32, 32);
		CompressionParameters params = baseParams();
		params.setTcpMct(0);
		byte[] encoded = encode(JP2CodecFormat.CODEC_JP2, image, params);
		assertNotNull(decode(JP2CodecFormat.CODEC_JP2, encoded, 0, 0));
	}

	@ParameterizedTest(name = "reduce {0} layer {1}")
	@CsvSource({ "1,0", "2,0", "0,1", "1,1", "0,2" })
	void decodeWithReduceAndLayerLimits(int reduce, int layer) {
		OpenJpegImage image = image(64, 64, 1, 8, 0);
		CompressionParameters params = baseParams();
		params.setNoOfResolution(4);
		params.setTcpNoOfLayers(3);
		params.setTcpRates(new float[] { 40f, 20f, 0f });
		byte[] encoded = encode(JP2CodecFormat.CODEC_J2K, image, params);
		OpenJpegImage decoded = decode(JP2CodecFormat.CODEC_J2K, encoded, reduce, layer);
		assertNotNull(decoded);
	}

	@Test
	void decodeReversibleWithIrreversibleSiblingTiles() {
		OpenJpegImage image = image(48, 48, 3, 8, 0);
		CompressionParameters params = baseParams();
		params.setIrreversible(1);
		params.setTileSizeOn(1);
		params.setCpTileDX(16);
		params.setCpTileDY(16);
		params.setMode(OpenJpegConstant.J2K_CCP_CBLKSTY_VSC | OpenJpegConstant.J2K_CCP_CBLKSTY_SEGSYM);
		byte[] encoded = encode(JP2CodecFormat.CODEC_J2K, image, params);
		assertTrue(encoded.length > 100);
	}

	private CompressionParameters baseParams() {
		CompressionParameters params = new CompressionParameters();
		codec.setDefaultEncodeParameters(params, false);
		params.setTcpNoOfLayers(1);
		params.setTcpRates(new float[] { 0f });
		return params;
	}

	private byte[] encode(JP2CodecFormat format, OpenJpegImage image, CompressionParameters params) {
		CompressionContextInfo cInfo = codec.createCompression(format);
		codec.setupEncoder(cInfo, params, image, false);
		Cio cio = CioHelper.getInstance().cioOpen(cInfo, null, 0);
		assertEquals(0, codec.encode(cInfo, cio, image, null, false), "encode failed");
		int len = CioHelper.getInstance().cioTell(cio);
		byte[] out = new byte[len];
		System.arraycopy(cio.getBuffer(), 0, out, 0, len);
		CioHelper.getInstance().cioClose(cio);
		codec.destroyCompression(cInfo);
		return out;
	}

	private OpenJpegImage decode(JP2CodecFormat format, byte[] data, int reduce, int layer) {
		DecompressionContextInfo dInfo = codec.createDecompression(format);
		DecompressionParameters params = new DecompressionParameters();
		codec.setDefaultDecoderParameters(params, false);
		params.setCpReduce(reduce);
		params.setCpLayer(layer);
		codec.setupDecoder(dInfo, params, false);
		Cio cio = CioHelper.getInstance().cioOpen(dInfo, data, data.length);
		OpenJpegImage image = codec.decode(dInfo, cio, false);
		CioHelper.getInstance().cioClose(cio);
		codec.destroyDecompression(dInfo);
		return image;
	}

	private static OpenJpegImage image(int w, int h, int comps, int prec, int sgnd) {
		OpenJpegImageComponentParameters[] parms = new OpenJpegImageComponentParameters[comps];
		for (int c = 0; c < comps; c++) {
			parms[c] = component(w, h, prec, sgnd);
		}
		OpenJpegImage image = ImageHelper.getInstance().imageCreate(comps, parms,
				comps == 1 ? Jp2ColorSpace.CLRSPC_GRAY : Jp2ColorSpace.CLRSPC_SRGB);
		frame(image, w, h);
		return image;
	}

	private static void frame(OpenJpegImage image, int w, int h) {
		image.setX0(0);
		image.setY0(0);
		image.setX1(w);
		image.setY1(h);
		for (int c = 0; c < image.getNoOfComps(); c++) {
			int prec = image.getComps()[c].getPrec();
			boolean signed = image.getComps()[c].getSgnd() != 0;
			int range = 1 << prec;
			int[] data = image.getComps()[c].getData();
			for (int i = 0; i < data.length; i++) {
				int v = (i * 31 + c * 7) % range;
				data[i] = signed ? v - range / 2 : v;
			}
		}
	}

	private static OpenJpegImageComponentParameters component(int w, int h, int prec, int sgnd) {
		OpenJpegImageComponentParameters p = new OpenJpegImageComponentParameters();
		p.setDx(1);
		p.setDy(1);
		p.setWidth(w);
		p.setHeight(h);
		p.setX0(0);
		p.setY0(0);
		p.setPrec(prec);
		p.setBpp(prec);
		p.setSgnd(sgnd);
		return p;
	}
}
