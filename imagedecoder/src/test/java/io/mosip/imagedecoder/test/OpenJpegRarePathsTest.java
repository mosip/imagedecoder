package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.CompressionContextInfo;
import io.mosip.imagedecoder.model.openjpeg.CompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.DecompressionContextInfo;
import io.mosip.imagedecoder.model.openjpeg.DecompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.JP2CodecFormat;
import io.mosip.imagedecoder.model.openjpeg.Jp2ColorSpace;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponentParameters;
import io.mosip.imagedecoder.model.openjpeg.Poc;
import io.mosip.imagedecoder.model.openjpeg.ProgressionOrder;
import io.mosip.imagedecoder.openjpeg.CioHelper;
import io.mosip.imagedecoder.openjpeg.ImageHelper;
import io.mosip.imagedecoder.openjpeg.OpenJpegHelper;

/**
 * Rare OpenJPEG encoder options: fixed-layer allocation, progression order
 * changes (POC), tile-part flags, JPWL EPC and odd-sized irreversible DWT.
 */
class OpenJpegRarePathsTest {

	private OpenJpegHelper codec;

	@BeforeEach
	void setUp() {
		codec = new OpenJpegHelper();
	}

	@Test
	void encodeFixedLayerAllocationWithMatrix() {
		OpenJpegImage image = gray(32, 32);
		CompressionParameters params = baseParams();
		int layers = 2;
		int resolutions = 3;
		params.setNoOfResolution(resolutions);
		params.setTcpNoOfLayers(layers);
		params.setTcpRates(new float[] { 0f, 0f });
		params.setCpDistortionAllocation(0);
		params.setCpFixedQuality(0);
		params.setCpFixedAllocation(1);
		int[] matrice = new int[layers * resolutions * 3];
		for (int i = 0; i < matrice.length; i++) {
			matrice[i] = i < resolutions * 3 ? 4 : 8;
		}
		params.setCpMatrice(matrice);
		byte[] encoded = encode(JP2CodecFormat.CODEC_J2K, image, params, false);
		assertTrue(encoded.length > 20);
		assertNotNull(decode(JP2CodecFormat.CODEC_J2K, encoded, false));
	}

	@Test
	void encodeWithProgressionOrderChanges() {
		OpenJpegImage image = rgb(24, 24);
		CompressionParameters params = baseParams();
		params.setNoOfResolution(3);
		params.setTcpNoOfLayers(2);
		params.setTcpRates(new float[] { 20f, 0f });
		params.setNoOfPocs(2);
		params.getPocs()[0] = poc(0, 0, 1, 2, 3, ProgressionOrder.LRCP);
		params.getPocs()[1] = poc(2, 0, 2, 3, 3, ProgressionOrder.RLCP);
		// Decoding POC codestreams from this encoder is not yet supported by the port.
		byte[] encoded = encode(JP2CodecFormat.CODEC_J2K, image, params, false);
		assertTrue(encoded.length > 20);
	}

	@ParameterizedTest(name = "tile-part flag {0} with {1}, encode only")
	@CsvSource({ "R,RPCL", "L,RLCP", "R,LRCP", "C,CPRL", "C,PCRL" })
	void encodeTilePartsByFlagEncodeOnly(char flag, ProgressionOrder order) {
		OpenJpegImage image = rgb(32, 32);
		CompressionParameters params = baseParams();
		params.setNoOfResolution(3);
		params.setTcpNoOfLayers(2);
		params.setTcpRates(new float[] { 30f, 0f });
		params.setTpOn(1);
		params.setTpFlag(flag);
		params.setProgressionOrder(order);
		assertTrue(encode(JP2CodecFormat.CODEC_J2K, image, params, false).length > 20);
	}

	@ParameterizedTest(name = "tile-part flag {0} with {1}")
	@CsvSource({ "P,PCRL" })
	void encodeTilePartsByFlag(char flag, ProgressionOrder order) {
		OpenJpegImage image = rgb(32, 32);
		CompressionParameters params = baseParams();
		params.setNoOfResolution(3);
		params.setTcpNoOfLayers(2);
		params.setTcpRates(new float[] { 30f, 0f });
		params.setTpOn(1);
		params.setTpFlag(flag);
		params.setProgressionOrder(order);
		byte[] encoded = encode(JP2CodecFormat.CODEC_J2K, image, params, false);
		assertTrue(encoded.length > 20);
		assertNotNull(decode(JP2CodecFormat.CODEC_J2K, encoded, false));
	}

	@Test
	void encodeJpwlWithEpcProtection() {
		OpenJpegImage image = gray(16, 16);
		CompressionParameters params = new CompressionParameters();
		codec.setDefaultEncodeParameters(params, true);
		params.setTcpNoOfLayers(1);
		params.setTcpRates(new float[] { 0f });
		params.setJpwlEpcOn(1);
		params.setJpwlHprotMH(1);
		params.getJpwlPprot()[0] = 1;
		params.setJpwlSensSize(1);
		params.setJpwlSensMH(1);
		byte[] encoded = encode(JP2CodecFormat.CODEC_J2K, image, params, true);
		assertTrue(encoded.length > 20);
	}

