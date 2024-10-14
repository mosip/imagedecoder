package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.mosip.imagedecoder.constant.DecoderConstant;
import io.mosip.imagedecoder.constant.DecoderErrorCodes;
import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import io.mosip.imagedecoder.constant.wsq.WsqErrorCode;

class ConstantTest {
	@Test
	void testDecoderConstant() {
		assertEquals("DECODER", DecoderConstant.LOGGER_SESSIONID);
		assertEquals("DECODER", DecoderConstant.LOGGER_IDTYPE);
	}

	@Test
	void testDecoderConstantPrivateConstructor() {
		Exception exception = assertThrows(IllegalStateException.class, () -> {
			new DecoderConstant(); // This should throw an exception
		});
		assertEquals("DecoderConstant class", exception.getMessage());
	}

	@Test
	void testDecoderErrorCodesGetErrorCode() {
		assertEquals("TOOLKIT_SUCCESS_000", DecoderErrorCodes.SUCCESS.getErrorCode());
		assertEquals("DECODER_ERR_001", DecoderErrorCodes.INVALID_DATA_ERROR.getErrorCode());
		assertEquals("DECODER_ERR_002", DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorCode());
		assertEquals("DECODER_ERR_003", DecoderErrorCodes.BUFFEREDIMAGE_ALLOCATION_FAILED_ERROR.getErrorCode());
		assertEquals("DECODER_REQ_ERR_500", DecoderErrorCodes.TECHNICAL_ERROR_EXCEPTION.getErrorCode());
	}

	@Test
	void testDecoderErrorCodesGetErrorMessage() {
		assertEquals("Success", DecoderErrorCodes.SUCCESS.getErrorMessage());
		assertEquals("Invalid Data Error.", DecoderErrorCodes.INVALID_DATA_ERROR.getErrorMessage());
		assertEquals("Unsupported format.", DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorMessage());
		assertEquals("BufferedImage allocation failed.",
				DecoderErrorCodes.BUFFEREDIMAGE_ALLOCATION_FAILED_ERROR.getErrorMessage());
		assertEquals("Technical Error", DecoderErrorCodes.TECHNICAL_ERROR_EXCEPTION.getErrorMessage());
	}

	@Test
	void testDecoderErrorCodesFromErrorCode_ValidCodes() {
		assertEquals(DecoderErrorCodes.SUCCESS, DecoderErrorCodes.fromErrorCode("TOOLKIT_SUCCESS_000"));
		assertEquals(DecoderErrorCodes.INVALID_DATA_ERROR, DecoderErrorCodes.fromErrorCode("DECODER_ERR_001"));
		assertEquals(DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR, DecoderErrorCodes.fromErrorCode("DECODER_ERR_002"));
		assertEquals(DecoderErrorCodes.BUFFEREDIMAGE_ALLOCATION_FAILED_ERROR,
				DecoderErrorCodes.fromErrorCode("DECODER_ERR_003"));
	}

	@Test
	void testDecoderErrorCodesFromErrorCode_InvalidCode() {
		// Test invalid code returns TECHNICAL_ERROR_EXCEPTION
		assertEquals(DecoderErrorCodes.TECHNICAL_ERROR_EXCEPTION, DecoderErrorCodes.fromErrorCode("INVALID_CODE"));
	}

	@Test
	void testWsqErrorCodeGetErrorId() {
		assertEquals(-1, WsqErrorCode.NON_COMPLIANT_WITH_WSQ_SPECS.getErrorId());
		assertEquals(-2, WsqErrorCode.EMPTY_STRING_FOUND.getErrorId());
		assertEquals(-3, WsqErrorCode.NO_DATA_TO_READ.getErrorId());
		assertEquals(-500, WsqErrorCode.TECHNICAL_ERROR_EXCEPTION.getErrorId());
	}

	@Test
	void testWsqErrorCodeGetErrorCode() {
		assertEquals("MOS-EXT-1", WsqErrorCode.NON_COMPLIANT_WITH_WSQ_SPECS.getErrorCode());
		assertEquals("MOS-EXT-2", WsqErrorCode.EMPTY_STRING_FOUND.getErrorCode());
		assertEquals("MOS-EXT-3", WsqErrorCode.NO_DATA_TO_READ.getErrorCode());
		assertEquals("MOS-EXT-500", WsqErrorCode.TECHNICAL_ERROR_EXCEPTION.getErrorCode());
	}

