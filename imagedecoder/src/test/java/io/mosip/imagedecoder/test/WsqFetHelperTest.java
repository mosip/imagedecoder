package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import io.mosip.imagedecoder.model.ByteBufferContext;
import io.mosip.imagedecoder.model.wsq.WsqFet;
import io.mosip.imagedecoder.model.wsq.WsqHeaderForm;
import io.mosip.imagedecoder.util.ByteStreamUtil;
import io.mosip.imagedecoder.wsq.WsqFetHelper;
import io.mosip.imagedecoder.wsq.WsqFetInfoHelper;

/**
 * Covers NIST FET helpers previously excluded from JaCoCo.
 */
class WsqFetHelperTest {

	@Test
	void singleton() {
		assertSame(WsqFetHelper.getInstance(), WsqFetHelper.getInstance());
		assertSame(WsqFetInfoHelper.getInstance(), WsqFetInfoHelper.getInstance());
	}

	@Test
	void allocUpdateExtractAndString2Fet() {
		WsqFetHelper helper = WsqFetHelper.getInstance();
		WsqFet fet = helper.allocFet(8);
		assertNotNull(fet);
		assertEquals(0, fet.getNum());

		assertEquals(0, helper.updateFet("PPI".toCharArray(), "500".toCharArray(), fet));
		StringBuilder value = new StringBuilder();
		assertEquals(0, helper.extractFet(value, WsqConstant.NCM_PPI.toCharArray(), fet));

		char[] data = "LOSSY 1\nCOLORSPACE GRAY\n\0".toCharArray();
		assertEquals(0, helper.string2fet(fet, data));
	}

	@Test
	void fetInfoHelperNullNistcomDefaults() {
		WsqFetInfoHelper info = WsqFetInfoHelper.getInstance();

		int[] ppi = new int[1];
		assertEquals(0, info.getWsqPPI(null, ppi));
		assertEquals(-1, ppi[0]);

		int[] lossy = new int[1];
		assertEquals(0, info.getWsqLossyFlag(null, lossy));
		assertEquals(1, lossy[0]);

		double[] bitRate = new double[1];
		assertEquals(0, info.getWsqBitRate(null, bitRate));
		assertEquals(0.0, bitRate[0], 0.001);

		StringBuilder color = new StringBuilder();
		assertEquals(0, info.getWsqColorSpace(null, color));
	}

	@Test
	void fetInfoHelperWithPopulatedFet() {
		WsqFetHelper helper = WsqFetHelper.getInstance();
		WsqFet fet = helper.allocFet(8);
		helper.updateFet(WsqConstant.NCM_PPI.toCharArray(), "500".toCharArray(), fet);
		helper.updateFet(WsqConstant.NCM_LOSSY.toCharArray(), "0".toCharArray(), fet);
		helper.updateFet(WsqConstant.NCM_WSQ_RATE.toCharArray(), "0.75".toCharArray(), fet);
		helper.updateFet(WsqConstant.NCM_COLORSPACE.toCharArray(), "GRAY".toCharArray(), fet);

		WsqFetInfoHelper info = WsqFetInfoHelper.getInstance();
		int[] ppi = new int[1];
		assertEquals(0, info.getWsqPPI(fet, ppi));
		assertEquals(500, ppi[0]);

		int[] lossy = new int[1];
		assertEquals(0, info.getWsqLossyFlag(fet, lossy));
		assertEquals(0, lossy[0]);

		double[] bitRate = new double[1];
		assertEquals(0, info.getWsqBitRate(fet, bitRate));

		StringBuilder color = new StringBuilder();
		assertEquals(0, info.getWsqColorSpace(fet, color));
		assertEquals("GRAY", color.toString());
	}

	@Test
	void getWsqFrameHeaderFromBuffer() {
		// Minimal synthetic SOF frame payload matching getWsqFrameHeader reads
		byte[] buf = new byte[] {
				0, 17, // hdrSize
				0, // black
				(byte) 255, // white
				0, 10, // height
				0, 10, // width
				0, // scale mShift
				0, 100, // mShift short
				0, // scale rScale
				0, 100, // rScale short
				1, // encoder
				0, 1 // software
		};
		ByteBufferContext ctx = new ByteBufferContext();
		ByteStreamUtil.getInstance().init(ctx, buf, buf.length);
		WsqHeaderForm header = new WsqHeaderForm();
		assertEquals(0, WsqFetInfoHelper.getInstance().getWsqFrameHeader(header, ctx));
		assertEquals(10, header.getWidth());
		assertEquals(10, header.getHeight());
	}

	@Test
	void getNistCommentsDoesNotThrowOnRandomBytes() {
		WsqFet nistcom = WsqFetHelper.getInstance().allocFet(8);
		byte[] data = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };
		assertDoesNotThrow(() -> WsqFetInfoHelper.getInstance().getNistComments(nistcom, data, data.length));
	}
}