	@ParameterizedTest(name = "irreversible {0}x{1}")
	@CsvSource({ "33,17", "17,33", "7,5", "1,9", "9,1" })
	void encodeOddSizedIrreversible(int w, int h) {
		OpenJpegImage image = gray(w, h);
		CompressionParameters params = baseParams();
		params.setIrreversible(1);
		params.setNoOfResolution(3);
		byte[] encoded = encode(JP2CodecFormat.CODEC_J2K, image, params, false);
		assertTrue(encoded.length > 10);
		OpenJpegImage decoded = decode(JP2CodecFormat.CODEC_J2K, encoded, false);
		assertNotNull(decoded);
		assertEquals(w, decoded.getComps()[0].getWidth());
	}

	@ParameterizedTest(name = "reversible {0}x{1}")
	@CsvSource({ "33,17", "7,5", "1,9", "9,1" })
	void encodeOddSizedReversible(int w, int h) {
		OpenJpegImage image = gray(w, h);
		CompressionParameters params = baseParams();
		params.setNoOfResolution(3);
		byte[] encoded = encode(JP2CodecFormat.CODEC_J2K, image, params, false);
		OpenJpegImage decoded = decode(JP2CodecFormat.CODEC_J2K, encoded, false);
		assertNotNull(decoded);
		assertEquals(h, decoded.getComps()[0].getHeight());
	}

	private static Poc poc(int resNo0, int compNo0, int layNo1, int resNo1, int compNo1, ProgressionOrder order) {
		Poc poc = new Poc();
		poc.setResNo0(resNo0);
		poc.setCompNo0(compNo0);
		poc.setLayNo1(layNo1);
		poc.setResNo1(resNo1);
		poc.setCompNo1(compNo1);
		poc.setProgressionOrder1(order);
		poc.setTile(-1);
		return poc;
	}

	private CompressionParameters baseParams() {
		CompressionParameters params = new CompressionParameters();
		codec.setDefaultEncodeParameters(params, false);
		params.setTcpNoOfLayers(1);
		params.setTcpRates(new float[] { 0f });
		return params;
	}

	private byte[] encode(JP2CodecFormat format, OpenJpegImage image, CompressionParameters params, boolean jpwl) {
		CompressionContextInfo cInfo = codec.createCompression(format);
		codec.setupEncoder(cInfo, params, image, jpwl);
		Cio cio = CioHelper.getInstance().cioOpen(cInfo, null, 0);
		int r = codec.encode(cInfo, cio, image, null, jpwl);
		assertEquals(0, r, "encode failed for " + format);
		int len = CioHelper.getInstance().cioTell(cio);
		byte[] out = new byte[len];
		System.arraycopy(cio.getBuffer(), 0, out, 0, len);
		CioHelper.getInstance().cioClose(cio);
		codec.destroyCompression(cInfo);
		return out;
	}

	private OpenJpegImage decode(JP2CodecFormat format, byte[] data, boolean jpwl) {
		DecompressionContextInfo dInfo = codec.createDecompression(format);
		DecompressionParameters params = new DecompressionParameters();
		codec.setDefaultDecoderParameters(params, jpwl);
		codec.setupDecoder(dInfo, params, jpwl);
		Cio cio = CioHelper.getInstance().cioOpen(dInfo, data, data.length);
		OpenJpegImage image = codec.decode(dInfo, cio, jpwl);
		CioHelper.getInstance().cioClose(cio);
		codec.destroyDecompression(dInfo);
		return image;
	}

	private static OpenJpegImage gray(int w, int h) {
		OpenJpegImageComponentParameters[] parms = { component(w, h) };
		OpenJpegImage image = ImageHelper.getInstance().imageCreate(1, parms, Jp2ColorSpace.CLRSPC_GRAY);
		image.setX0(0);
		image.setY0(0);
		image.setX1(w);
		image.setY1(h);
		fill(image.getComps()[0].getData());
		return image;
	}

	private static OpenJpegImage rgb(int w, int h) {
		OpenJpegImageComponentParameters[] parms = { component(w, h), component(w, h), component(w, h) };
		OpenJpegImage image = ImageHelper.getInstance().imageCreate(3, parms, Jp2ColorSpace.CLRSPC_SRGB);
		image.setX0(0);
		image.setY0(0);
		image.setX1(w);
		image.setY1(h);
		for (int c = 0; c < 3; c++) {
			fill(image.getComps()[c].getData());
		}
		return image;
	}

	private static OpenJpegImageComponentParameters component(int w, int h) {
		OpenJpegImageComponentParameters p = new OpenJpegImageComponentParameters();
		p.setDx(1);
		p.setDy(1);
		p.setWidth(w);
		p.setHeight(h);
		p.setX0(0);
		p.setY0(0);
		p.setPrec(8);
		p.setBpp(8);
		p.setSgnd(0);
		return p;
	}

	private static void fill(int[] data) {
		for (int i = 0; i < data.length; i++) {
			data[i] = (i * 13) % 256;
		}
	}
}