	@Test
	void testWsqErrorCodeGetErrorMessage() {
		assertEquals(
				"A code in the hufftable contains an : all 1's code. This image may still be  decodable. It is not compliant with the WSQ specification.",
				WsqErrorCode.NON_COMPLIANT_WITH_WSQ_SPECS.getErrorMessage());
		assertEquals("Empty name string found.", WsqErrorCode.EMPTY_STRING_FOUND.getErrorMessage());
		assertEquals("No huffman table bytes remaining.", WsqErrorCode.NO_DATA_TO_READ.getErrorMessage());
		assertEquals("Technical Error", WsqErrorCode.TECHNICAL_ERROR_EXCEPTION.getErrorMessage());
	}

	@Test
	void testWsqErrorCodeFromErrorCode_Valid() {
		assertEquals(WsqErrorCode.NON_COMPLIANT_WITH_WSQ_SPECS, WsqErrorCode.fromErrorCode("MOS-EXT-1"));
		assertEquals(WsqErrorCode.EMPTY_STRING_FOUND, WsqErrorCode.fromErrorCode("MOS-EXT-2"));
	}

	@Test
	void testWsqErrorCodeFromErrorCode_Invalid() {
		// Test invalid code returns TECHNICAL_ERROR_EXCEPTION
		assertEquals(WsqErrorCode.TECHNICAL_ERROR_EXCEPTION, WsqErrorCode.fromErrorCode("INVALID_CODE"));
	}

	@Test
	void testWsqConstantWsqMarkers() {
		assertEquals(0xffa0, WsqConstant.SOI_WSQ);
		assertEquals(0xffa1, WsqConstant.EOI_WSQ);
		assertEquals(0xffa2, WsqConstant.SOF_WSQ);
		assertEquals(0xffa3, WsqConstant.SOB_WSQ);
		assertEquals(0xffa4, WsqConstant.DTT_WSQ);
		assertEquals(0xffa5, WsqConstant.DQT_WSQ);
		assertEquals(0xffa6, WsqConstant.DHT_WSQ);
		assertEquals(0xffa7, WsqConstant.DRT_WSQ);
		assertEquals(0xffa8, WsqConstant.COM_WSQ);
		assertEquals(0xffff, WsqConstant.ANY_WSQ);
	}

	@Test
	void testWsqConstantSubbandDefinitions() {
		assertEquals(19, WsqConstant.STRT_SUBBAND_2);
		assertEquals(52, WsqConstant.STRT_SUBBAND_3);
		assertEquals(64, WsqConstant.MAX_SUBBANDS);
		assertEquals(60, WsqConstant.NUM_SUBBANDS);
		assertEquals(60, WsqConstant.STRT_SUBBAND_DEL);
		assertEquals(4, WsqConstant.STRT_SIZE_REGION_2);
		assertEquals(51, WsqConstant.STRT_SIZE_REGION_3);
	}

	@Test
	void testWsqConstantImageConstants() {
		assertEquals(256, WsqConstant.MIN_IMG_DIM);
		assertEquals(255, WsqConstant.WHITE);
		assertEquals(0, WsqConstant.BLACK);
		assertEquals(1, WsqConstant.RAW_IMAGE);
		assertEquals(0, WsqConstant.IHEAD_IMAGE);
		assertEquals(1.01f, WsqConstant.VARIANCE_THRESH);
	}

	@Test
	void testWsqConstantCompressionConstants() {
		assertEquals("ncm", WsqConstant.NCM_EXT);
		assertEquals("NIST_COM", WsqConstant.NCM_HEADER);
		assertEquals("PIX_WIDTH", WsqConstant.NCM_PIX_WIDTH);
		assertEquals("PIX_HEIGHT", WsqConstant.NCM_PIX_HEIGHT);
		assertEquals("PIX_DEPTH", WsqConstant.NCM_PIX_DEPTH);
		assertEquals("PPI", WsqConstant.NCM_PPI);
		assertEquals("COLORSPACE", WsqConstant.NCM_COLORSPACE);
		assertEquals("NUM_COMPONENTS", WsqConstant.NCM_N_CMPNTS);
		assertEquals("HV_FACTORS", WsqConstant.NCM_HV_FCTRS);
	}

	@Test
	void testWsqConstantErrorConstants() {
		assertNotEquals(0, WsqConstant.AVERROR_INVALIDDATA);
	}
}