package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.model.ByteBufferContext;
import io.mosip.imagedecoder.model.DecoderRequestInfo;
import io.mosip.imagedecoder.model.DecoderResponseInfo;
import io.mosip.imagedecoder.model.Response;
import io.mosip.imagedecoder.model.openjpeg.Bio;
import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.CodeStreamInfo;
import io.mosip.imagedecoder.model.openjpeg.CodecContextInfo;
import io.mosip.imagedecoder.model.openjpeg.CodingParameters;
import io.mosip.imagedecoder.model.openjpeg.CompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.ContextInfo;
import io.mosip.imagedecoder.model.openjpeg.DecoderFunctionInfo;
import io.mosip.imagedecoder.model.openjpeg.DecompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.Dwt;
import io.mosip.imagedecoder.model.openjpeg.DwtV4;
import io.mosip.imagedecoder.model.openjpeg.J2K;
import io.mosip.imagedecoder.model.openjpeg.J2KProgressionOrder;
import io.mosip.imagedecoder.model.openjpeg.J2KT2Mode;
import io.mosip.imagedecoder.model.openjpeg.J2kStatus;
import io.mosip.imagedecoder.model.openjpeg.JP2;
import io.mosip.imagedecoder.model.openjpeg.JP2Box;
import io.mosip.imagedecoder.model.openjpeg.JP2CinemeaMode;
import io.mosip.imagedecoder.model.openjpeg.JP2CodecFormat;
import io.mosip.imagedecoder.model.openjpeg.JP2Component;
import io.mosip.imagedecoder.model.openjpeg.JP2ResolutionBox;
import io.mosip.imagedecoder.model.openjpeg.JPTMessageHeader;
import io.mosip.imagedecoder.model.openjpeg.Jp2ColorSpace;
import io.mosip.imagedecoder.model.openjpeg.LimitDecoding;
import io.mosip.imagedecoder.model.openjpeg.MQCoder;
import io.mosip.imagedecoder.model.openjpeg.MQCoderState;
import io.mosip.imagedecoder.model.openjpeg.MarkerInfo;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponent;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponentParameters;
import io.mosip.imagedecoder.model.openjpeg.PacketInfo;
import io.mosip.imagedecoder.model.openjpeg.PiComponent;
import io.mosip.imagedecoder.model.openjpeg.PiIterator;
import io.mosip.imagedecoder.model.openjpeg.PiResolution;
import io.mosip.imagedecoder.model.openjpeg.Poc;
import io.mosip.imagedecoder.model.openjpeg.ProgressionOrder;
import io.mosip.imagedecoder.model.openjpeg.Raw;
import io.mosip.imagedecoder.model.openjpeg.RsizCapabilities;
import io.mosip.imagedecoder.model.openjpeg.StepSize;
import io.mosip.imagedecoder.model.openjpeg.Tcd;
import io.mosip.imagedecoder.model.openjpeg.TcdBand;
import io.mosip.imagedecoder.model.openjpeg.TcdCodeBlockDecoder;
import io.mosip.imagedecoder.model.openjpeg.TcdCodeBlockEncoder;
import io.mosip.imagedecoder.model.openjpeg.TcdImage;
import io.mosip.imagedecoder.model.openjpeg.TcdLayer;
import io.mosip.imagedecoder.model.openjpeg.TcdPass;
import io.mosip.imagedecoder.model.openjpeg.TcdPrecinct;
import io.mosip.imagedecoder.model.openjpeg.TcdResolution;
import io.mosip.imagedecoder.model.openjpeg.TcdSegment;
import io.mosip.imagedecoder.model.openjpeg.TcdTile;
import io.mosip.imagedecoder.model.openjpeg.TcdTileComponent;
import io.mosip.imagedecoder.model.openjpeg.Tcp;
import io.mosip.imagedecoder.model.openjpeg.TgtNode;
import io.mosip.imagedecoder.model.openjpeg.TgtTree;
import io.mosip.imagedecoder.model.openjpeg.Tier1;
import io.mosip.imagedecoder.model.openjpeg.Tier2;
import io.mosip.imagedecoder.model.openjpeg.TileComponentCodingParameters;
import io.mosip.imagedecoder.model.openjpeg.TileInfo;
import io.mosip.imagedecoder.model.openjpeg.TpInfo;
import io.mosip.imagedecoder.model.openjpeg.V4;
import io.mosip.imagedecoder.model.wsq.WsqFet;
import io.mosip.imagedecoder.model.wsq.WsqHeaderForm;
import io.mosip.imagedecoder.model.wsq.WsqHuffCode;
import io.mosip.imagedecoder.model.wsq.WsqInfo;
import io.mosip.imagedecoder.model.wsq.WsqQuantization;
import io.mosip.imagedecoder.model.wsq.WsqQuantizationTree;
import io.mosip.imagedecoder.model.wsq.WsqTableDht;
import io.mosip.imagedecoder.model.wsq.WsqTableDqt;
import io.mosip.imagedecoder.model.wsq.WsqTableDtt;
import io.mosip.imagedecoder.model.wsq.WsqWavletTree;

class ModelTest {
	@Test
	void testDecoderResponseInfoEqualsAndHashCode() {
		DecoderResponseInfo info1 = new DecoderResponseInfo();
		DecoderResponseInfo info2 = new DecoderResponseInfo();

		info1.setImageType("WSQ");
		info1.setImageWidth("1024");
		info1.setImageHeight("768");
		info1.setImageLossless("0");
		info1.setImageDepth("8");
		info1.setImageDpiHorizontal("500");
		info1.setImageDpiVertical("500");
		info1.setImageBitRate("1.2");
		info1.setImageSize("512KB");
		info1.setImageData("base64encodeddata");
		info1.setImageColorSpace("GRAY");
		info1.setImageAspectRatio("4:3");
		info1.setImageCompressionRatio("15:1");
		info1.setAllInfo(true);

		info2.setImageType("WSQ");
		info2.setImageWidth("1024");
		info2.setImageHeight("768");
		info2.setImageLossless("0");
		info2.setImageDepth("8");
		info2.setImageDpiHorizontal("500");
		info2.setImageDpiVertical("500");
		info2.setImageBitRate("1.2");
		info2.setImageSize("512KB");
		info2.setImageData("base64encodeddata");
		info2.setImageColorSpace("GRAY");
		info2.setImageAspectRatio("4:3");
		info2.setImageCompressionRatio("15:1");
		info2.setAllInfo(true);

		// Test equality
		assertEquals(info1, info2);
		assertEquals(info1.hashCode(), info2.hashCode());

		// Test inequality
		info2.setImageType("JPEG2000");
		assertNotEquals(info1, info2);
	}

	@Test
	void testDecoderResponseInfoCanEqual() {
		DecoderResponseInfo info1 = new DecoderResponseInfo();
		DecoderResponseInfo info2 = new DecoderResponseInfo();
		assertTrue(info1.canEqual(info2));
	}

	@Test
	void testDecoderRequestInfoEqualsAndHashCode() {
		DecoderRequestInfo request1 = new DecoderRequestInfo();
		DecoderRequestInfo request2 = new DecoderRequestInfo();

		byte[] imageData = { 1, 2, 3, 4, 5 };
		request1.setImageData(imageData);
		request1.setBufferedImage(true);
		request1.setAllInfo(true);

		request2.setImageData(imageData);
		request2.setBufferedImage(true);
		request2.setAllInfo(true);

		// Test equality
		assertEquals(request1, request2);
		assertEquals(request1.hashCode(), request2.hashCode());

		// Test inequality
		byte[] differentData = { 6, 7, 8 };
		request2.setImageData(differentData);
		assertNotEquals(request1, request2);
	}

	@Test
	void testDecoderRequestInfoCanEqual() {
		DecoderRequestInfo request1 = new DecoderRequestInfo();
		DecoderRequestInfo request2 = new DecoderRequestInfo();
		assertTrue(request1.canEqual(request2));
	}

	@Test
	void testResponseEqualsAndHashCode() {
		Response<String> response1 = new Response<>();
		Response<String> response2 = new Response<>();

		response1.setStatusCode(200);
		response1.setStatusMessage("OK");
		response1.setResponse("Success");

		response2.setStatusCode(200);
		response2.setStatusMessage("OK");
		response2.setResponse("Success");

		// Test equality
		assertEquals(response1, response2);
		assertEquals(response1.hashCode(), response2.hashCode());

		// Test inequality
		response2.setResponse("Failure");
		assertNotEquals(response1, response2);
	}

	@Test
	void testResponseCanEqual() {
		Response<String> response = new Response<>();
		assertTrue(response.canEqual(new Response<>()));
		assertFalse(response.canEqual(new Object()));
	}

	@Test
	void testByteBufferContextEqualsAndHashCode() {
		ByteBufferContext context1 = new ByteBufferContext();
		ByteBufferContext context2 = new ByteBufferContext();

		ByteBuffer buffer1 = ByteBuffer.allocate(10);
		buffer1.put((byte) 1);

		ByteBuffer buffer2 = ByteBuffer.allocate(10);
		buffer2.put((byte) 1);

		context1.setBuffer(buffer1);
		context2.setBuffer(buffer2);

		// Test equality (same content)
		assertEquals(context1, context2);
		assertEquals(context1.hashCode(), context2.hashCode());

		// Modify buffer2 and test inequality
		buffer2.put((byte) 2);
		assertNotEquals(context1, context2);
	}

	@Test
	void testByteBufferContextCanEqual() {
		ByteBufferContext context = new ByteBufferContext();
		assertTrue(context.canEqual(new ByteBufferContext()));
		assertFalse(context.canEqual(new Object()));
	}

	@Test
	void testWsqFetEquals_SameObject() {
		WsqFet fet = new WsqFet();
		assertTrue(fet.equals(fet), "Should be equal to itself");
	}

	@Test
	void testWsqFetEquals_EqualObjects() {
		WsqFet fet1 = new WsqFet();
		fet1.setNum(1);
		fet1.setAlloc(10);
		fet1.setNames(new String[] { "name1", "name2" });
		fet1.setValues(new String[] { "value1", "value2" });

		WsqFet fet2 = new WsqFet();
		fet2.setNum(1);
		fet2.setAlloc(10);
		fet2.setNames(new String[] { "name1", "name2" });
		fet2.setValues(new String[] { "value1", "value2" });

		assertTrue(fet1.equals(fet2), "Two equal objects should be equal");
	}

	@Test
	void testWsqFetEquals_NonEqualObjects() {
		WsqFet fet1 = new WsqFet();
		fet1.setNum(1);
		fet1.setAlloc(10);
		fet1.setNames(new String[] { "name1", "name2" });
		fet1.setValues(new String[] { "value1", "value2" });

		WsqFet fet2 = new WsqFet();
		fet2.setNum(1);
		fet2.setAlloc(20); // Different alloc
		fet2.setNames(new String[] { "name1", "name2" });
		fet2.setValues(new String[] { "value1", "value2" });

		assertFalse(fet1.equals(fet2), "Different objects should not be equal");
	}

	@Test
	void testWsqFetHashCode_EqualObjects() {
		WsqFet fet1 = new WsqFet();
		fet1.setNum(1);
		fet1.setAlloc(10);
		fet1.setNames(new String[] { "name1", "name2" });
		fet1.setValues(new String[] { "value1", "value2" });

		WsqFet fet2 = new WsqFet();
		fet2.setNum(1);
		fet2.setAlloc(10);
		fet2.setNames(new String[] { "name1", "name2" });
		fet2.setValues(new String[] { "value1", "value2" });

		assertEquals(fet1.hashCode(), fet2.hashCode(), "Equal objects should have the same hash code");
	}

	@Test
	void testWsqFetHashCode_NonEqualObjects() {
		WsqFet fet1 = new WsqFet();
		fet1.setNum(1);
		fet1.setAlloc(10);
		fet1.setNames(new String[] { "name1", "name2" });
		fet1.setValues(new String[] { "value1", "value2" });

		WsqFet fet2 = new WsqFet();
		fet2.setNum(2); // Different num
		fet2.setAlloc(10);
		fet2.setNames(new String[] { "name1", "name2" });
		fet2.setValues(new String[] { "value1", "value2" });

		assertNotEquals(fet1.hashCode(), fet2.hashCode(), "Non-equal objects should have different hash codes");
	}

	@Test
	void testWsqFetCanEqual() {
		WsqFet fet = new WsqFet();
		assertTrue(fet.canEqual(new WsqFet()), "Should be able to equal another WsqFet");
		assertFalse(fet.canEqual(new Object()), "Should not be able to equal a non-WsqFet");
	}

	@Test
	void testWsqHeaderFormEquals_SameObject() {
		WsqHeaderForm header = new WsqHeaderForm();
		assertTrue(header.equals(header), "Should be equal to itself");
	}

	@Test
	void testWsqHeaderFormEquals_EqualObjects() {
		WsqHeaderForm header1 = new WsqHeaderForm();
		header1.setBlack(1);
		header1.setWhite(2);
		header1.setWidth(3);
		header1.setHeight(4);
		header1.setMShift(new float[] { 1.1f });
		header1.setRScale(new float[] { 2.2f });
		header1.setWsqEncoder(5);
		header1.setSoftware(6L);

		WsqHeaderForm header2 = new WsqHeaderForm();
		header2.setBlack(1);
		header2.setWhite(2);
		header2.setWidth(3);
		header2.setHeight(4);
		header2.setMShift(new float[] { 1.1f });
		header2.setRScale(new float[] { 2.2f });
		header2.setWsqEncoder(5);
		header2.setSoftware(6L);

		assertTrue(header1.equals(header2), "Two equal objects should be equal");
	}

	@Test
	void testWsqHeaderFormEquals_NonEqualObjects() {
		WsqHeaderForm header1 = new WsqHeaderForm();
		header1.setBlack(1);
		header1.setWhite(2);
		header1.setWidth(3);
		header1.setHeight(4);
		header1.setMShift(new float[] { 1.1f });
		header1.setRScale(new float[] { 2.2f });
		header1.setWsqEncoder(5);
		header1.setSoftware(6L);

		WsqHeaderForm header2 = new WsqHeaderForm();
		header2.setBlack(1);
		header2.setWhite(2);
		header2.setWidth(3);
		header2.setHeight(5); // Different height
		header2.setMShift(new float[] { 1.1f });
		header2.setRScale(new float[] { 2.2f });
		header2.setWsqEncoder(5);
		header2.setSoftware(6L);

		assertFalse(header1.equals(header2), "Different objects should not be equal");
	}

	@Test
	void testWsqHeaderFormHashCode_EqualObjects() {
		WsqHeaderForm header1 = new WsqHeaderForm();
		header1.setBlack(1);
		header1.setWhite(2);
		header1.setWidth(3);
		header1.setHeight(4);
		header1.setMShift(new float[] { 1.1f });
		header1.setRScale(new float[] { 2.2f });
		header1.setWsqEncoder(5);
		header1.setSoftware(6L);

		WsqHeaderForm header2 = new WsqHeaderForm();
		header2.setBlack(1);
		header2.setWhite(2);
		header2.setWidth(3);
		header2.setHeight(4);
		header2.setMShift(new float[] { 1.1f });
		header2.setRScale(new float[] { 2.2f });
		header2.setWsqEncoder(5);
		header2.setSoftware(6L);

		assertEquals(header1.hashCode(), header2.hashCode(), "Equal objects should have the same hash code");
	}

	@Test
	void testWsqHeaderFormHashCode_NonEqualObjects() {
		WsqHeaderForm header1 = new WsqHeaderForm();
		header1.setBlack(1);
		header1.setWhite(2);
		header1.setWidth(3);
		header1.setHeight(4);
		header1.setMShift(new float[] { 1.1f });
		header1.setRScale(new float[] { 2.2f });
		header1.setWsqEncoder(5);
		header1.setSoftware(6L);

		WsqHeaderForm header2 = new WsqHeaderForm();
		header2.setBlack(1);
		header2.setWhite(2);
		header2.setWidth(3);
		header2.setHeight(4);
		header2.setMShift(new float[] { 1.1f });
		header2.setRScale(new float[] { 2.2f });
		header2.setWsqEncoder(6); // Different encoder

		assertNotEquals(header1.hashCode(), header2.hashCode(), "Non-equal objects should have different hash codes");
	}

	@Test
	void testWsqHeaderFormCanEqual() {
		WsqHeaderForm header = new WsqHeaderForm();
		assertTrue(header.canEqual(new WsqHeaderForm()), "Should be able to equal another WsqHeaderForm");
		assertFalse(header.canEqual(new Object()), "Should not be able to equal a non-WsqHeaderForm");
	}

	@Test
	void testWsqHuffCodeEquals_SameObject() {
		WsqHuffCode huffCode = new WsqHuffCode();
		assertTrue(huffCode.equals(huffCode), "Should be equal to itself");
	}

	@Test
	void testWsqHuffCodeEquals_EqualObjects() {
		WsqHuffCode code1 = new WsqHuffCode();
		code1.setSize(5);
		code1.setCode(123456L);

		WsqHuffCode code2 = new WsqHuffCode();
		code2.setSize(5);
		code2.setCode(123456L);

		assertTrue(code1.equals(code2), "Two equal objects should be equal");
	}

	@Test
	void testWsqHuffCodeEquals_NonEqualObjects() {
		WsqHuffCode code1 = new WsqHuffCode();
		code1.setSize(5);
		code1.setCode(123456L);

		WsqHuffCode code2 = new WsqHuffCode();
		code2.setSize(6); // Different size
		code2.setCode(123456L);

		assertFalse(code1.equals(code2), "Different objects should not be equal");
	}

	@Test
	void testWsqHuffCodeHashCode_EqualObjects() {
		WsqHuffCode code1 = new WsqHuffCode();
		code1.setSize(5);
		code1.setCode(123456L);

		WsqHuffCode code2 = new WsqHuffCode();
		code2.setSize(5);
		code2.setCode(123456L);

		assertEquals(code1.hashCode(), code2.hashCode(), "Equal objects should have the same hash code");
	}

	@Test
	void testWsqHuffCodeHashCode_NonEqualObjects() {
		WsqHuffCode code1 = new WsqHuffCode();
		code1.setSize(5);
		code1.setCode(123456L);

		WsqHuffCode code2 = new WsqHuffCode();
		code2.setSize(6); // Different size

		assertNotEquals(code1.hashCode(), code2.hashCode(), "Non-equal objects should have different hash codes");
	}

	@Test
	void testWsqHuffCodeCanEqual() {
		WsqHuffCode huffCode = new WsqHuffCode();
		assertTrue(huffCode.canEqual(new WsqHuffCode()), "Should be able to equal another WsqHuffCode");
		assertFalse(huffCode.canEqual(new Object()), "Should not be able to equal a non-WsqHuffCode");
	}

	@Test
	void testWsqInfoEquals_SameObject() {
		WsqInfo info = new WsqInfo();
		assertTrue(info.equals(info), "Should be equal to itself");
	}

	@Test
	void testWsqInfoEquals_EqualObjects() {
		WsqInfo info1 = new WsqInfo();
		info1.setWidth(100);
		info1.setHeight(200);
		info1.setDepth(8);
		info1.setPpi(300);
		info1.setLossyFlag(0);
		info1.setBitRate(0.5);
		info1.setColorSpace("RGB");
		info1.setData(new byte[] { 1, 2, 3 });

		WsqInfo info2 = new WsqInfo();
		info2.setWidth(100);
		info2.setHeight(200);
		info2.setDepth(8);
		info2.setPpi(300);
		info2.setLossyFlag(0);
		info2.setBitRate(0.5);
		info2.setColorSpace("RGB");
		info2.setData(new byte[] { 1, 2, 3 });

		assertTrue(info1.equals(info2), "Two equal objects should be equal");
	}

	@Test
	void testWsqInfoEquals_NonEqualObjects() {
		WsqInfo info1 = new WsqInfo();
		info1.setWidth(100);
		info1.setHeight(200);
		info1.setDepth(8);
		info1.setPpi(300);
		info1.setLossyFlag(0);
		info1.setBitRate(0.5);
		info1.setColorSpace("RGB");
		info1.setData(new byte[] { 1, 2, 3 });

		WsqInfo info2 = new WsqInfo();
		info2.setWidth(100);
		info2.setHeight(200);
		info2.setDepth(8);
		info2.setPpi(300);
		info2.setLossyFlag(1); // Different lossyFlag

		assertFalse(info1.equals(info2), "Different objects should not be equal");
	}

	@Test
	void testWsqInfoHashCode_EqualObjects() {
		WsqInfo info1 = new WsqInfo();
		info1.setWidth(100);
		info1.setHeight(200);
		info1.setDepth(8);
		info1.setPpi(300);
		info1.setLossyFlag(0);
		info1.setBitRate(0.5);
		info1.setColorSpace("RGB");
		info1.setData(new byte[] { 1, 2, 3 });

		WsqInfo info2 = new WsqInfo();
		info2.setWidth(100);
		info2.setHeight(200);
		info2.setDepth(8);
		info2.setPpi(300);
		info2.setLossyFlag(0);
		info2.setBitRate(0.5);
		info2.setColorSpace("RGB");
		info2.setData(new byte[] { 1, 2, 3 });

		assertEquals(info1.hashCode(), info2.hashCode(), "Equal objects should have the same hash code");
	}

	@Test
	void testWsqInfoHashCode_NonEqualObjects() {
		WsqInfo info1 = new WsqInfo();
		info1.setWidth(100);
		info1.setHeight(200);
		info1.setDepth(8);
		info1.setPpi(300);
		info1.setLossyFlag(0);
		info1.setBitRate(0.5);
		info1.setColorSpace("RGB");
		info1.setData(new byte[] { 1, 2, 3 });

		WsqInfo info2 = new WsqInfo();
		info2.setWidth(100);
		info2.setHeight(200);
		info2.setDepth(8);
		info2.setPpi(300);
		info2.setLossyFlag(1); // Different lossyFlag

		assertNotEquals(info1.hashCode(), info2.hashCode(), "Non-equal objects should have different hash codes");
	}

	@Test
	void testWsqInfoCanEqual() {
		WsqInfo info = new WsqInfo();
		assertTrue(info.canEqual(new WsqInfo()), "Should be able to equal another WsqInfo");
		assertFalse(info.canEqual(new Object()), "Should not be able to equal a non-WsqInfo");
	}

	@Test
	void testWsqQuantizationEquals_SameObject() {
		WsqQuantization quantization = new WsqQuantization();
		assertTrue(quantization.equals(quantization), "Should be equal to itself");
	}

	@Test
	void testWsqQuantizationEquals_EqualObjects() {
		WsqQuantization quantization1 = new WsqQuantization();
		quantization1.setQuantizationLevel(1.0f);
		quantization1.setCompressionRatio(2.0f);
		quantization1.setCompressionBitRate(3.0f);
		quantization1.setQbssT(new float[] { 0.1f, 0.2f });
		quantization1.setQbss(new float[] { 0.3f, 0.4f });
		quantization1.setQzbs(new float[] { 0.5f, 0.6f });
		quantization1.setVar(new float[] { 0.7f, 0.8f });

		WsqQuantization quantization2 = new WsqQuantization();
		quantization2.setQuantizationLevel(1.0f);
		quantization2.setCompressionRatio(2.0f);
		quantization2.setCompressionBitRate(3.0f);
		quantization2.setQbssT(new float[] { 0.1f, 0.2f });
		quantization2.setQbss(new float[] { 0.3f, 0.4f });
		quantization2.setQzbs(new float[] { 0.5f, 0.6f });
		quantization2.setVar(new float[] { 0.7f, 0.8f });

		assertTrue(quantization1.equals(quantization2), "Two equal objects should be equal");
	}

	@Test
	void testWsqQuantizationEquals_NonEqualObjects() {
		WsqQuantization quantization1 = new WsqQuantization();
		quantization1.setQuantizationLevel(1.0f);
		quantization1.setCompressionRatio(2.0f);
		quantization1.setCompressionBitRate(3.0f);

		WsqQuantization quantization2 = new WsqQuantization();
		quantization2.setQuantizationLevel(1.1f); // Different value

		assertFalse(quantization1.equals(quantization2), "Different objects should not be equal");
	}

	@Test
	void testWsqQuantizationHashCode_EqualObjects() {
		WsqQuantization quantization1 = new WsqQuantization();
		quantization1.setQuantizationLevel(1.0f);
		quantization1.setCompressionRatio(2.0f);
		quantization1.setCompressionBitRate(3.0f);
		quantization1.setQbssT(new float[] { 0.1f, 0.2f });
		quantization1.setQbss(new float[] { 0.3f, 0.4f });
		quantization1.setQzbs(new float[] { 0.5f, 0.6f });
		quantization1.setVar(new float[] { 0.7f, 0.8f });

		WsqQuantization quantization2 = new WsqQuantization();
		quantization2.setQuantizationLevel(1.0f);
		quantization2.setCompressionRatio(2.0f);
		quantization2.setCompressionBitRate(3.0f);
		quantization2.setQbssT(new float[] { 0.1f, 0.2f });
		quantization2.setQbss(new float[] { 0.3f, 0.4f });
		quantization2.setQzbs(new float[] { 0.5f, 0.6f });
		quantization2.setVar(new float[] { 0.7f, 0.8f });

		assertEquals(quantization1.hashCode(), quantization2.hashCode(),
				"Equal objects should have the same hash code");
	}

	@Test
	void testWsqQuantizationHashCode_NonEqualObjects() {
		WsqQuantization quantization1 = new WsqQuantization();
		quantization1.setQuantizationLevel(1.0f);

		WsqQuantization quantization2 = new WsqQuantization();
		quantization2.setQuantizationLevel(1.1f); // Different value

		assertNotEquals(quantization1.hashCode(), quantization2.hashCode(),
				"Non-equal objects should have different hash codes");
	}

	@Test
	void testWsqQuantizationCanEqual() {
		WsqQuantization quantization = new WsqQuantization();
		assertTrue(quantization.canEqual(new WsqQuantization()), "Should be able to equal another WsqQuantization");
		assertFalse(quantization.canEqual(new Object()), "Should not be able to equal a non-WsqQuantization");
	}

	@Test
	void testWsqQuantizationTreeEquals_SameObject() {
		WsqQuantizationTree quantizationTree = new WsqQuantizationTree();
		assertTrue(quantizationTree.equals(quantizationTree), "Should be equal to itself");
	}

	@Test
	void testWsqQuantizationTreeEquals_EqualObjects() {
		WsqQuantizationTree tree1 = new WsqQuantizationTree();
		tree1.setX((short) 1);
		tree1.setY((short) 2);
		tree1.setLenX((short) 3);
		tree1.setLenY((short) 4);

		WsqQuantizationTree tree2 = new WsqQuantizationTree();
		tree2.setX((short) 1);
		tree2.setY((short) 2);
		tree2.setLenX((short) 3);
		tree2.setLenY((short) 4);

		assertTrue(tree1.equals(tree2), "Two equal objects should be equal");
	}

	@Test
	void testWsqQuantizationTreeEquals_NonEqualObjects() {
		WsqQuantizationTree tree1 = new WsqQuantizationTree();
		tree1.setX((short) 1);

		WsqQuantizationTree tree2 = new WsqQuantizationTree();
		tree2.setX((short) 2); // Different value

		assertFalse(tree1.equals(tree2), "Different objects should not be equal");
	}

	@Test
	void testWsqQuantizationTreeHashCode_EqualObjects() {
		WsqQuantizationTree tree1 = new WsqQuantizationTree();
		tree1.setX((short) 1);
		tree1.setY((short) 2);
		tree1.setLenX((short) 3);
		tree1.setLenY((short) 4);

		WsqQuantizationTree tree2 = new WsqQuantizationTree();
		tree2.setX((short) 1);
		tree2.setY((short) 2);
		tree2.setLenX((short) 3);
		tree2.setLenY((short) 4);

		assertEquals(tree1.hashCode(), tree2.hashCode(), "Equal objects should have the same hash code");
	}

	@Test
	void testWsqQuantizationTreeHashCode_NonEqualObjects() {
		WsqQuantizationTree tree1 = new WsqQuantizationTree();
		tree1.setX((short) 1);

		WsqQuantizationTree tree2 = new WsqQuantizationTree();
		tree2.setX((short) 2); // Different value

		assertNotEquals(tree1.hashCode(), tree2.hashCode(), "Non-equal objects should have different hash codes");
	}

	@Test
	void testWsqQuantizationTreeCanEqual() {
		WsqQuantizationTree tree = new WsqQuantizationTree();
		assertTrue(tree.canEqual(new WsqQuantizationTree()), "Should be able to equal another WsqQuantizationTree");
		assertFalse(tree.canEqual(new Object()), "Should not be able to equal a non-WsqQuantizationTree");
	}

	@Test
	void testWsqTableDhtEquals_SameObject() {
		WsqTableDht tableDht = new WsqTableDht();
		assertTrue(tableDht.equals(tableDht), "Should be equal to itself");
	}

	@Test
	void testWsqTableDhtEquals_EqualObjects() {
		WsqTableDht tableDht1 = new WsqTableDht();
		tableDht1.setTableDef(1);
		tableDht1.setHuffBits(new int[] { 1, 2, 3 });
		tableDht1.setHuffValues(new int[] { 1, 2, 3, 4 });

		WsqTableDht tableDht2 = new WsqTableDht();
		tableDht2.setTableDef(1);
		tableDht2.setHuffBits(new int[] { 1, 2, 3 });
		tableDht2.setHuffValues(new int[] { 1, 2, 3, 4 });

		assertTrue(tableDht1.equals(tableDht2), "Two equal objects should be equal");
	}

	@Test
	void testWsqTableDhtEquals_NonEqualObjects() {
		WsqTableDht tableDht1 = new WsqTableDht();
		tableDht1.setTableDef(1);

		WsqTableDht tableDht2 = new WsqTableDht();
		tableDht2.setTableDef(2); // Different value

		assertFalse(tableDht1.equals(tableDht2), "Different objects should not be equal");
	}

	@Test
	void testWsqTableDhtHashCode_EqualObjects() {
		WsqTableDht tableDht1 = new WsqTableDht();
		tableDht1.setTableDef(1);
		tableDht1.setHuffBits(new int[] { 1, 2, 3 });
		tableDht1.setHuffValues(new int[] { 1, 2, 3, 4 });

		WsqTableDht tableDht2 = new WsqTableDht();
		tableDht2.setTableDef(1);
		tableDht2.setHuffBits(new int[] { 1, 2, 3 });
		tableDht2.setHuffValues(new int[] { 1, 2, 3, 4 });

		assertEquals(tableDht1.hashCode(), tableDht2.hashCode(), "Equal objects should have the same hash code");
	}

	@Test
	void testWsqTableDhtHashCode_NonEqualObjects() {
		WsqTableDht tableDht1 = new WsqTableDht();
		tableDht1.setTableDef(1);

		WsqTableDht tableDht2 = new WsqTableDht();
		tableDht2.setTableDef(2); // Different value

		assertNotEquals(tableDht1.hashCode(), tableDht2.hashCode(),
				"Non-equal objects should have different hash codes");
	}

	@Test
	void testWsqTableDhtCanEqual() {
		WsqTableDht tableDht = new WsqTableDht();
		assertTrue(tableDht.canEqual(new WsqTableDht()), "Should be able to equal another WsqTableDht");
		assertFalse(tableDht.canEqual(new Object()), "Should not be able to equal a non-WsqTableDht");
	}

	@Test
	void testWsqTableDqtEquals_SameObject() {
		WsqTableDqt tableDqt = new WsqTableDqt();
		assertTrue(tableDqt.equals(tableDqt), "Should be equal to itself");
	}

	@Test
	void testWsqTableDqtEquals_EqualObjects() {
		WsqTableDqt tableDqt1 = new WsqTableDqt();
		tableDqt1.setBinCenter(1.5f);
		tableDqt1.setQBin(new float[] { 1.0f, 2.0f, 3.0f });
		tableDqt1.setZBin(new float[] { 4.0f, 5.0f, 6.0f });
		tableDqt1.setDqtDef(2);

		WsqTableDqt tableDqt2 = new WsqTableDqt();
		tableDqt2.setBinCenter(1.5f);
		tableDqt2.setQBin(new float[] { 1.0f, 2.0f, 3.0f });
		tableDqt2.setZBin(new float[] { 4.0f, 5.0f, 6.0f });
		tableDqt2.setDqtDef(2);

		assertTrue(tableDqt1.equals(tableDqt2), "Two equal objects should be equal");
	}

	@Test
	void testWsqTableDqtEquals_NonEqualObjects() {
		WsqTableDqt tableDqt1 = new WsqTableDqt();
		tableDqt1.setBinCenter(1.5f);
		tableDqt1.setDqtDef(1);

		WsqTableDqt tableDqt2 = new WsqTableDqt();
		tableDqt2.setBinCenter(2.5f); // Different value
		tableDqt2.setDqtDef(2);

		assertFalse(tableDqt1.equals(tableDqt2), "Different objects should not be equal");
	}

	@Test
	void testWsqTableDqtHashCode_EqualObjects() {
		WsqTableDqt tableDqt1 = new WsqTableDqt();
		tableDqt1.setBinCenter(1.5f);
		tableDqt1.setQBin(new float[] { 1.0f, 2.0f, 3.0f });
		tableDqt1.setZBin(new float[] { 4.0f, 5.0f, 6.0f });
		tableDqt1.setDqtDef(2);

		WsqTableDqt tableDqt2 = new WsqTableDqt();
		tableDqt2.setBinCenter(1.5f);
		tableDqt2.setQBin(new float[] { 1.0f, 2.0f, 3.0f });
		tableDqt2.setZBin(new float[] { 4.0f, 5.0f, 6.0f });
		tableDqt2.setDqtDef(2);

		assertEquals(tableDqt1.hashCode(), tableDqt2.hashCode(), "Equal objects should have the same hash code");
	}

	@Test
	void testWsqTableDqtHashCode_NonEqualObjects() {
		WsqTableDqt tableDqt1 = new WsqTableDqt();
		tableDqt1.setBinCenter(1.5f);

		WsqTableDqt tableDqt2 = new WsqTableDqt();
		tableDqt2.setBinCenter(2.5f); // Different value

		assertNotEquals(tableDqt1.hashCode(), tableDqt2.hashCode(),
				"Non-equal objects should have different hash codes");
	}

	@Test
	void testWsqTableDqtCanEqual() {
		WsqTableDqt tableDqt = new WsqTableDqt();
		assertTrue(tableDqt.canEqual(new WsqTableDqt()), "Should be able to equal another WsqTableDqt");
		assertFalse(tableDqt.canEqual(new Object()), "Should not be able to equal a non-WsqTableDqt");
	}

	@Test
	void testWsqTableDttEquals_SameObject() {
		WsqTableDtt tableDtt = new WsqTableDtt();
		assertTrue(tableDtt.equals(tableDtt), "Should be equal to itself");
	}

	@Test
	void testWsqTableDttEquals_EqualObjects() {
		WsqTableDtt tableDtt1 = new WsqTableDtt();
		tableDtt1.setLowFilter(new float[] { 1.0f, 2.0f, 3.0f });
		tableDtt1.setHighFilter(new float[] { 4.0f, 5.0f, 6.0f });
		tableDtt1.setLowSize(2);
		tableDtt1.setHighSize(3);
		tableDtt1.setLowDef(1);
		tableDtt1.setHighDef(2);

		WsqTableDtt tableDtt2 = new WsqTableDtt();
		tableDtt2.setLowFilter(new float[] { 1.0f, 2.0f, 3.0f });
		tableDtt2.setHighFilter(new float[] { 4.0f, 5.0f, 6.0f });
		tableDtt2.setLowSize(2);
		tableDtt2.setHighSize(3);
		tableDtt2.setLowDef(1);
		tableDtt2.setHighDef(2);

		assertTrue(tableDtt1.equals(tableDtt2), "Two equal objects should be equal");
	}

	@Test
	void testWsqTableDttEquals_NonEqualObjects() {
		WsqTableDtt tableDtt1 = new WsqTableDtt();
		tableDtt1.setLowSize(2);

		WsqTableDtt tableDtt2 = new WsqTableDtt();
		tableDtt2.setLowSize(3); // Different value

		assertFalse(tableDtt1.equals(tableDtt2), "Different objects should not be equal");
	}

	@Test
	void testWsqTableDttHashCode_EqualObjects() {
		WsqTableDtt tableDtt1 = new WsqTableDtt();
		tableDtt1.setLowFilter(new float[] { 1.0f, 2.0f });
		tableDtt1.setHighFilter(new float[] { 3.0f, 4.0f });
		tableDtt1.setLowSize(2);
		tableDtt1.setHighSize(2);
		tableDtt1.setLowDef(1);
		tableDtt1.setHighDef(2);

		WsqTableDtt tableDtt2 = new WsqTableDtt();
		tableDtt2.setLowFilter(new float[] { 1.0f, 2.0f });
		tableDtt2.setHighFilter(new float[] { 3.0f, 4.0f });
		tableDtt2.setLowSize(2);
		tableDtt2.setHighSize(2);
		tableDtt2.setLowDef(1);
		tableDtt2.setHighDef(2);

		assertEquals(tableDtt1.hashCode(), tableDtt2.hashCode(), "Equal objects should have the same hash code");
	}

	@Test
	void testWsqTableDttHashCode_NonEqualObjects() {
		WsqTableDtt tableDtt1 = new WsqTableDtt();
		tableDtt1.setLowSize(2);

		WsqTableDtt tableDtt2 = new WsqTableDtt();
		tableDtt2.setLowSize(3); // Different value

		assertNotEquals(tableDtt1.hashCode(), tableDtt2.hashCode(),
				"Non-equal objects should have different hash codes");
	}

	@Test
	void testWsqTableDttCanEqual() {
		WsqTableDtt tableDtt = new WsqTableDtt();
		assertTrue(tableDtt.canEqual(new WsqTableDtt()), "Should be able to equal another WsqTableDtt");
		assertFalse(tableDtt.canEqual(new Object()), "Should not be able to equal a non-WsqTableDtt");
	}

	@Test
	void testWsqWavletTreeEquals_SameObject() {
		WsqWavletTree tree = new WsqWavletTree();
		assertTrue(tree.equals(tree), "Should be equal to itself");
	}

	@Test
	void testWsqWavletTreeEquals_EqualObjects() {
		WsqWavletTree tree1 = new WsqWavletTree();
		tree1.setX(1);
		tree1.setY(2);
		tree1.setLenX(3);
		tree1.setLenY(4);
		tree1.setInvRow(5);
		tree1.setInvCol(6);

		WsqWavletTree tree2 = new WsqWavletTree();
		tree2.setX(1);
		tree2.setY(2);
		tree2.setLenX(3);
		tree2.setLenY(4);
		tree2.setInvRow(5);
		tree2.setInvCol(6);

		assertTrue(tree1.equals(tree2), "Two equal objects should be equal");
	}

	@Test
	void testWsqWavletTreeEquals_NonEqualObjects() {
		WsqWavletTree tree1 = new WsqWavletTree();
		tree1.setX(1);
		tree1.setY(2);
		tree1.setLenX(3);
		tree1.setLenY(4);
		tree1.setInvRow(5);
		tree1.setInvCol(6);

		WsqWavletTree tree2 = new WsqWavletTree();
		tree2.setX(2);
		tree2.setY(2);
		tree2.setLenX(3);
		tree2.setLenY(4);
		tree2.setInvRow(5);
		tree2.setInvCol(6);

		assertFalse(tree1.equals(tree2), "Different objects should not be equal");
	}

	@Test
	void testWsqWavletTreeHashCode_EqualObjects() {
		WsqWavletTree tree1 = new WsqWavletTree();
		tree1.setX(1);
		tree1.setY(2);
		tree1.setLenX(3);
		tree1.setLenY(4);
		tree1.setInvRow(5);
		tree1.setInvCol(6);

		WsqWavletTree tree2 = new WsqWavletTree();
		tree2.setX(1);
		tree2.setY(2);
		tree2.setLenX(3);
		tree2.setLenY(4);
		tree2.setInvRow(5);
		tree2.setInvCol(6);

		assertEquals(tree1.hashCode(), tree2.hashCode(), "Equal objects should have the same hash code");
	}

	@Test
	void testWsqWavletTreeHashCode_NonEqualObjects() {
		WsqWavletTree tree1 = new WsqWavletTree();
		tree1.setX(1);
		tree1.setY(2);
		tree1.setLenX(3);
		tree1.setLenY(4);
		tree1.setInvRow(5);
		tree1.setInvCol(6);

		WsqWavletTree tree2 = new WsqWavletTree();
		tree2.setX(2);
		tree2.setY(2);
		tree2.setLenX(3);
		tree2.setLenY(4);
		tree2.setInvRow(5);
		tree2.setInvCol(6);

		assertNotEquals(tree1.hashCode(), tree2.hashCode(), "Non-equal objects should have different hash codes");
	}

	@Test
	void testWsqWavletTreeCanEqual() {
		WsqWavletTree tree = new WsqWavletTree();
		assertTrue(tree.canEqual(new WsqWavletTree()), "Should be able to equal another WsqWavletTree");
		assertFalse(tree.canEqual(new Object()), "Should not be able to equal a non-WsqWavletTree");
	}

	@Test
	void testBioEquals_SameObject() {
		Bio bio = new Bio();
		assertTrue(bio.equals(bio), "Should be equal to itself");
	}

	@Test
	void testBioEquals_EqualObjects() {
		Bio bio1 = new Bio();
		bio1.setStart(0);
		bio1.setEnd(10);
		bio1.setBpIndex(5);
		bio1.setBp(new byte[] { 1, 2, 3 });
		bio1.setBuf(12345L);
		bio1.setCt(8);

		Bio bio2 = new Bio();
		bio2.setStart(0);
		bio2.setEnd(10);
		bio2.setBpIndex(5);
		bio2.setBp(new byte[] { 1, 2, 3 });
		bio2.setBuf(12345L);
		bio2.setCt(8);

		assertTrue(bio1.equals(bio2), "Two equal objects should be equal");
	}

	@Test
	void testBioEquals_NonEqualObjects() {
		Bio bio1 = new Bio();
		bio1.setStart(0);

		Bio bio2 = new Bio();
		bio2.setStart(1); // Different value

		assertFalse(bio1.equals(bio2), "Different objects should not be equal");
	}

	@Test
	void testBioHashCode_EqualObjects() {
		Bio bio1 = new Bio();
		bio1.setStart(0);
		bio1.setEnd(10);
		bio1.setBpIndex(5);
		bio1.setBp(new byte[] { 1, 2, 3 });
		bio1.setBuf(12345L);
		bio1.setCt(8);

		Bio bio2 = new Bio();
		bio2.setStart(0);
		bio2.setEnd(10);
		bio2.setBpIndex(5);
		bio2.setBp(new byte[] { 1, 2, 3 });
		bio2.setBuf(12345L);
		bio2.setCt(8);

		assertEquals(bio1.hashCode(), bio2.hashCode(), "Equal objects should have the same hash code");
	}

	@Test
	void testBioHashCode_NonEqualObjects() {
		Bio bio1 = new Bio();
		bio1.setStart(0);

		Bio bio2 = new Bio();
		bio2.setStart(1); // Different value

		assertNotEquals(bio1.hashCode(), bio2.hashCode(), "Non-equal objects should have different hash codes");
	}

	@Test
	void testBioCanEqual() {
		Bio bio = new Bio();
		assertTrue(bio.canEqual(new Bio()), "Should be able to equal another Bio");
		assertFalse(bio.canEqual(new Object()), "Should not be able to equal a non-Bio");
	}

	@Test
	void testCioEquals_SameObject() {
		Cio cio = new Cio();
		assertTrue(cio.equals(cio), "Should be equal to itself");
	}

	@Test
	void testCioEquals_EqualObjects() {
		Cio cio1 = new Cio();
		cio1.setOpenMode(1);
		cio1.setLength(100);
		cio1.setStart(0);
		cio1.setEnd(100);
		cio1.setBpIndex(0);
		cio1.setBuffer(new byte[] { 1, 2, 3 });
		cio1.setCodecContextInfo(new CodecContextInfo()); // Assume a proper constructor

		Cio cio2 = new Cio();
		cio2.setOpenMode(1);
		cio2.setLength(100);
		cio2.setStart(0);
		cio2.setEnd(100);
		cio2.setBpIndex(0);
		cio2.setBuffer(new byte[] { 1, 2, 3 });
		cio2.setCodecContextInfo(new CodecContextInfo()); // Assume a proper constructor

		assertTrue(cio1.equals(cio2), "Two equal objects should be equal");
	}

	@Test
	void testCioEquals_NonEqualObjects() {
		Cio cio1 = new Cio();
		cio1.setOpenMode(1);

		Cio cio2 = new Cio();
		cio2.setOpenMode(2); // Different value

		assertFalse(cio1.equals(cio2), "Different objects should not be equal");
	}

	@Test
	void testCioHashCode_EqualObjects() {
		Cio cio1 = new Cio();
		cio1.setOpenMode(1);
		cio1.setLength(100);
		cio1.setStart(0);
		cio1.setEnd(100);
		cio1.setBpIndex(0);
		cio1.setBuffer(new byte[] { 1, 2, 3 });
		cio1.setCodecContextInfo(new CodecContextInfo()); // Assume a proper constructor

		Cio cio2 = new Cio();
		cio2.setOpenMode(1);
		cio2.setLength(100);
		cio2.setStart(0);
		cio2.setEnd(100);
		cio2.setBpIndex(0);
		cio2.setBuffer(new byte[] { 1, 2, 3 });
		cio2.setCodecContextInfo(new CodecContextInfo()); // Assume a proper constructor

		assertEquals(cio1.hashCode(), cio2.hashCode(), "Equal objects should have the same hash code");
	}

	@Test
	void testCioHashCode_NonEqualObjects() {
		Cio cio1 = new Cio();
		cio1.setOpenMode(1);

		Cio cio2 = new Cio();
		cio2.setOpenMode(2); // Different value

		assertNotEquals(cio1.hashCode(), cio2.hashCode(), "Non-equal objects should have different hash codes");
	}

	@Test
	void testCioCanEqual() {
		Cio cio = new Cio();
		assertTrue(cio.canEqual(new Cio()), "Should be able to equal another Cio");
		assertFalse(cio.canEqual(new Object()), "Should not be able to equal a non-Cio");
	}

	@Test
	void testCodecContextInfoEquals_SameObject() {
		CodecContextInfo codecContextInfo = new CodecContextInfo();
		assertTrue(codecContextInfo.equals(codecContextInfo), "Should be equal to itself");
	}

	@Test
	void testCodecContextInfoEquals_EqualObjects() {
		CodecContextInfo codecContextInfo1 = new CodecContextInfo();
		codecContextInfo1.setContextInfo(new ContextInfo()); // Assume a proper constructor or setter

		CodecContextInfo codecContextInfo2 = new CodecContextInfo();
		codecContextInfo2.setContextInfo(new ContextInfo()); // Assume a proper constructor or setter

		assertTrue(codecContextInfo1.equals(codecContextInfo2), "Two equal objects should be equal");
	}

	@Test
	void testCodecContextInfoHashCode_EqualObjects() {
		CodecContextInfo codecContextInfo1 = new CodecContextInfo();
		codecContextInfo1.setContextInfo(new ContextInfo()); // Assume a proper constructor or setter

		CodecContextInfo codecContextInfo2 = new CodecContextInfo();
		codecContextInfo2.setContextInfo(new ContextInfo()); // Assume a proper constructor or setter

		assertEquals(codecContextInfo1.hashCode(), codecContextInfo2.hashCode(),
				"Equal objects should have the same hash code");
	}

	@Test
	void testCodecContextInfoCanEqual() {
		CodecContextInfo codecContextInfo = new CodecContextInfo();
		assertTrue(codecContextInfo.canEqual(new CodecContextInfo()),
				"Should be able to equal another CodecContextInfo");
		assertFalse(codecContextInfo.canEqual(new Object()), "Should not be able to equal a non-CodecContextInfo");
	}

	@Test
    void testCodeStreamInfoEqualsAndHashCode() {
        CodeStreamInfo info1 = new CodeStreamInfo();
        info1.setDistortionMax(1.0);
        info1.setPacketNo(10);
        info1.setIndexWrite(5);
        info1.setImageWidth(1024);
        info1.setImageHeight(768);
        info1.setNoOfDecompositionComps(new int[]{1, 2, 3});
        
        CodeStreamInfo info2 = new CodeStreamInfo();
        info2.setDistortionMax(1.0);
        info2.setPacketNo(10);
        info2.setIndexWrite(5);
        info2.setImageWidth(1024);
        info2.setImageHeight(768);
        info2.setNoOfDecompositionComps(new int[]{1, 2, 3});

        assertEquals(info1, info2);
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    @Test
    void testCodeStreamInfoCanEqual() {
        CodeStreamInfo info = new CodeStreamInfo();
        assertTrue(info.canEqual(new CodeStreamInfo()));
        assertFalse(info.canEqual(new Object()));
    }

    @Test
    void testCodeStreamInfoToString() {
        CodeStreamInfo info = new CodeStreamInfo();
        info.setDistortionMax(2.5);
        info.setPacketNo(20);
        info.setImageWidth(800);
        info.setImageHeight(600);
        info.setNoOfDecompositionComps(new int[]{2, 4, 6});

        String toStringOutput = info.toString();
        assertTrue(toStringOutput.contains("distortionMax=2.5"));
        assertTrue(toStringOutput.contains("packetNo=20"));
        assertTrue(toStringOutput.contains("imageWidth=800"));
        assertTrue(toStringOutput.contains("imageHeight=600"));
        assertTrue(toStringOutput.contains("noOfDecompositionComps=[2, 4, 6]"));
    }

	@Test
	void testCodingParametersEqualsAndHashCode() {
		CodingParameters params1 = new CodingParameters();
		CodingParameters params2 = new CodingParameters();

		// Set identical fields
		params1.setMaxCompSize(10);
		params1.setImageSize(200);
		params1.setTilePartOn(1);
		// Set more fields as needed...

		params2.setMaxCompSize(10);
		params2.setImageSize(200);
		params2.setTilePartOn(1);
		// Set the same fields as params1...

		assertEquals(params1, params2);
		assertEquals(params1.hashCode(), params2.hashCode());

		// Change a field in params2
		params2.setMaxCompSize(20);
		assertNotEquals(params1, params2);
	}

	@Test
	void testCodingParametersCanEqual() {
		CodingParameters params = new CodingParameters();
		assertTrue(params.canEqual(new CodingParameters()));
		assertFalse(params.canEqual(new Object()));
	}

	@Test
	void testCodingParametersToString() {
		CodingParameters params = new CodingParameters();
		params.setMaxCompSize(10);
		params.setImageSize(200);
		String expectedString = "CodingParameters";
		assertEquals(params.toString().contains(expectedString), true);
	}

	@Test
	void testCompressionParametersEqualsAndHashCode() {
		CompressionParameters params1 = new CompressionParameters();
		CompressionParameters params2 = new CompressionParameters();

		// Set identical fields
		params1.setTileSizeOn(1);
		params1.setCpTileX0(10);
		params1.setCpTileY0(20);
		// Set more fields as needed...

		params2.setTileSizeOn(1);
		params2.setCpTileX0(10);
		params2.setCpTileY0(20);
		// Set identical fields as params1...

		assertEquals(params1, params2);
		assertEquals(params1.hashCode(), params2.hashCode());

		// Change a field in params2
		params2.setCpTileY0(30);

		assertNotEquals(params1, params2);
		assertNotEquals(params1.hashCode(), params2.hashCode());
	}

	@Test
	void testCompressionParametersToString() {
		CompressionParameters params = new CompressionParameters();
		params.setTileSizeOn(1);
		params.setCpTileX0(10);
		params.setCpTileY0(20);
		// Set more fields as needed...

		String expected = "CompressionParameters(tileSizeOn=1, cpTileX0=10, cpTileY0=20, ...)";
		// Ensure to construct the expected output as per the toString method.
		assertTrue(params.toString().contains("tileSizeOn=1"));
		assertTrue(params.toString().contains("cpTileX0=10"));
		assertTrue(params.toString().contains("cpTileY0=20"));
	}

	@Test
	void testCompressionParametersCanEqual() {
		CompressionParameters params = new CompressionParameters();
		assertTrue(params.canEqual(new CompressionParameters())); // Should return true for the same type

		Object nonParamsObject = new Object();
		assertFalse(params.canEqual(nonParamsObject)); // Should return false for a different type
	}

	@Test
	void testContextInfoEqualsAndHashCode() {
		ContextInfo context1 = new ContextInfo();
		ContextInfo context2 = new ContextInfo();

		// Set identical fields
		context1.setIsDecompressor(1);
		context1.setClientData("Data");
		context1.setCodecFormat(JP2CodecFormat.CODEC_JP2);
		// Set other fields as needed...

		context2.setIsDecompressor(1);
		context2.setClientData("Data");
		context2.setCodecFormat(JP2CodecFormat.CODEC_JP2);
		// Set other fields to match context1...

		assertEquals(context1, context2);
		assertEquals(context1.hashCode(), context2.hashCode());

		// Change a field in context2
		context2.setIsDecompressor(2);

		assertNotEquals(context1, context2);
		assertNotEquals(context1.hashCode(), context2.hashCode());
	}

	@Test
	void testContextInfoCanEqual() {
		ContextInfo context = new ContextInfo();
		assertTrue(context.canEqual(new ContextInfo())); // Same type, should return true

		Object nonContextObject = new Object();
		assertFalse(context.canEqual(nonContextObject)); // Different type, should return false
	}

	@Test
	void testContextInfoToString() {
		ContextInfo context = new ContextInfo();
		context.setIsDecompressor(1);
		context.setClientData("Data");

		String toStringOutput = context.toString();
		assertTrue(toStringOutput.contains("isDecompressor=1"));
		assertTrue(toStringOutput.contains("clientData=Data"));
	}

	@Test
	void testDecoderFunctionInfoEqualsAndHashCode() {
		DecoderFunctionInfo decoder1 = new DecoderFunctionInfo(1, 2, "FunctionA");
		DecoderFunctionInfo decoder2 = new DecoderFunctionInfo(1, 2, "FunctionA");

		assertEquals(decoder1, decoder2);
		assertEquals(decoder1.hashCode(), decoder2.hashCode());

		// Change a field in decoder2
		decoder2.setStates(3);
		assertNotEquals(decoder1, decoder2);
		assertNotEquals(decoder1.hashCode(), decoder2.hashCode());
	}

	@Test
	void testDecoderFunctionInfoCanEqual() {
		DecoderFunctionInfo decoder = new DecoderFunctionInfo(1, 2, "FunctionA");
		assertTrue(decoder.canEqual(new DecoderFunctionInfo(2, 3, "FunctionB"))); // Same type, should return true

		Object nonDecoderObject = new Object();
		assertFalse(decoder.canEqual(nonDecoderObject)); // Different type, should return false
	}

	@Test
	void testDecoderFunctionInfoToString() {
		DecoderFunctionInfo decoder = new DecoderFunctionInfo(1, 2, "FunctionA");
		String toStringOutput = decoder.toString();
		assertTrue(toStringOutput.contains("id=1"));
		assertTrue(toStringOutput.contains("states=2"));
		assertTrue(toStringOutput.contains("j2kFunctionName=FunctionA"));
	}

	@Test
	void testDecompressionParametersEqualsAndHashCode() {
		DecompressionParameters params1 = new DecompressionParameters();
		params1.setCpReduce(1);
		params1.setCpLayer(2);
		params1.setDecodeFormat(0);
		params1.setCodecFormat(1);
		params1.setJpwlCorrect(1);
		params1.setJpwlExpComps(3);
		params1.setJpwlMaxTiles(5);
		params1.setCpLimitDecoding(LimitDecoding.NO_LIMITATION);

		DecompressionParameters params2 = new DecompressionParameters();
		params2.setCpReduce(1);
		params2.setCpLayer(2);
		params2.setDecodeFormat(0);
		params2.setCodecFormat(1);
		params2.setJpwlCorrect(1);
		params2.setJpwlExpComps(3);
		params2.setJpwlMaxTiles(5);
		params2.setCpLimitDecoding(LimitDecoding.NO_LIMITATION);

		assertEquals(params1, params2);
		assertEquals(params1.hashCode(), params2.hashCode());

		// Change a field in params2
		params2.setCpLayer(3);
		assertNotEquals(params1, params2);
		assertNotEquals(params1.hashCode(), params2.hashCode());
	}

	@Test
	void testDecompressionParametersCanEqual() {
		DecompressionParameters params = new DecompressionParameters();
		assertTrue(params.canEqual(new DecompressionParameters())); // Same type, should return true

		Object nonParamsObject = new Object();
		assertFalse(params.canEqual(nonParamsObject)); // Different type, should return false
	}

	@Test
	void testDecompressionParametersToString() {
		DecompressionParameters params = new DecompressionParameters();
		params.setCpReduce(1);
		params.setCpLayer(2);
		String toStringOutput = params.toString();
		assertTrue(toStringOutput.contains("cpReduce=1"));
		assertTrue(toStringOutput.contains("cpLayer=2"));
	}

	@Test
	void testDwtEqualsAndHashCode() {
		Dwt dwt1 = new Dwt();
		dwt1.setMemIndex(1);
		dwt1.setMem(new int[] { 1, 2, 3 });
		dwt1.setDn(4);
		dwt1.setSn(5);
		dwt1.setCas(6);

		Dwt dwt2 = new Dwt();
		dwt2.setMemIndex(1);
		dwt2.setMem(new int[] { 1, 2, 3 });
		dwt2.setDn(4);
		dwt2.setSn(5);
		dwt2.setCas(6);

		assertEquals(dwt1, dwt2);
		assertEquals(dwt1.hashCode(), dwt2.hashCode());

		// Change a field in dwt2
		dwt2.setMemIndex(2);
		assertNotEquals(dwt1, dwt2);
		assertNotEquals(dwt1.hashCode(), dwt2.hashCode());
	}

	@Test
	void testDwtCanEqual() {
		Dwt dwt = new Dwt();
		assertTrue(dwt.canEqual(new Dwt())); // Same type, should return true

		Object nonDwtObject = new Object();
		assertFalse(dwt.canEqual(nonDwtObject)); // Different type, should return false
	}

	@Test
	void testDwtToString() {
		Dwt dwt = new Dwt();
		dwt.setMemIndex(1);
		dwt.setDn(4);
		dwt.setSn(5);
		dwt.setCas(6);
		String toStringOutput = dwt.toString();
		assertTrue(toStringOutput.contains("memIndex=1"));
		assertTrue(toStringOutput.contains("dn=4"));
		assertTrue(toStringOutput.contains("sn=5"));
		assertTrue(toStringOutput.contains("cas=6"));
	}

	@Test
	void testDwtV4EqualsAndHashCode() {
		V4[] wavelet1 = { new V4(), new V4() }; // Assuming V4 has a default constructor
		DwtV4 dwtV41 = new DwtV4();
		dwtV41.setWavelet(wavelet1);
		dwtV41.setDn(4);
		dwtV41.setSn(5);
		dwtV41.setCas(6);

		V4[] wavelet2 = { new V4(), new V4() }; // Same content as wavelet1
		DwtV4 dwtV42 = new DwtV4();
		dwtV42.setWavelet(wavelet2);
		dwtV42.setDn(4);
		dwtV42.setSn(5);
		dwtV42.setCas(6);

		assertEquals(dwtV41, dwtV42);
		assertEquals(dwtV41.hashCode(), dwtV42.hashCode());

		// Change a field in dwtV42
		dwtV42.setDn(10);
		assertNotEquals(dwtV41, dwtV42);
		assertNotEquals(dwtV41.hashCode(), dwtV42.hashCode());
	}

	@Test
	void testDwtV4CanEqual() {
		DwtV4 dwtV4 = new DwtV4();
		assertTrue(dwtV4.canEqual(new DwtV4())); // Same type, should return true

		Object nonDwtV4Object = new Object();
		assertFalse(dwtV4.canEqual(nonDwtV4Object)); // Different type, should return false
	}

	@Test
	void testDwtV4ToString() {
		DwtV4 dwtV4 = new DwtV4();
		dwtV4.setDn(4);
		dwtV4.setSn(5);
		dwtV4.setCas(6);
		String toStringOutput = dwtV4.toString();
		assertTrue(toStringOutput.contains("dn=4"));
		assertTrue(toStringOutput.contains("sn=5"));
		assertTrue(toStringOutput.contains("cas=6"));
	}

	@Test
	void testJ2KEqualsAndHashCode() {
		J2K j2k1 = new J2K();
		j2k1.setState(1);
		j2k1.setCurTileNo(2);
		j2k1.setTilePartNo(3);
		j2k1.setCurTilePartNo(4);
		j2k1.setTlmStart(5);
		j2k1.setTotalNoOfTilePart(6);
		j2k1.setEot(7);
		j2k1.setSotStart(8);
		j2k1.setSodStart(9);
		j2k1.setPosCorrection(10);
		j2k1.setEndCode(11);

		// Assuming CodecContextInfo and other required classes are implemented
		CodecContextInfo codecContextInfo = new CodecContextInfo();
		j2k1.setCodecContextInfo(codecContextInfo);

		// Set other necessary fields

		J2K j2k2 = new J2K();
		j2k2.setState(1);
		j2k2.setCurTileNo(2);
		j2k2.setTilePartNo(3);
		j2k2.setCurTilePartNo(4);
		j2k2.setTlmStart(5);
		j2k2.setTotalNoOfTilePart(6);
		j2k2.setEot(7);
		j2k2.setSotStart(8);
		j2k2.setSodStart(9);
		j2k2.setPosCorrection(10);
		j2k2.setEndCode(11);
		j2k2.setCodecContextInfo(codecContextInfo);

		assertEquals(j2k1, j2k2);
		assertEquals(j2k1.hashCode(), j2k2.hashCode());

		// Change a field in j2k2
		j2k2.setCurTileNo(20);
		assertNotEquals(j2k1, j2k2);
		assertNotEquals(j2k1.hashCode(), j2k2.hashCode());
	}

	@Test
	void testJ2KCanEqual() {
		J2K j2k = new J2K();
		assertTrue(j2k.canEqual(new J2K())); // Same type, should return true

		Object nonJ2KObject = new Object();
		assertFalse(j2k.canEqual(nonJ2KObject)); // Different type, should return false
	}

	@Test
	void testJ2KToString() {
		J2K j2k = new J2K();
		j2k.setState(1);
		j2k.setCurTileNo(2);
		j2k.setTilePartNo(3);
		j2k.setCurTilePartNo(4);
		String toStringOutput = j2k.toString();
		assertTrue(toStringOutput.contains("state=1"));
		assertTrue(toStringOutput.contains("curTileNo=2"));
		assertTrue(toStringOutput.contains("tilePartNo=3"));
		assertTrue(toStringOutput.contains("curTilePartNo=4"));
	}

	@Test
	void testJ2KProgressionOrderEqualsAndHashCode() {
		char[] name1 = { 'R', 'G', 'B', 'A' };
		char[] name2 = { 'R', 'G', 'B', 'A' };
		J2KProgressionOrder order1 = new J2KProgressionOrder(ProgressionOrder.RLCP, name1);
		J2KProgressionOrder order2 = new J2KProgressionOrder(ProgressionOrder.RLCP, name2);

		assertEquals(order1, order2);
		assertEquals(order1.hashCode(), order2.hashCode());

		// Change a field in order2
		order2.setProgressionOrder(ProgressionOrder.RPCL);
		assertNotEquals(order1, order2);
		assertNotEquals(order1.hashCode(), order2.hashCode());
	}

	@Test
	void testJ2KProgressionOrderCanEqual() {
		J2KProgressionOrder order = new J2KProgressionOrder(ProgressionOrder.RLCP, new char[4]);
		assertTrue(order.canEqual(new J2KProgressionOrder(ProgressionOrder.RLCP, new char[4])));

		Object nonJ2KProgressionOrder = new Object();
		assertFalse(order.canEqual(nonJ2KProgressionOrder));
	}

	@Test
	void testJ2KProgressionOrderToString() {
		char[] progressionName = { 'R', 'G', 'B', 'A' };
		J2KProgressionOrder order = new J2KProgressionOrder(ProgressionOrder.RLCP, progressionName);
		String toStringOutput = order.toString();
		assertTrue(toStringOutput.contains("progressionOrder=RLCP"));
		assertTrue(toStringOutput.contains("progressionName=[R, G, B, A]"));
	}

	@Test
	void testJP2EqualsAndHashCode() {
		long[] cl1 = { 1, 2, 3 };
		long[] cl2 = { 1, 2, 3 };
		long[] offset1 = { 100 };
		long[] offset2 = { 100 };
		long[] length1 = { 200 };
		long[] length2 = { 200 };

		JP2 jp2a = new JP2();
		jp2a.setWidth(1920);
		jp2a.setHeight(1080);
		jp2a.setNoOfComps(3);
		jp2a.setBpc(8);
		jp2a.setCl(cl1);
		jp2a.setJ2kCodestreamOffset(offset1);
		jp2a.setJ2kCodestreamLength(length1);

		JP2 jp2b = new JP2();
		jp2b.setWidth(1920);
		jp2b.setHeight(1080);
		jp2b.setNoOfComps(3);
		jp2b.setBpc(8);
		jp2b.setCl(cl2);
		jp2b.setJ2kCodestreamOffset(offset2);
		jp2b.setJ2kCodestreamLength(length2);

		assertEquals(jp2a, jp2b);
		assertEquals(jp2a.hashCode(), jp2b.hashCode());

		// Change a field in jp2b
		jp2b.setWidth(1280);
		assertNotEquals(jp2a, jp2b);
		assertNotEquals(jp2a.hashCode(), jp2b.hashCode());
	}

	@Test
	void testJP2CanEqual() {
		JP2 jp2 = new JP2();
		assertTrue(jp2.canEqual(new JP2()));

		Object nonJP2 = new Object();
		assertFalse(jp2.canEqual(nonJP2));
	}

	@Test
	void testJP2ToString() {
		JP2 jp2 = new JP2();
		jp2.setWidth(1920);
		jp2.setHeight(1080);
		String toStringOutput = jp2.toString();
		assertTrue(toStringOutput.contains("width=1920"));
		assertTrue(toStringOutput.contains("height=1080"));
	}

	@Test
	void testJP2BoxEqualsAndHashCode() {
		JP2Box box1 = new JP2Box();
		box1.setLength(100);
		box1.setType(1);
		box1.setInitPosition(50);

		JP2Box box2 = new JP2Box();
		box2.setLength(100);
		box2.setType(1);
		box2.setInitPosition(50);

		assertEquals(box1, box2);
		assertEquals(box1.hashCode(), box2.hashCode());

		// Change a field in box2
		box2.setLength(200);
		assertNotEquals(box1, box2);
		assertNotEquals(box1.hashCode(), box2.hashCode());
	}

	@Test
	void testJP2BoxCanEqual() {
		JP2Box box = new JP2Box();
		assertTrue(box.canEqual(new JP2Box()));

		Object nonJP2Box = new Object();
		assertFalse(box.canEqual(nonJP2Box));
	}

	@Test
	void testJP2BoxToString() {
		JP2Box box = new JP2Box();
		box.setLength(100);
		box.setType(1);
		box.setInitPosition(50);
		String toStringOutput = box.toString();
		assertTrue(toStringOutput.contains("length=100"));
		assertTrue(toStringOutput.contains("type=1"));
		assertTrue(toStringOutput.contains("initPosition=50"));
	}

	@Test
	void testJP2ComponentEqualsAndHashCode() {
		JP2Component comp1 = new JP2Component();
		comp1.setDepth(8);
		comp1.setSgnd(0);
		comp1.setBpcc(3);

		JP2Component comp2 = new JP2Component();
		comp2.setDepth(8);
		comp2.setSgnd(0);
		comp2.setBpcc(3);

		assertEquals(comp1, comp2);
		assertEquals(comp1.hashCode(), comp2.hashCode());

		// Change a field in comp2
		comp2.setDepth(10);
		assertNotEquals(comp1, comp2);
		assertNotEquals(comp1.hashCode(), comp2.hashCode());
	}

	@Test
	void testJP2ComponentCanEqual() {
		JP2Component comp = new JP2Component();
		assertTrue(comp.canEqual(new JP2Component()));

		Object nonJP2Component = new Object();
		assertFalse(comp.canEqual(nonJP2Component));
	}

	@Test
	void testJP2ComponentToString() {
		JP2Component comp = new JP2Component();
		comp.setDepth(8);
		comp.setSgnd(0);
		comp.setBpcc(3);
		String toStringOutput = comp.toString();
		assertTrue(toStringOutput.contains("depth=8"));
		assertTrue(toStringOutput.contains("sgnd=0"));
		assertTrue(toStringOutput.contains("bpcc=3"));
	}

	@Test
	void testJP2ResolutionBoxEqualsAndHashCode() {
		JP2ResolutionBox box1 = new JP2ResolutionBox();
		box1.setResolutionBoxType(1);
		box1.setVerticalNumerator(720);
		box1.setVerticalDenominator(480);
		box1.setHorizontalNumerator(1280);
		box1.setHorizontalDenominator(720);
		box1.setVerticalExponent(2);
		box1.setHorizontalExponent(2);
		box1.setVerticalResolution(480);
		box1.setHorizontalResolution(720);

		JP2ResolutionBox box2 = new JP2ResolutionBox();
		box2.setResolutionBoxType(1);
		box2.setVerticalNumerator(720);
		box2.setVerticalDenominator(480);
		box2.setHorizontalNumerator(1280);
		box2.setHorizontalDenominator(720);
		box2.setVerticalExponent(2);
		box2.setHorizontalExponent(2);
		box2.setVerticalResolution(480);
		box2.setHorizontalResolution(720);

		assertEquals(box1, box2);
		assertEquals(box1.hashCode(), box2.hashCode());

		// Change a field in box2
		box2.setVerticalNumerator(1080);
		assertNotEquals(box1, box2);
		assertNotEquals(box1.hashCode(), box2.hashCode());
	}

	@Test
	void testJP2ResolutionBoxCanEqual() {
		JP2ResolutionBox box = new JP2ResolutionBox();
		assertTrue(box.canEqual(new JP2ResolutionBox()));

		Object nonJP2ResolutionBox = new Object();
		assertFalse(box.canEqual(nonJP2ResolutionBox));
	}

	@Test
	void testJP2ResolutionBoxToString() {
		JP2ResolutionBox box = new JP2ResolutionBox();
		box.setResolutionBoxType(1);
		box.setVerticalNumerator(720);
		box.setVerticalDenominator(480);
		box.setHorizontalNumerator(1280);
		box.setHorizontalDenominator(720);
		box.setVerticalExponent(2);
		box.setHorizontalExponent(2);
		box.setVerticalResolution(480);
		box.setHorizontalResolution(720);
		String toStringOutput = box.toString();
		assertTrue(toStringOutput.contains("resolutionBoxType=1"));
		assertTrue(toStringOutput.contains("verticalNumerator=720"));
		assertTrue(toStringOutput.contains("verticalDenominator=480"));
		assertTrue(toStringOutput.contains("horizontalNumerator=1280"));
		assertTrue(toStringOutput.contains("horizontalDenominator=720"));
		assertTrue(toStringOutput.contains("verticalExponent=2"));
		assertTrue(toStringOutput.contains("horizontalExponent=2"));
		assertTrue(toStringOutput.contains("verticalResolution=480"));
		assertTrue(toStringOutput.contains("horizontalResolution=720"));
	}

	@Test
	void testJPTMessageHeaderEqualsAndHashCode() {
		JPTMessageHeader header1 = new JPTMessageHeader();
		header1.setId(1L);
		header1.setLastByte(255L);
		header1.setClassId(100L);
		header1.setCSnId(10L);
		header1.setMsgOffset(0L);
		header1.setMsgLength(1024L);
		header1.setLayerNb(2L);

		JPTMessageHeader header2 = new JPTMessageHeader();
		header2.setId(1L);
		header2.setLastByte(255L);
		header2.setClassId(100L);
		header2.setCSnId(10L);
		header2.setMsgOffset(0L);
		header2.setMsgLength(1024L);
		header2.setLayerNb(2L);

		assertEquals(header1, header2);
		assertEquals(header1.hashCode(), header2.hashCode());

		// Change a field in header2
		header2.setLastByte(128L);
		assertNotEquals(header1, header2);
		assertNotEquals(header1.hashCode(), header2.hashCode());
	}

	@Test
	void testJPTMessageHeaderCanEqual() {
		JPTMessageHeader header = new JPTMessageHeader();
		assertTrue(header.canEqual(new JPTMessageHeader()));

		Object nonJPTMessageHeader = new Object();
		assertFalse(header.canEqual(nonJPTMessageHeader));
	}

	@Test
	void testJPTMessageHeaderToString() {
		JPTMessageHeader header = new JPTMessageHeader();
		header.setId(1L);
		header.setLastByte(255L);
		header.setClassId(100L);
		header.setCSnId(10L);
		header.setMsgOffset(0L);
		header.setMsgLength(1024L);
		header.setLayerNb(2L);

		String toStringOutput = header.toString();
		assertTrue(toStringOutput.contains("Id=1"));
		assertTrue(toStringOutput.contains("lastByte=255"));
		assertTrue(toStringOutput.contains("classId=100"));
		assertTrue(toStringOutput.contains("cSnId=10"));
		assertTrue(toStringOutput.contains("msgOffset=0"));
		assertTrue(toStringOutput.contains("msgLength=1024"));
		assertTrue(toStringOutput.contains("layerNb=2"));
	}

	@Test
	void testMarkerInfoEqualsAndHashCode() {
		MarkerInfo marker1 = new MarkerInfo();
		marker1.setType(1);
		marker1.setPosition(100);
		marker1.setLength(50);

		MarkerInfo marker2 = new MarkerInfo();
		marker2.setType(1);
		marker2.setPosition(100);
		marker2.setLength(50);

		assertEquals(marker1, marker2);
		assertEquals(marker1.hashCode(), marker2.hashCode());

		// Change a field in marker2
		marker2.setLength(60);
		assertNotEquals(marker1, marker2);
		assertNotEquals(marker1.hashCode(), marker2.hashCode());
	}

	@Test
	void testMarkerInfoCanEqual() {
		MarkerInfo marker = new MarkerInfo();
		assertTrue(marker.canEqual(new MarkerInfo()));

		Object nonMarkerInfo = new Object();
		assertFalse(marker.canEqual(nonMarkerInfo));
	}

	@Test
	void testMarkerInfoToString() {
		MarkerInfo marker = new MarkerInfo();
		marker.setType(1);
		marker.setPosition(100);
		marker.setLength(50);

		String toStringOutput = marker.toString();
		assertTrue(toStringOutput.contains("type=1"));
		assertTrue(toStringOutput.contains("position=100"));
		assertTrue(toStringOutput.contains("length=50"));
	}

	@Test
	void testMQCoderEquals() {
		MQCoder coder1 = new MQCoder();

		// Set the same values for all fields
		coder1.setC(100L);
		coder1.setA(200L);
		coder1.setCt(300L);
		coder1.setBp(new byte[] { 1, 2, 3 });
		coder1.setBpIndex(1);
		coder1.setStart(0);
		coder1.setEnd(5);
		coder1.setContextIndex(2);
		coder1.setContexts(new MQCoderState[OpenJpegConstant.MQC_NUMCTXS]);
        coder1.setCurrentContext(new MQCoderState(0x8000, 1, 0, 1));

		MQCoder coder2 = new MQCoder();
		coder2.setC(100L);
		coder2.setA(200L);
		coder2.setCt(300L);
		coder2.setBp(new byte[] { 1, 2, 3 });
		coder2.setBpIndex(1);
		coder2.setStart(0);
		coder2.setEnd(5);
		coder2.setContextIndex(2);
		coder2.setContexts(new MQCoderState[OpenJpegConstant.MQC_NUMCTXS]);
        coder2.setCurrentContext(new MQCoderState(0x8000, 1, 0, 1));

		// Test for equality
		assertEquals(coder1, coder2);
		assertEquals(coder1.hashCode(), coder2.hashCode());
		assertTrue(coder1.canEqual(coder2));

		// Test for inequality by changing a field
		coder2.setC(101L);
		assertNotEquals(coder1, coder2);
	}

	@Test
	void testMQCoderHashCode() {
		MQCoder coder1 = new MQCoder();
		MQCoder coder2 = new MQCoder();

		coder1.setC(100L);
		coder1.setA(200L);
		coder1.setCt(300L);
		coder1.setBp(new byte[] { 1, 2, 3 });

		coder2.setC(100L);
		coder2.setA(200L);
		coder2.setCt(300L);
		coder2.setBp(new byte[] { 1, 2, 3 });

		assertEquals(coder1.hashCode(), coder2.hashCode());

		// Change a field to ensure hash codes are different
		coder2.setA(201L);
		assertNotEquals(coder1.hashCode(), coder2.hashCode());
	}

	@Test
    void testMQCoderToString() {
        MQCoder coder = new MQCoder();
        coder.setC(100L);
        coder.setA(200L);
        coder.setCt(300L);
        coder.setBp(new byte[]{1, 2, 3});
        coder.setBpIndex(1);
        coder.setStart(0);
        coder.setEnd(5);
        coder.setContextIndex(2);
        coder.setContexts(new MQCoderState[OpenJpegConstant.MQC_NUMCTXS]);
        coder.setCurrentContext(new MQCoderState(0x8000, 1, 0, 1));

        String toStringOutput = coder.toString();

        // Assertions to check if the toString contains expected field values
        assertTrue(toStringOutput.contains("c=100"));
        assertTrue(toStringOutput.contains("a=200"));
        assertTrue(toStringOutput.contains("ct=300"));
        assertTrue(toStringOutput.contains("bp=[1, 2, 3]"));
        assertTrue(toStringOutput.contains("bpIndex=1"));
        assertTrue(toStringOutput.contains("start=0"));
        assertTrue(toStringOutput.contains("end=5"));
        assertTrue(toStringOutput.contains("contextIndex=2"));
        assertTrue(toStringOutput.contains("contexts=" + Arrays.toString(new MQCoderState[OpenJpegConstant.MQC_NUMCTXS])));
        assertTrue(toStringOutput.contains("currentContext=" + coder.getCurrentContext().toString()));
    }

	@Test
	void testMQCoderCanEqual() {
		MQCoder coder1 = new MQCoder();
		MQCoder coder2 = new MQCoder();
		assertTrue(coder1.canEqual(coder2));

		// Test with a different object type
		assertFalse(coder1.canEqual(new Object()));
	}

	@Test
	void testMQCoderStateEqualsAndHashCode() {
		MQCoderState state1 = new MQCoderState(0x8000, 1, 0, 1);
		MQCoderState state2 = new MQCoderState(0x8000, 1, 0, 1);

		// Test equality
		assertEquals(state1, state2);
		assertEquals(state1.hashCode(), state2.hashCode());

		// Change a field in state2
		state2.setMps(0);
		assertNotEquals(state1, state2);
		assertNotEquals(state1.hashCode(), state2.hashCode());
	}

	@Test
	void testMQCoderStateCanEqual() {
		MQCoderState state = new MQCoderState(0x8000, 1, 0, 1);
		assertTrue(state.canEqual(new MQCoderState(0x8000, 1, 0, 1)));

		Object nonMQCoderState = new Object();
		assertFalse(state.canEqual(nonMQCoderState));
	}

	@Test
	void testMQCoderStateToString() {
		MQCoderState state = new MQCoderState(0x8000, 1, 0, 1);
		String toStringOutput = state.toString();
		assertTrue(toStringOutput.contains("qeval=32768"));
		assertTrue(toStringOutput.contains("mps=1"));
		assertTrue(toStringOutput.contains("nmpsIndex=0"));
		assertTrue(toStringOutput.contains("nlpsIndex=1"));
	}

	@Test
	void testOpenJpegImageEqualsAndHashCode() {
		OpenJpegImage image1 = new OpenJpegImage();
		image1.setX0(0);
		image1.setY0(0);
		image1.setX1(1920);
		image1.setY1(1080);
		image1.setNoOfComps(3);
		image1.setQmfbid(0);
		image1.setColorSpace(Jp2ColorSpace.CLRSPC_SRGB);
		image1.setComps(new OpenJpegImageComponent[3]);

		OpenJpegImage image2 = new OpenJpegImage();
		image2.setX0(0);
		image2.setY0(0);
		image2.setX1(1920);
		image2.setY1(1080);
		image2.setNoOfComps(3);
		image2.setQmfbid(0);
		image2.setColorSpace(Jp2ColorSpace.CLRSPC_SRGB);
		image2.setComps(new OpenJpegImageComponent[3]);

		// Test equality
		assertEquals(image1, image2);
		assertEquals(image1.hashCode(), image2.hashCode());

		// Change a field in image2
		image2.setX1(1280);
		assertNotEquals(image1, image2);
		assertNotEquals(image1.hashCode(), image2.hashCode());
	}

	@Test
	void testOpenJpegImageCanEqual() {
		OpenJpegImage image = new OpenJpegImage();
		assertTrue(image.canEqual(new OpenJpegImage()));

		Object nonOpenJpegImage = new Object();
		assertFalse(image.canEqual(nonOpenJpegImage));
	}

	@Test
	void testOpenJpegImageToString() {
		OpenJpegImage image = new OpenJpegImage();
		image.setX0(0);
		image.setY0(0);
		image.setX1(1920);
		image.setY1(1080);
		image.setNoOfComps(3);
		image.setQmfbid(0);
		image.setColorSpace(Jp2ColorSpace.CLRSPC_SRGB);
		String toStringOutput = image.toString();
		assertTrue(toStringOutput.contains("x0=0"));
		assertTrue(toStringOutput.contains("y0=0"));
		assertTrue(toStringOutput.contains("x1=1920"));
		assertTrue(toStringOutput.contains("y1=1080"));
		assertTrue(toStringOutput.contains("noOfComps=3"));
		assertTrue(toStringOutput.contains("qmfbid=0"));
	}

	@Test
	void testOpenJpegImageComponentEqualsAndHashCode() {
		OpenJpegImageComponent component1 = new OpenJpegImageComponent();
		component1.setDX(1);
		component1.setDY(1);
		component1.setWidth(1920);
		component1.setHeight(1080);
		component1.setX0(0);
		component1.setY0(0);
		component1.setPrec(8);
		component1.setBpp(24);
		component1.setSgnd(0);
		component1.setResNoDecoded(1);
		component1.setFactor(1);
		component1.setData(new int[] { 0, 1, 2 });

		OpenJpegImageComponent component2 = new OpenJpegImageComponent();
		component2.setDX(1);
		component2.setDY(1);
		component2.setWidth(1920);
		component2.setHeight(1080);
		component2.setX0(0);
		component2.setY0(0);
		component2.setPrec(8);
		component2.setBpp(24);
		component2.setSgnd(0);
		component2.setResNoDecoded(1);
		component2.setFactor(1);
		component2.setData(new int[] { 0, 1, 2 });

		// Test equality
		assertEquals(component1, component2);
		assertEquals(component1.hashCode(), component2.hashCode());

		// Change a field in component2
		component2.setWidth(1280);
		assertNotEquals(component1, component2);
		assertNotEquals(component1.hashCode(), component2.hashCode());
	}

	@Test
	void testOpenJpegImageComponentCanEqual() {
		OpenJpegImageComponent component = new OpenJpegImageComponent();
		assertTrue(component.canEqual(new OpenJpegImageComponent()));

		Object nonOpenJpegImageComponent = new Object();
		assertFalse(component.canEqual(nonOpenJpegImageComponent));
	}

	@Test
	void testOpenJpegImageComponentToString() {
		OpenJpegImageComponent component = new OpenJpegImageComponent();
		component.setDX(1);
		component.setDY(1);
		component.setWidth(1920);
		component.setHeight(1080);
		String toStringOutput = component.toString();
		assertTrue(toStringOutput.contains("dX=1"));
		assertTrue(toStringOutput.contains("dY=1"));
		assertTrue(toStringOutput.contains("width=1920"));
		assertTrue(toStringOutput.contains("height=1080"));
	}

	@Test
	void testOpenJpegImageComponentParametersEqualsAndHashCode() {
		OpenJpegImageComponentParameters params1 = new OpenJpegImageComponentParameters();
		params1.setDx(1);
		params1.setDy(1);
		params1.setWidth(1920);
		params1.setHeight(1080);
		params1.setX0(0);
		params1.setY0(0);
		params1.setPrec(8);
		params1.setBpp(24);
		params1.setSgnd(0);

		OpenJpegImageComponentParameters params2 = new OpenJpegImageComponentParameters();
		params2.setDx(1);
		params2.setDy(1);
		params2.setWidth(1920);
		params2.setHeight(1080);
		params2.setX0(0);
		params2.setY0(0);
		params2.setPrec(8);
		params2.setBpp(24);
		params2.setSgnd(0);

		// Test equality
		assertEquals(params1, params2);
		assertEquals(params1.hashCode(), params2.hashCode());

		// Change a field in params2
		params2.setWidth(1280);
		assertNotEquals(params1, params2);
		assertNotEquals(params1.hashCode(), params2.hashCode());
	}

	@Test
	void testOpenJpegImageComponentParametersCanEqual() {
		OpenJpegImageComponentParameters params = new OpenJpegImageComponentParameters();
		assertTrue(params.canEqual(new OpenJpegImageComponentParameters()));

		Object nonOpenJpegImageComponentParameters = new Object();
		assertFalse(params.canEqual(nonOpenJpegImageComponentParameters));
	}

	@Test
	void testOpenJpegImageComponentParametersToString() {
		OpenJpegImageComponentParameters params = new OpenJpegImageComponentParameters();
		params.setDx(1);
		params.setDy(1);
		params.setWidth(1920);
		params.setHeight(1080);
		String toStringOutput = params.toString();
		assertTrue(toStringOutput.contains("dx=1"));
		assertTrue(toStringOutput.contains("dy=1"));
		assertTrue(toStringOutput.contains("width=1920"));
		assertTrue(toStringOutput.contains("height=1080"));
	}

	@Test
	void testPacketInfoEqualsAndHashCode() {
		PacketInfo packet1 = new PacketInfo();
		packet1.setStartPosition(100);
		packet1.setEndPHPosition(200);
		packet1.setEndPosition(300);
		packet1.setDistortion(0.5);

		PacketInfo packet2 = new PacketInfo();
		packet2.setStartPosition(100);
		packet2.setEndPHPosition(200);
		packet2.setEndPosition(300);
		packet2.setDistortion(0.5);

		// Test equality
		assertEquals(packet1, packet2);
		assertEquals(packet1.hashCode(), packet2.hashCode());

		// Change a field in packet2
		packet2.setDistortion(1.0);
		assertNotEquals(packet1, packet2);
		assertNotEquals(packet1.hashCode(), packet2.hashCode());
	}

	@Test
	void testPacketInfoCanEqual() {
		PacketInfo packet = new PacketInfo();
		assertTrue(packet.canEqual(new PacketInfo()));

		Object nonPacketInfo = new Object();
		assertFalse(packet.canEqual(nonPacketInfo));
	}

	@Test
	void testPacketInfoToString() {
		PacketInfo packet = new PacketInfo();
		packet.setStartPosition(100);
		packet.setEndPHPosition(200);
		packet.setEndPosition(300);
		packet.setDistortion(0.5);
		String toStringOutput = packet.toString();
		assertTrue(toStringOutput.contains("startPosition=100"));
		assertTrue(toStringOutput.contains("endPHPosition=200"));
		assertTrue(toStringOutput.contains("endPosition=300"));
		assertTrue(toStringOutput.contains("distortion=0.5"));
	}

	@Test
	void testPiComponentEqualsAndHashCode() {
		PiComponent component1 = new PiComponent();
		component1.setDX(10);
		component1.setDY(20);
		component1.setNoOfResolutions(5);
		component1.setResolutions(new PiResolution[] { new PiResolution(), // Assuming PiResolution has a no-args
																			// constructor
				new PiResolution() // You may need to set properties to make them equal
		});

		PiComponent component2 = new PiComponent();
		component2.setDX(10);
		component2.setDY(20);
		component2.setNoOfResolutions(5);
		component2.setResolutions(new PiResolution[] { new PiResolution(), new PiResolution() });

		// Test equality
		assertEquals(component1, component2);
		assertEquals(component1.hashCode(), component2.hashCode());

		// Change a field in component2
		component2.setNoOfResolutions(6);
		assertNotEquals(component1, component2);
		assertNotEquals(component1.hashCode(), component2.hashCode());
	}

	@Test
	void testPiComponentCanEqual() {
		PiComponent component = new PiComponent();
		assertTrue(component.canEqual(new PiComponent()));

		Object nonPiComponent = new Object();
		assertFalse(component.canEqual(nonPiComponent));
	}

	@Test
	void testPiComponentToString() {
		PiComponent component = new PiComponent();
		component.setDX(10);
		component.setDY(20);
		component.setNoOfResolutions(5);
		String toStringOutput = component.toString();
		assertTrue(toStringOutput.contains("dX=10"));
		assertTrue(toStringOutput.contains("dY=20"));
		assertTrue(toStringOutput.contains("noOfResolutions=5"));
	}

	@Test
	void tesPiIteratortEqualsAndHashCode() {
		PiIterator iterator1 = new PiIterator();
		iterator1.setTilePartOn(1);
		iterator1.setInclude(new int[] { 0, 1, 0 });
		iterator1.setStepL(2);
		iterator1.setStepR(3);
		iterator1.setStepC(4);
		iterator1.setStepP(5);
		iterator1.setCompNo(6);
		iterator1.setResNo(7);
		iterator1.setPrecNo(8);
		iterator1.setLayNo(9);
		iterator1.setFirst(0);
		iterator1.setNoOfComps(3);
		iterator1.setComps(new PiComponent[] { new PiComponent() });

		PiIterator iterator2 = new PiIterator();
		iterator2.setTilePartOn(1);
		iterator2.setInclude(new int[] { 0, 1, 0 });
		iterator2.setStepL(2);
		iterator2.setStepR(3);
		iterator2.setStepC(4);
		iterator2.setStepP(5);
		iterator2.setCompNo(6);
		iterator2.setResNo(7);
		iterator2.setPrecNo(8);
		iterator2.setLayNo(9);
		iterator2.setFirst(0);
		iterator2.setNoOfComps(3);
		iterator2.setComps(new PiComponent[] { new PiComponent() });

		// Test equality
		assertEquals(iterator1, iterator2);
		assertEquals(iterator1.hashCode(), iterator2.hashCode());

		// Change a field in iterator2
		iterator2.setFirst(1);
		assertNotEquals(iterator1, iterator2);
		assertNotEquals(iterator1.hashCode(), iterator2.hashCode());
	}

	@Test
	void testPiIteratorCanEqual() {
		PiIterator iterator = new PiIterator();
		assertTrue(iterator.canEqual(new PiIterator()));

		Object nonPiIterator = new Object();
		assertFalse(iterator.canEqual(nonPiIterator));
	}

	@Test
	void testPiIteratorToString() {
		PiIterator iterator = new PiIterator();
		iterator.setTilePartOn(1);
		iterator.setStepL(2);
		iterator.setNoOfComps(3);
		String toStringOutput = iterator.toString();
		assertTrue(toStringOutput.contains("tilePartOn=1"));
		assertTrue(toStringOutput.contains("stepL=2"));
		assertTrue(toStringOutput.contains("noOfComps=3"));
	}

	@Test
	void testPiResolutionEqualsAndHashCode() {
		PiResolution resolution1 = new PiResolution();
		resolution1.setPDX(10);
		resolution1.setPDY(20);
		resolution1.setPWidth(100);
		resolution1.setPHeight(200);

		PiResolution resolution2 = new PiResolution();
		resolution2.setPDX(10);
		resolution2.setPDY(20);
		resolution2.setPWidth(100);
		resolution2.setPHeight(200);

		// Test equality
		assertEquals(resolution1, resolution2);
		assertEquals(resolution1.hashCode(), resolution2.hashCode());

		// Change a field in resolution2
		resolution2.setPHeight(250);
		assertNotEquals(resolution1, resolution2);
		assertNotEquals(resolution1.hashCode(), resolution2.hashCode());
	}

	@Test
	void testPiResolutionCanEqual() {
		PiResolution resolution = new PiResolution();
		assertTrue(resolution.canEqual(new PiResolution()));

		Object nonPiResolution = new Object();
		assertFalse(resolution.canEqual(nonPiResolution));
	}

	@Test
	void testPiResolutionToString() {
		PiResolution resolution = new PiResolution();
		resolution.setPDX(10);
		resolution.setPDY(20);
		resolution.setPWidth(100);
		resolution.setPHeight(200);
		String toStringOutput = resolution.toString();
		assertTrue(toStringOutput.contains("pDX=10"));
		assertTrue(toStringOutput.contains("pDY=20"));
		assertTrue(toStringOutput.contains("pWidth=100"));
		assertTrue(toStringOutput.contains("pHeight=200"));
	}

	@Test
	void testPocEqualsAndHashCode() {
		Poc poc1 = new Poc();
		poc1.setResNo0(1);
		poc1.setCompNo0(2);
		poc1.setLayNo1(3);
		poc1.setResNo1(4);
		poc1.setCompNo1(5);
		poc1.setLayNo0(6);
		poc1.setPrecNo0(7);
		poc1.setPrecNo1(8);
		poc1.setTile(9);
		poc1.setTX0(10);
		poc1.setTX1(11);
		poc1.setTY0(12);
		poc1.setTY1(13);
		poc1.setLayS(14);
		poc1.setResS(15);
		poc1.setCompS(16);
		poc1.setPrcS(17);
		poc1.setLayE(18);
		poc1.setResE(19);
		poc1.setCompE(20);
		poc1.setPrcE(21);
		poc1.setTXS(22);
		poc1.setTXE(23);
		poc1.setTYS(24);
		poc1.setTYE(25);
		poc1.setDX(26);
		poc1.setDY(27);
		poc1.setLayTmp(28);
		poc1.setResTmp(29);
		poc1.setCompTmp(30);
		poc1.setPrcTmp(31);
		poc1.setTx0Tmp(32);
		poc1.setTy0Tmp(33);
		poc1.setProgressionName(new char[] { 'A', 'B', 'C', 'D', 'E' });

		Poc poc2 = new Poc();
		poc2.setResNo0(1);
		poc2.setCompNo0(2);
		poc2.setLayNo1(3);
		poc2.setResNo1(4);
		poc2.setCompNo1(5);
		poc2.setLayNo0(6);
		poc2.setPrecNo0(7);
		poc2.setPrecNo1(8);
		poc2.setTile(9);
		poc2.setTX0(10);
		poc2.setTX1(11);
		poc2.setTY0(12);
		poc2.setTY1(13);
		poc2.setLayS(14);
		poc2.setResS(15);
		poc2.setCompS(16);
		poc2.setPrcS(17);
		poc2.setLayE(18);
		poc2.setResE(19);
		poc2.setCompE(20);
		poc2.setPrcE(21);
		poc2.setTXS(22);
		poc2.setTXE(23);
		poc2.setTYS(24);
		poc2.setTYE(25);
		poc2.setDX(26);
		poc2.setDY(27);
		poc2.setLayTmp(28);
		poc2.setResTmp(29);
		poc2.setCompTmp(30);
		poc2.setPrcTmp(31);
		poc2.setTx0Tmp(32);
		poc2.setTy0Tmp(33);
		poc2.setProgressionName(new char[] { 'A', 'B', 'C', 'D', 'E' });

		// Test equality
		assertEquals(poc1, poc2);
		assertEquals(poc1.hashCode(), poc2.hashCode());

		// Change a field in poc2
		poc2.setTile(50);
		assertNotEquals(poc1, poc2);
		assertNotEquals(poc1.hashCode(), poc2.hashCode());
	}

	@Test
	void testPocCanEqual() {
		Poc poc = new Poc();
		assertTrue(poc.canEqual(new Poc()));

		Object nonPoc = new Object();
		assertFalse(poc.canEqual(nonPoc));
	}

	@Test
	void testPocToString() {
		Poc poc = new Poc();
		poc.setResNo0(1);
		poc.setCompNo0(2);
		String toStringOutput = poc.toString();
		assertTrue(toStringOutput.contains("resNo0=1"));
		assertTrue(toStringOutput.contains("compNo0=2"));
	}

	@Test
	void testRawEqualsAndHashCode() {
		Raw raw1 = new Raw();
		raw1.setC(10);
		raw1.setCt(20L);
		raw1.setLengthMax(30L);
		raw1.setLength(40L);
		raw1.setBpIndex(5);
		raw1.setBp(new byte[] { 1, 2, 3 });
		raw1.setStart(0);
		raw1.setEnd(10);

		Raw raw2 = new Raw();
		raw2.setC(10);
		raw2.setCt(20L);
		raw2.setLengthMax(30L);
		raw2.setLength(40L);
		raw2.setBpIndex(5);
		raw2.setBp(new byte[] { 1, 2, 3 });
		raw2.setStart(0);
		raw2.setEnd(10);

		// Test equality
		assertEquals(raw1, raw2);
		assertEquals(raw1.hashCode(), raw2.hashCode());

		// Change a field in raw2
		raw2.setC(50);
		assertNotEquals(raw1, raw2);
		assertNotEquals(raw1.hashCode(), raw2.hashCode());
	}

	@Test
	void testRawCanEqual() {
		Raw raw = new Raw();
		assertTrue(raw.canEqual(new Raw()));

		Object nonRaw = new Object();
		assertFalse(raw.canEqual(nonRaw));
	}

	@Test
	void testRawToString() {
		Raw raw = new Raw();
		raw.setC(10);
		String toStringOutput = raw.toString();
		assertTrue(toStringOutput.contains("c=10"));
	}

	@Test
	void testStepSizeEqualsAndHashCode() {
		StepSize stepSize1 = new StepSize();
		stepSize1.setExpn(3);
		stepSize1.setMant(5);

		StepSize stepSize2 = new StepSize();
		stepSize2.setExpn(3);
		stepSize2.setMant(5);

		// Test equality
		assertEquals(stepSize1, stepSize2);
		assertEquals(stepSize1.hashCode(), stepSize2.hashCode());

		// Change a field in stepSize2
		stepSize2.setMant(10);
		assertNotEquals(stepSize1, stepSize2);
		assertNotEquals(stepSize1.hashCode(), stepSize2.hashCode());
	}

	@Test
	void testStepSizeCanEqual() {
		StepSize stepSize = new StepSize();
		assertTrue(stepSize.canEqual(new StepSize()));

		Object nonStepSize = new Object();
		assertFalse(stepSize.canEqual(nonStepSize));
	}

	@Test
	void testStepSizeToString() {
		StepSize stepSize = new StepSize();
		stepSize.setExpn(3);
		stepSize.setMant(5);
		String toStringOutput = stepSize.toString();
		assertTrue(toStringOutput.contains("expn=3"));
		assertTrue(toStringOutput.contains("mant=5"));
	}

	@Test
	void TcdtestEquals() {
		Tcd tcd1 = new Tcd();
		tcd1.setTilePartPosition(1);
		tcd1.setTilePartNo(2);

		Tcd tcd2 = new Tcd();
		tcd2.setTilePartPosition(1);
		tcd2.setTilePartNo(2);

		assertEquals(tcd1, tcd2);
	}

	@Test
	void testTcdHashCode() {
		Tcd tcd1 = new Tcd();
		tcd1.setTilePartPosition(1);
		tcd1.setTilePartNo(2);

		Tcd tcd2 = new Tcd();
		tcd2.setTilePartPosition(1);
		tcd2.setTilePartNo(2);

		assertEquals(tcd1.hashCode(), tcd2.hashCode());
	}

	@Test
	void testTcdCanEqual() {
		Tcd tcd1 = new Tcd();
		assertTrue(tcd1.canEqual(new Tcd()));
	}

	@Test
	void testTcdToString() {
		Tcd tcd = new Tcd();
		tcd.setTilePartPosition(1);
		tcd.setTilePartNo(2);
		tcd.setCurTilePartNo(3);
		tcd.setCurTotalNoOfTileParts(4);
		tcd.setCurPiNo(5);
		tcd.setTcdTileNo(6);
		tcd.setEncodingTime(10.5);

		String toStringOutput = tcd.toString();
		assertTrue(toStringOutput.contains("tcdTileNo=6"));
		assertTrue(toStringOutput.contains("tilePartNo=2"));
	}

	@Test
	void testTcdBandEquals() {
		TcdBand tcdBand1 = new TcdBand();
		tcdBand1.setX0(10);
		tcdBand1.setY0(20);
		tcdBand1.setX1(30);
		tcdBand1.setY1(40);
		tcdBand1.setBandNo(1);
		tcdBand1.setNoOfBps(8);
		tcdBand1.setStepSize(0.5f);
		tcdBand1.setPrecincts(new TcdPrecinct[] { new TcdPrecinct() });

		TcdBand tcdBand2 = new TcdBand();
		tcdBand2.setX0(10);
		tcdBand2.setY0(20);
		tcdBand2.setX1(30);
		tcdBand2.setY1(40);
		tcdBand2.setBandNo(1);
		tcdBand2.setNoOfBps(8);
		tcdBand2.setStepSize(0.5f);
		tcdBand2.setPrecincts(new TcdPrecinct[] { new TcdPrecinct() });

		assertEquals(tcdBand1, tcdBand2);
	}

	@Test
	void testTcdBandHashCode() {
		TcdBand tcdBand1 = new TcdBand();
		tcdBand1.setX0(10);
		tcdBand1.setY0(20);
		tcdBand1.setX1(30);
		tcdBand1.setY1(40);
		tcdBand1.setBandNo(1);
		tcdBand1.setNoOfBps(8);
		tcdBand1.setStepSize(0.5f);
		tcdBand1.setPrecincts(new TcdPrecinct[] { new TcdPrecinct() });

		TcdBand tcdBand2 = new TcdBand();
		tcdBand2.setX0(10);
		tcdBand2.setY0(20);
		tcdBand2.setX1(30);
		tcdBand2.setY1(40);
		tcdBand2.setBandNo(1);
		tcdBand2.setNoOfBps(8);
		tcdBand2.setStepSize(0.5f);
		tcdBand2.setPrecincts(new TcdPrecinct[] { new TcdPrecinct() });

		assertEquals(tcdBand1.hashCode(), tcdBand2.hashCode());
	}

	@Test
	void testTcdBandCanEqual() {
		TcdBand tcdBand1 = new TcdBand();
		assertTrue(tcdBand1.canEqual(new TcdBand()));
	}

	@Test
	void testTcdBandToString() {
		TcdBand tcdBand = new TcdBand();
		tcdBand.setX0(10);
		tcdBand.setY0(20);
		tcdBand.setX1(30);
		tcdBand.setY1(40);
		tcdBand.setBandNo(1);
		tcdBand.setNoOfBps(8);
		tcdBand.setStepSize(0.5f);
		tcdBand.setPrecincts(new TcdPrecinct[] { new TcdPrecinct() });

		String toStringOutput = tcdBand.toString();
		assertTrue(toStringOutput.contains("x0=10"));
	}

	@Test
	void testTcdCodeBlockDecoderEquals() {
		TcdCodeBlockDecoder block1 = new TcdCodeBlockDecoder();
		block1.setX0(10);
		block1.setY0(20);
		block1.setX1(30);
		block1.setY1(40);
		block1.setNoOfBps(8);
		block1.setNoOfLengthBits(16);
		block1.setLength(100);
		block1.setNoOfNewPasses(3);
		block1.setNoOfSegs(2);
		block1.setData(new byte[] { 1, 2, 3 });
		block1.setSegs(new TcdSegment[] { new TcdSegment() });

		TcdCodeBlockDecoder block2 = new TcdCodeBlockDecoder();
		block2.setX0(10);
		block2.setY0(20);
		block2.setX1(30);
		block2.setY1(40);
		block2.setNoOfBps(8);
		block2.setNoOfLengthBits(16);
		block2.setLength(100);
		block2.setNoOfNewPasses(3);
		block2.setNoOfSegs(2);
		block2.setData(new byte[] { 1, 2, 3 });
		block2.setSegs(new TcdSegment[] { new TcdSegment() });

		assertEquals(block1, block2);
	}

	@Test
	void testTcdCodeBlockDecoderHashCode() {
		TcdCodeBlockDecoder block1 = new TcdCodeBlockDecoder();
		block1.setX0(10);
		block1.setY0(20);
		block1.setX1(30);
		block1.setY1(40);
		block1.setNoOfBps(8);
		block1.setNoOfLengthBits(16);
		block1.setLength(100);
		block1.setNoOfNewPasses(3);
		block1.setNoOfSegs(2);
		block1.setData(new byte[] { 1, 2, 3 });
		block1.setSegs(new TcdSegment[] { new TcdSegment() });

		TcdCodeBlockDecoder block2 = new TcdCodeBlockDecoder();
		block2.setX0(10);
		block2.setY0(20);
		block2.setX1(30);
		block2.setY1(40);
		block2.setNoOfBps(8);
		block2.setNoOfLengthBits(16);
		block2.setLength(100);
		block2.setNoOfNewPasses(3);
		block2.setNoOfSegs(2);
		block2.setData(new byte[] { 1, 2, 3 });
		block2.setSegs(new TcdSegment[] { new TcdSegment() });

		assertEquals(block1.hashCode(), block2.hashCode());
	}

	@Test
	void testTcdCodeBlockDecoderCanEqual() {
		TcdCodeBlockDecoder block1 = new TcdCodeBlockDecoder();
		assertTrue(block1.canEqual(new TcdCodeBlockDecoder()));
	}

	@Test
	void testTcdCodeBlockDecoderToString() {
		TcdCodeBlockDecoder block = new TcdCodeBlockDecoder();
		block.setX0(10);
		block.setY0(20);
		block.setX1(30);
		block.setY1(40);
		block.setNoOfBps(8);
		block.setNoOfLengthBits(16);
		block.setLength(100);
		block.setNoOfNewPasses(3);
		block.setNoOfSegs(2);
		block.setData(new byte[] { 1, 2, 3 });
		block.setSegs(new TcdSegment[] { new TcdSegment() });

		String toStringOutput = block.toString();
		assertTrue(toStringOutput.contains("x0=10"));
		assertTrue(toStringOutput.contains("y0=20"));
	}

	@Test
	void testTcdCodeBlockEncoderEquals() {
		TcdCodeBlockEncoder encoder1 = new TcdCodeBlockEncoder();
		encoder1.setDataIndex(1);
		encoder1.setX0(10);
		encoder1.setY0(20);
		encoder1.setX1(30);
		encoder1.setY1(40);
		encoder1.setNoOfBps(8);
		encoder1.setNoOfLengthBits(16);
		encoder1.setNoOfPasses(2);
		encoder1.setNoOfPassesInLayers(1);
		encoder1.setTotalPasses(3);
		encoder1.setData(new byte[] { 1, 2, 3 });
		encoder1.setLayers(new TcdLayer[] { new TcdLayer() });
		encoder1.setPasses(new TcdPass[] { new TcdPass() });

		TcdCodeBlockEncoder encoder2 = new TcdCodeBlockEncoder();
		encoder2.setDataIndex(1);
		encoder2.setX0(10);
		encoder2.setY0(20);
		encoder2.setX1(30);
		encoder2.setY1(40);
		encoder2.setNoOfBps(8);
		encoder2.setNoOfLengthBits(16);
		encoder2.setNoOfPasses(2);
		encoder2.setNoOfPassesInLayers(1);
		encoder2.setTotalPasses(3);
		encoder2.setData(new byte[] { 1, 2, 3 });
		encoder2.setLayers(new TcdLayer[] { new TcdLayer() });
		encoder2.setPasses(new TcdPass[] { new TcdPass() });

		assertEquals(encoder1, encoder2);
	}

	@Test
	void testTcdCodeBlockEncoderHashCode() {
		TcdCodeBlockEncoder encoder1 = new TcdCodeBlockEncoder();
		encoder1.setDataIndex(1);
		encoder1.setX0(10);
		encoder1.setY0(20);
		encoder1.setX1(30);
		encoder1.setY1(40);
		encoder1.setNoOfBps(8);
		encoder1.setNoOfLengthBits(16);
		encoder1.setNoOfPasses(2);
		encoder1.setNoOfPassesInLayers(1);
		encoder1.setTotalPasses(3);
		encoder1.setData(new byte[] { 1, 2, 3 });
		encoder1.setLayers(new TcdLayer[] { new TcdLayer() });
		encoder1.setPasses(new TcdPass[] { new TcdPass() });

		TcdCodeBlockEncoder encoder2 = new TcdCodeBlockEncoder();
		encoder2.setDataIndex(1);
		encoder2.setX0(10);
		encoder2.setY0(20);
		encoder2.setX1(30);
		encoder2.setY1(40);
		encoder2.setNoOfBps(8);
		encoder2.setNoOfLengthBits(16);
		encoder2.setNoOfPasses(2);
		encoder2.setNoOfPassesInLayers(1);
		encoder2.setTotalPasses(3);
		encoder2.setData(new byte[] { 1, 2, 3 });
		encoder2.setLayers(new TcdLayer[] { new TcdLayer() });
		encoder2.setPasses(new TcdPass[] { new TcdPass() });

		assertEquals(encoder1.hashCode(), encoder2.hashCode());
	}

	@Test
	void testTcdCodeBlockEncoderCanEqual() {
		TcdCodeBlockEncoder encoder = new TcdCodeBlockEncoder();
		assertTrue(encoder.canEqual(new TcdCodeBlockEncoder()));
	}

	@Test
	void testTcdCodeBlockEncoderToString() {
		TcdCodeBlockEncoder encoder = new TcdCodeBlockEncoder();
		encoder.setDataIndex(1);
		encoder.setX0(10);
		encoder.setY0(20);
		encoder.setX1(30);
		encoder.setY1(40);
		encoder.setNoOfBps(8);
		encoder.setNoOfLengthBits(16);
		encoder.setNoOfPasses(2);
		encoder.setNoOfPassesInLayers(1);
		encoder.setTotalPasses(3);
		encoder.setData(new byte[] { 1, 2, 3 });
		encoder.setLayers(new TcdLayer[] { new TcdLayer() });
		encoder.setPasses(new TcdPass[] { new TcdPass() });

		String toStringOutput = encoder.toString();
		assertTrue(toStringOutput.contains("dataIndex=1"));
		assertTrue(toStringOutput.contains("x0=10"));
	}

	@Test
	void testTcdImageEquals() {
		TcdImage image1 = new TcdImage();
		image1.setTileWidth(1024);
		image1.setTileHeight(768);
		image1.setTiles(new TcdTile[] { new TcdTile() });

		TcdImage image2 = new TcdImage();
		image2.setTileWidth(1024);
		image2.setTileHeight(768);
		image2.setTiles(new TcdTile[] { new TcdTile() });

		assertEquals(image1, image2);
	}

	@Test
	void testTcdImageHashCode() {
		TcdImage image1 = new TcdImage();
		image1.setTileWidth(1024);
		image1.setTileHeight(768);
		image1.setTiles(new TcdTile[] { new TcdTile() });

		TcdImage image2 = new TcdImage();
		image2.setTileWidth(1024);
		image2.setTileHeight(768);
		image2.setTiles(new TcdTile[] { new TcdTile() });

		assertEquals(image1.hashCode(), image2.hashCode());
	}

	@Test
	void testTcdImageCanEqual() {
		TcdImage image = new TcdImage();
		assertTrue(image.canEqual(new TcdImage()));
	}

	@Test
	void testTcdImageToString() {
		TcdImage image = new TcdImage();
		image.setTileWidth(1024);
		image.setTileHeight(768);
		image.setTiles(new TcdTile[] { new TcdTile() });

		String toStringOutput = image.toString();
		assertTrue(toStringOutput.contains("tileWidth=1024"));
		assertTrue(toStringOutput.contains("tileHeight=768"));
	}

	@Test
	void testTcdLayerEquals() {
		TcdLayer layer1 = new TcdLayer();
		layer1.setNoOfPasses(5);
		layer1.setLength(100);
		layer1.setDistortion(0.01);
		layer1.setData(new byte[] { 1, 2, 3 });

		TcdLayer layer2 = new TcdLayer();
		layer2.setNoOfPasses(5);
		layer2.setLength(100);
		layer2.setDistortion(0.01);
		layer2.setData(new byte[] { 1, 2, 3 });

		assertEquals(layer1, layer2);
	}

	@Test
	void testTcdLayerHashCode() {
		TcdLayer layer1 = new TcdLayer();
		layer1.setNoOfPasses(5);
		layer1.setLength(100);
		layer1.setDistortion(0.01);
		layer1.setData(new byte[] { 1, 2, 3 });

		TcdLayer layer2 = new TcdLayer();
		layer2.setNoOfPasses(5);
		layer2.setLength(100);
		layer2.setDistortion(0.01);
		layer2.setData(new byte[] { 1, 2, 3 });

		assertEquals(layer1.hashCode(), layer2.hashCode());
	}

	@Test
	void testTcdLayerCanEqual() {
		TcdLayer layer = new TcdLayer();
		assertTrue(layer.canEqual(new TcdLayer()));
	}

	@Test
	void testTcdLayerToString() {
		TcdLayer layer = new TcdLayer();
		layer.setNoOfPasses(5);
		layer.setLength(100);
		layer.setDistortion(0.01);
		layer.setData(new byte[] { 1, 2, 3 });

		String expected = "TcdLayer(noOfPasses=5, length=100, distortion=0.01, data=[1, 2, 3])";
		assertEquals(expected, layer.toString());
	}

	@Test
	void testTcdPassEquals() {
		TcdPass pass1 = new TcdPass();
		pass1.setRate(10);
		pass1.setDistortionDec(0.1);
		pass1.setTerm(5);
		pass1.setLength(100);

		TcdPass pass2 = new TcdPass();
		pass2.setRate(10);
		pass2.setDistortionDec(0.1);
		pass2.setTerm(5);
		pass2.setLength(100);

		assertEquals(pass1, pass2);
	}

	@Test
	void testTcdPassHashCode() {
		TcdPass pass1 = new TcdPass();
		pass1.setRate(10);
		pass1.setDistortionDec(0.1);
		pass1.setTerm(5);
		pass1.setLength(100);

		TcdPass pass2 = new TcdPass();
		pass2.setRate(10);
		pass2.setDistortionDec(0.1);
		pass2.setTerm(5);
		pass2.setLength(100);

		assertEquals(pass1.hashCode(), pass2.hashCode());
	}

	@Test
	void testTcdPassCanEqual() {
		TcdPass pass = new TcdPass();
		assertTrue(pass.canEqual(new TcdPass()));
	}

	@Test
	void testTcdPassToString() {
		TcdPass pass = new TcdPass();
		pass.setRate(10);
		pass.setDistortionDec(0.1);
		pass.setTerm(5);
		pass.setLength(100);

		String expected = "TcdPass(rate=10, distortionDec=0.1, term=5, length=100)";
		assertEquals(expected, pass.toString());
	}

	@Test
	void testTcdPrecinctEquals() {
		TcdPrecinct precinct1 = new TcdPrecinct();
		precinct1.setX0(1);
		precinct1.setY0(2);
		precinct1.setX1(3);
		precinct1.setCWidth(4);
		precinct1.setCHeight(5);

		TcdPrecinct precinct2 = new TcdPrecinct();
		precinct2.setX0(1);
		precinct2.setY0(2);
		precinct2.setX1(3);
		precinct2.setCWidth(4);
		precinct2.setCHeight(5);

		assertEquals(precinct1, precinct2);
	}

	@Test
	void testTcdPrecinctHashCode() {
		TcdPrecinct precinct1 = new TcdPrecinct();
		precinct1.setX0(1);
		precinct1.setY0(2);
		precinct1.setX1(3);
		precinct1.setCWidth(4);
		precinct1.setCHeight(5);

		TcdPrecinct precinct2 = new TcdPrecinct();
		precinct2.setX0(1);
		precinct2.setY0(2);
		precinct2.setX1(3);
		precinct2.setCWidth(4);
		precinct2.setCHeight(5);

		assertEquals(precinct1.hashCode(), precinct2.hashCode());
	}

	@Test
	void testTcdPrecinctCanEqual() {
		TcdPrecinct precinct = new TcdPrecinct();
		assertTrue(precinct.canEqual(new TcdPrecinct()));
	}

	@Test
	void testTcdPrecinctToString() {
		TcdPrecinct precinct = new TcdPrecinct();
		precinct.setX0(1);
		precinct.setY0(2);
		precinct.setX1(3);
		precinct.setCWidth(4);
		precinct.setCHeight(5);

		String toStringOutput = precinct.toString();
		assertTrue(toStringOutput.contains("x0=1"));
		assertTrue(toStringOutput.contains("y0=2"));
	}

	@Test
	void testTcdResolutionEquals() {
		TcdResolution resolution1 = new TcdResolution();
		resolution1.setX0(1);
		resolution1.setY0(2);
		resolution1.setX1(3);
		resolution1.setY1(4);
		resolution1.setPWidth(5);
		resolution1.setPHeight(6);
		resolution1.setNoOfBands(2);

		TcdResolution resolution2 = new TcdResolution();
		resolution2.setX0(1);
		resolution2.setY0(2);
		resolution2.setX1(3);
		resolution2.setY1(4);
		resolution2.setPWidth(5);
		resolution2.setPHeight(6);
		resolution2.setNoOfBands(2);

		assertEquals(resolution1, resolution2);
	}

	@Test
	void testTcdResolutionHashCode() {
		TcdResolution resolution1 = new TcdResolution();
		resolution1.setX0(1);
		resolution1.setY0(2);
		resolution1.setX1(3);
		resolution1.setY1(4);
		resolution1.setPWidth(5);
		resolution1.setPHeight(6);
		resolution1.setNoOfBands(2);

		TcdResolution resolution2 = new TcdResolution();
		resolution2.setX0(1);
		resolution2.setY0(2);
		resolution2.setX1(3);
		resolution2.setY1(4);
		resolution2.setPWidth(5);
		resolution2.setPHeight(6);
		resolution2.setNoOfBands(2);

		assertEquals(resolution1.hashCode(), resolution2.hashCode());
	}

	@Test
	void testTcdResolutionCanEqual() {
		TcdResolution resolution = new TcdResolution();
		assertTrue(resolution.canEqual(new TcdResolution()));
	}

	@Test
	void testTcdResolutionToString() {
		TcdResolution resolution = new TcdResolution();
		resolution.setX0(1);
		resolution.setY0(2);
		resolution.setX1(3);
		resolution.setY1(4);
		resolution.setPWidth(5);
		resolution.setPHeight(6);
		resolution.setNoOfBands(2);

		String expected = "TcdResolution(x0=1, y0=2, x1=3, y1=4, pWidth=5, pHeight=6, noOfBands=2, bands=[null, null, null])";
		assertEquals(expected, resolution.toString());
	}

	@Test
	void testTcdSegmentEquals() {
		TcdSegment segment1 = new TcdSegment();
		segment1.setData(new byte[] { 1, 2, 3 });
		segment1.setDataIndex(1);
		segment1.setNoOfPasses(2);
		segment1.setLength(3);
		segment1.setMaxPasses(4);
		segment1.setNoOfNewPasses(5);
		segment1.setNewLength(6);

		TcdSegment segment2 = new TcdSegment();
		segment2.setData(new byte[] { 1, 2, 3 });
		segment2.setDataIndex(1);
		segment2.setNoOfPasses(2);
		segment2.setLength(3);
		segment2.setMaxPasses(4);
		segment2.setNoOfNewPasses(5);
		segment2.setNewLength(6);

		assertEquals(segment1, segment2);
	}

	@Test
	void testTcdSegmentHashCode() {
		TcdSegment segment1 = new TcdSegment();
		segment1.setData(new byte[] { 1, 2, 3 });
		segment1.setDataIndex(1);
		segment1.setNoOfPasses(2);
		segment1.setLength(3);
		segment1.setMaxPasses(4);
		segment1.setNoOfNewPasses(5);
		segment1.setNewLength(6);

		TcdSegment segment2 = new TcdSegment();
		segment2.setData(new byte[] { 1, 2, 3 });
		segment2.setDataIndex(1);
		segment2.setNoOfPasses(2);
		segment2.setLength(3);
		segment2.setMaxPasses(4);
		segment2.setNoOfNewPasses(5);
		segment2.setNewLength(6);

		assertEquals(segment1.hashCode(), segment2.hashCode());
	}

	@Test
	void testTcdSegmentCanEqual() {
		TcdSegment segment1 = new TcdSegment();
		assertTrue(segment1.canEqual(new TcdSegment()));
	}

	@Test
	void testTcdSegmentToString() {
		TcdSegment segment = new TcdSegment();
		segment.setData(new byte[] { 1, 2, 3 });
		segment.setDataIndex(1);
		segment.setNoOfPasses(2);
		segment.setLength(3);
		segment.setMaxPasses(4);
		segment.setNoOfNewPasses(5);
		segment.setNewLength(6);

		String toStringOutput = segment.toString();
		assertTrue(toStringOutput.contains("dataIndex=1"));
		assertTrue(toStringOutput.contains("noOfPasses=2"));
	}

	@Test
	void testTcdTileEquals() {
		TcdTile tile1 = new TcdTile();
		tile1.setX0(0);
		tile1.setY0(0);
		tile1.setX1(100);
		tile1.setY1(100);
		tile1.setNoOfComps(3);
		tile1.setNoOfPixels(30000);
		tile1.setDistortionTile(0.01);
		tile1.setPacketNo(1);

		TcdTile tile2 = new TcdTile();
		tile2.setX0(0);
		tile2.setY0(0);
		tile2.setX1(100);
		tile2.setY1(100);
		tile2.setNoOfComps(3);
		tile2.setNoOfPixels(30000);
		tile2.setDistortionTile(0.01);
		tile2.setPacketNo(1);

		assertEquals(tile1, tile2);
	}

	@Test
	void testTcdTileHashCode() {
		TcdTile tile1 = new TcdTile();
		tile1.setX0(0);
		tile1.setY0(0);
		tile1.setX1(100);
		tile1.setY1(100);
		tile1.setNoOfComps(3);
		tile1.setNoOfPixels(30000);
		tile1.setDistortionTile(0.01);
		tile1.setPacketNo(1);

		TcdTile tile2 = new TcdTile();
		tile2.setX0(0);
		tile2.setY0(0);
		tile2.setX1(100);
		tile2.setY1(100);
		tile2.setNoOfComps(3);
		tile2.setNoOfPixels(30000);
		tile2.setDistortionTile(0.01);
		tile2.setPacketNo(1);

		assertEquals(tile1.hashCode(), tile2.hashCode());
	}

	@Test
	void testTcdTileCanEqual() {
		TcdTile tile1 = new TcdTile();
		assertTrue(tile1.canEqual(new TcdTile()));
	}

	@Test
	void testTcdTileToString() {
		TcdTile tile = new TcdTile();
		tile.setX0(0);
		tile.setY0(0);
		tile.setX1(100);
		tile.setY1(100);
		tile.setNoOfComps(3);

		String toStringOutput = tile.toString();
		assertTrue(toStringOutput.contains("x0=0"));
		assertTrue(toStringOutput.contains("y0=0"));
		assertTrue(toStringOutput.contains("x1=100"));
		assertTrue(toStringOutput.contains("y1=100"));
		assertTrue(toStringOutput.contains("noOfComps=3"));
	}

	@Test
	void testTcdTileComponentEquals() {
		TcdTileComponent component1 = new TcdTileComponent();
		component1.setX0(0);
		component1.setY0(0);
		component1.setX1(100);
		component1.setY1(100);
		component1.setNoOfResolutions(3);
		component1.setNoOfPixels(30000);
		component1.setIData(new int[] { 1, 2, 3 });
		component1.setFData(new double[] { 0.1, 0.2, 0.3 });

		TcdTileComponent component2 = new TcdTileComponent();
		component2.setX0(0);
		component2.setY0(0);
		component2.setX1(100);
		component2.setY1(100);
		component2.setNoOfResolutions(3);
		component2.setNoOfPixels(30000);
		component2.setIData(new int[] { 1, 2, 3 });
		component2.setFData(new double[] { 0.1, 0.2, 0.3 });

		assertEquals(component1, component2);
	}

	@Test
	void testTcdTileComponentHashCode() {
		TcdTileComponent component1 = new TcdTileComponent();
		component1.setX0(0);
		component1.setY0(0);
		component1.setX1(100);
		component1.setY1(100);
		component1.setNoOfResolutions(3);
		component1.setNoOfPixels(30000);
		component1.setIData(new int[] { 1, 2, 3 });
		component1.setFData(new double[] { 0.1, 0.2, 0.3 });

		TcdTileComponent component2 = new TcdTileComponent();
		component2.setX0(0);
		component2.setY0(0);
		component2.setX1(100);
		component2.setY1(100);
		component2.setNoOfResolutions(3);
		component2.setNoOfPixels(30000);
		component2.setIData(new int[] { 1, 2, 3 });
		component2.setFData(new double[] { 0.1, 0.2, 0.3 });

		assertEquals(component1.hashCode(), component2.hashCode());
	}

	@Test
	void testTcdTileComponentCanEqual() {
		TcdTileComponent component1 = new TcdTileComponent();
		assertTrue(component1.canEqual(new TcdTileComponent()));
	}

	@Test
	void testTcdTileComponentToString() {
		TcdTileComponent component = new TcdTileComponent();
		component.setX0(0);
		component.setY0(0);
		component.setX1(100);
		component.setY1(100);
		component.setNoOfResolutions(3);

		String toStringOutput = component.toString();
		assertTrue(toStringOutput.contains("x0=0"));
		assertTrue(toStringOutput.contains("y0=0"));
		assertTrue(toStringOutput.contains("x1=100"));
		assertTrue(toStringOutput.contains("y1=100"));
		assertTrue(toStringOutput.contains("noOfResolutions=3"));
	}

	@Test
	void testTcpEquals() {
		Tcp tcp1 = new Tcp();
		tcp1.setFirst(1);
		tcp1.setCodingStyle(2);
		tcp1.setNoOfLayers(3);
		tcp1.setMct(4);
		tcp1.setNoOfPocs(5);
		tcp1.setIsPoc(1);
		tcp1.setRates(new float[] { 1.0f, 2.0f, 3.0f });
		tcp1.setPocs(new Poc[] { new Poc() }); // assuming a constructor exists
		tcp1.setPptData(new byte[] { 0, 1, 2 });
		tcp1.setPptDataFirst(new byte[] { 3, 4, 5 });
		tcp1.setPpt(1);
		tcp1.setPptStore(0);
		tcp1.setPptLength(10);
		tcp1.setDistortionRatio(new float[] { 0.1f, 0.2f });

		Tcp tcp2 = new Tcp();
		tcp2.setFirst(1);
		tcp2.setCodingStyle(2);
		tcp2.setNoOfLayers(3);
		tcp2.setMct(4);
		tcp2.setNoOfPocs(5);
		tcp2.setIsPoc(1);
		tcp2.setRates(new float[] { 1.0f, 2.0f, 3.0f });
		tcp2.setPocs(new Poc[] { new Poc() }); // assuming a constructor exists
		tcp2.setPptData(new byte[] { 0, 1, 2 });
		tcp2.setPptDataFirst(new byte[] { 3, 4, 5 });
		tcp2.setPpt(1);
		tcp2.setPptStore(0);
		tcp2.setPptLength(10);
		tcp2.setDistortionRatio(new float[] { 0.1f, 0.2f });

		assertEquals(tcp1, tcp2);
	}

	@Test
	void testTcpHashCode() {
		Tcp tcp1 = new Tcp();
		tcp1.setFirst(1);
		tcp1.setCodingStyle(2);
		tcp1.setNoOfLayers(3);
		tcp1.setMct(4);
		tcp1.setNoOfPocs(5);
		tcp1.setIsPoc(1);
		tcp1.setRates(new float[] { 1.0f, 2.0f, 3.0f });
		tcp1.setPocs(new Poc[] { new Poc() }); // assuming a constructor exists
		tcp1.setPptData(new byte[] { 0, 1, 2 });
		tcp1.setPptDataFirst(new byte[] { 3, 4, 5 });
		tcp1.setPpt(1);
		tcp1.setPptStore(0);
		tcp1.setPptLength(10);
		tcp1.setDistortionRatio(new float[] { 0.1f, 0.2f });

		Tcp tcp2 = new Tcp();
		tcp2.setFirst(1);
		tcp2.setCodingStyle(2);
		tcp2.setNoOfLayers(3);
		tcp2.setMct(4);
		tcp2.setNoOfPocs(5);
		tcp2.setIsPoc(1);
		tcp2.setRates(new float[] { 1.0f, 2.0f, 3.0f });
		tcp2.setPocs(new Poc[] { new Poc() }); // assuming a constructor exists
		tcp2.setPptData(new byte[] { 0, 1, 2 });
		tcp2.setPptDataFirst(new byte[] { 3, 4, 5 });
		tcp2.setPpt(1);
		tcp2.setPptStore(0);
		tcp2.setPptLength(10);
		tcp2.setDistortionRatio(new float[] { 0.1f, 0.2f });

		assertEquals(tcp1.hashCode(), tcp2.hashCode());
	}

	@Test
	void testTcpCanEqual() {
		Tcp tcp1 = new Tcp();
		assertTrue(tcp1.canEqual(new Tcp()));
	}

	@Test
	void testTcpToString() {
		Tcp tcp = new Tcp();
		tcp.setFirst(1);
		tcp.setCodingStyle(2);
		tcp.setNoOfLayers(3);

		String toStringOutput = tcp.toString();
		assertTrue(toStringOutput.contains("first=1"));
		assertTrue(toStringOutput.contains("codingStyle=2"));
		assertTrue(toStringOutput.contains("noOfLayers=3"));
	}

	@Test
	void testTgtNodeEquals() {
		TgtNode node1 = new TgtNode();
		node1.setParent(1);
		node1.setValue(2);
		node1.setLow(3);
		node1.setKnown(4);

		TgtNode node2 = new TgtNode();
		node2.setParent(1);
		node2.setValue(2);
		node2.setLow(3);
		node2.setKnown(4);

		assertEquals(node1, node2);
	}

	@Test
	void testTgtNodeNotEquals() {
		TgtNode node1 = new TgtNode();
		node1.setParent(1);
		node1.setValue(2);
		node1.setLow(3);
		node1.setKnown(4);

		TgtNode node2 = new TgtNode();
		node2.setParent(2); // Different parent
		node2.setValue(2);
		node2.setLow(3);
		node2.setKnown(4);

		assertNotEquals(node1, node2);
	}

	@Test
	void testTgtNodeHashCode() {
		TgtNode node1 = new TgtNode();
		node1.setParent(1);
		node1.setValue(2);
		node1.setLow(3);
		node1.setKnown(4);

		TgtNode node2 = new TgtNode();
		node2.setParent(1);
		node2.setValue(2);
		node2.setLow(3);
		node2.setKnown(4);

		assertEquals(node1.hashCode(), node2.hashCode());
	}

	@Test
	void testTgtNodeCanEqual() {
		TgtNode node1 = new TgtNode();
		assertTrue(node1.canEqual(new TgtNode()));
	}

	@Test
	void testTgtNodeToString() {
		TgtNode node = new TgtNode();
		node.setParent(1);
		node.setValue(2);
		node.setLow(3);
		node.setKnown(4);

		String toStringOutput = node.toString();
		assertTrue(toStringOutput.contains("parent=1"));
		assertTrue(toStringOutput.contains("value=2"));
		assertTrue(toStringOutput.contains("low=3"));
		assertTrue(toStringOutput.contains("known=4"));
	}

	@Test
	void testTgtTreeEquals() {
		TgtNode node1 = new TgtNode();
		node1.setParent(1);
		node1.setValue(2);
		node1.setLow(3);
		node1.setKnown(4);

		TgtNode node2 = new TgtNode();
		node2.setParent(1);
		node2.setValue(2);
		node2.setLow(3);
		node2.setKnown(4);

		TgtTree tree1 = new TgtTree();
		tree1.setNoOfLeafSH(5);
		tree1.setNoOfLeafSV(10);
		tree1.setNoOfNodes(3);
		tree1.setNodes(new TgtNode[] { node1 });

		TgtTree tree2 = new TgtTree();
		tree2.setNoOfLeafSH(5);
		tree2.setNoOfLeafSV(10);
		tree2.setNoOfNodes(3);
		tree2.setNodes(new TgtNode[] { node2 });

		assertEquals(tree1, tree2);
	}

	@Test
	void testTgtTreeNotEquals() {
		TgtTree tree1 = new TgtTree();
		tree1.setNoOfLeafSH(5);
		tree1.setNoOfLeafSV(10);
		tree1.setNoOfNodes(3);
		tree1.setNodes(new TgtNode[1]);

		TgtTree tree2 = new TgtTree();
		tree2.setNoOfLeafSH(6); // Different noOfLeafSH
		tree2.setNoOfLeafSV(10);
		tree2.setNoOfNodes(3);
		tree2.setNodes(new TgtNode[1]);

		assertNotEquals(tree1, tree2);
	}

	@Test
	void testTgtTreeHashCode() {
		TgtNode node1 = new TgtNode();
		node1.setParent(1);
		node1.setValue(2);
		node1.setLow(3);
		node1.setKnown(4);

		TgtTree tree1 = new TgtTree();
		tree1.setNoOfLeafSH(5);
		tree1.setNoOfLeafSV(10);
		tree1.setNoOfNodes(3);
		tree1.setNodes(new TgtNode[] { node1 });

		TgtTree tree2 = new TgtTree();
		tree2.setNoOfLeafSH(5);
		tree2.setNoOfLeafSV(10);
		tree2.setNoOfNodes(3);
		tree2.setNodes(new TgtNode[] { node1 });

		assertEquals(tree1.hashCode(), tree2.hashCode());
	}

	@Test
	void testTgtTreeCanEqual() {
		TgtTree tree1 = new TgtTree();
		assertTrue(tree1.canEqual(new TgtTree()));
	}

	@Test
	void testTgtTreeToString() {
		TgtTree tree = new TgtTree();
		tree.setNoOfLeafSH(5);
		tree.setNoOfLeafSV(10);
		tree.setNoOfNodes(3);
		tree.setNodes(new TgtNode[1]);

		String toStringOutput = tree.toString();
		assertTrue(toStringOutput.contains("noOfLeafSH=5"));
		assertTrue(toStringOutput.contains("noOfLeafSV=10"));
		assertTrue(toStringOutput.contains("noOfNodes=3"));
	}

	@Test
	void testTier1Equals() {
		Tier1 tier1a = new Tier1();
		tier1a.setWidth(1920);
		tier1a.setHeight(1080);
		tier1a.setData(new int[] { 1, 2, 3 });
		tier1a.setFlags(new int[] { 0, 1, 0 });

		Tier1 tier1b = new Tier1();
		tier1b.setWidth(1920);
		tier1b.setHeight(1080);
		tier1b.setData(new int[] { 1, 2, 3 });
		tier1b.setFlags(new int[] { 0, 1, 0 });

		assertEquals(tier1a, tier1b);
	}

	@Test
	void testTier1NotEquals() {
		Tier1 tier1a = new Tier1();
		tier1a.setWidth(1920);
		tier1a.setHeight(1080);

		Tier1 tier1b = new Tier1();
		tier1b.setWidth(1280); // Different width

		assertNotEquals(tier1a, tier1b);
	}

	@Test
	void testTier1HashCode() {
		Tier1 tier1a = new Tier1();
		tier1a.setWidth(1920);
		tier1a.setHeight(1080);
		tier1a.setData(new int[] { 1, 2, 3 });
		tier1a.setFlags(new int[] { 0, 1, 0 });

		Tier1 tier1b = new Tier1();
		tier1b.setWidth(1920);
		tier1b.setHeight(1080);
		tier1b.setData(new int[] { 1, 2, 3 });
		tier1b.setFlags(new int[] { 0, 1, 0 });

		assertEquals(tier1a.hashCode(), tier1b.hashCode());
	}

	@Test
	void testTier1CanEqual() {
		Tier1 tier1a = new Tier1();
		assertTrue(tier1a.canEqual(new Tier1()));
	}

	@Test
	void testTier1ToString() {
		Tier1 tier1 = new Tier1();
		tier1.setWidth(1920);
		tier1.setHeight(1080);
		String toStringOutput = tier1.toString();
		assertTrue(toStringOutput.contains("width=1920"));
		assertTrue(toStringOutput.contains("height=1080"));
	}

	@Test
	void testTier2Equals() {
		Tier2 tier2a = new Tier2();
		tier2a.setCodecContextInfo(new CodecContextInfo());
		tier2a.setImage(new OpenJpegImage());
		tier2a.setCodingParameters(new CodingParameters());

		Tier2 tier2b = new Tier2();
		tier2b.setCodecContextInfo(new CodecContextInfo());
		tier2b.setImage(new OpenJpegImage());
		tier2b.setCodingParameters(new CodingParameters());

		assertEquals(tier2a, tier2b);
	}

	@Test
	void testTier2NotEquals() {
		Tier2 tier2a = new Tier2();
		tier2a.setCodecContextInfo(new CodecContextInfo());

		Tier2 tier2b = new Tier2();
		tier2b.setCodecContextInfo(null); // Different codec context

		assertNotEquals(tier2a, tier2b);
	}

	@Test
	void testTier2HashCode() {
		Tier2 tier2a = new Tier2();
		tier2a.setCodecContextInfo(new CodecContextInfo());
		tier2a.setImage(new OpenJpegImage());
		tier2a.setCodingParameters(new CodingParameters());

		Tier2 tier2b = new Tier2();
		tier2b.setCodecContextInfo(new CodecContextInfo());
		tier2b.setImage(new OpenJpegImage());
		tier2b.setCodingParameters(new CodingParameters());

		assertEquals(tier2a.hashCode(), tier2b.hashCode());
	}

	@Test
	void testTier2CanEqual() {
		Tier2 tier2a = new Tier2();
		assertTrue(tier2a.canEqual(new Tier2()));
	}

	@Test
	void testTier2ToString() {
		Tier2 tier2 = new Tier2();
		tier2.setCodecContextInfo(new CodecContextInfo());
		tier2.setImage(new OpenJpegImage());
		String toStringOutput = tier2.toString();
		assertTrue(toStringOutput.contains("codecContextInfo"));
		assertTrue(toStringOutput.contains("image"));
		assertTrue(toStringOutput.contains("codingParameters"));
	}

	@Test
	void testTileComponentCodingParametersEquals() {
		TileComponentCodingParameters params1 = new TileComponentCodingParameters();
		params1.setCodingStyle(1);
		params1.setNoOfResolutions(3);
		params1.setCodeBlockWidth(64);
		params1.setCodeBlockHeight(64);
		params1.setCodeBlockStyle(0);
		params1.setQmfbid(1);
		params1.setQuantisationStyle(2);
		params1.setNoOfGaurdBits(5);
		params1.setRoiShift(1);
		params1.setStepsizes(new StepSize[] { new StepSize() });
		params1.setPrecinctWidth(new int[] { 8, 16, 32 });
		params1.setPrecinctHeight(new int[] { 8, 16, 32 });

		TileComponentCodingParameters params2 = new TileComponentCodingParameters();
		params2.setCodingStyle(1);
		params2.setNoOfResolutions(3);
		params2.setCodeBlockWidth(64);
		params2.setCodeBlockHeight(64);
		params2.setCodeBlockStyle(0);
		params2.setQmfbid(1);
		params2.setQuantisationStyle(2);
		params2.setNoOfGaurdBits(5);
		params2.setRoiShift(1);
		params2.setStepsizes(new StepSize[] { new StepSize() });
		params2.setPrecinctWidth(new int[] { 8, 16, 32 });
		params2.setPrecinctHeight(new int[] { 8, 16, 32 });

		assertEquals(params1, params2);
	}

	@Test
	void testTileComponentCodingParametersNotEquals() {
		TileComponentCodingParameters params1 = new TileComponentCodingParameters();
		params1.setCodingStyle(1);
		params1.setNoOfResolutions(3);

		TileComponentCodingParameters params2 = new TileComponentCodingParameters();
		params2.setCodingStyle(2); // Different coding style

		assertNotEquals(params1, params2);
	}

	@Test
	void testTileComponentCodingParametersHashCode() {
		TileComponentCodingParameters params1 = new TileComponentCodingParameters();
		params1.setCodingStyle(1);
		params1.setNoOfResolutions(3);

		TileComponentCodingParameters params2 = new TileComponentCodingParameters();
		params2.setCodingStyle(1);
		params2.setNoOfResolutions(3);

		assertEquals(params1.hashCode(), params2.hashCode());
	}

	@Test
	void testTileComponentCodingParametersCanEqual() {
		TileComponentCodingParameters params1 = new TileComponentCodingParameters();
		assertTrue(params1.canEqual(new TileComponentCodingParameters()));
	}

	@Test
	void testTileComponentCodingParametersToString() {
		TileComponentCodingParameters params = new TileComponentCodingParameters();
		params.setCodingStyle(1);
		params.setNoOfResolutions(3);
		String toStringOutput = params.toString();
		assertTrue(toStringOutput.contains("codingStyle=1"));
		assertTrue(toStringOutput.contains("noOfResolutions=3"));
	}

	@Test
	void testTileInfoEquals() {
		TileInfo tileInfo1 = new TileInfo();
		tileInfo1.setTileNo(1);
		tileInfo1.setStartPosition(0);
		tileInfo1.setEndHeader(100);
		tileInfo1.setEndPosition(200);
		tileInfo1.setThresh(new double[] { 0.5, 1.0 });
		tileInfo1.setPWidth(new int[] { 8, 16, 32 });
		tileInfo1.setPHeight(new int[] { 8, 16, 32 });
		tileInfo1.setPDX(new int[] { 2, 3, 4 });
		tileInfo1.setPDY(new int[] { 2, 3, 4 });
		tileInfo1.setNoOfPixel(150);
		tileInfo1.setDistortionTile(0.05);
		tileInfo1.setNoOfTileParts(2);
		tileInfo1.setPacket(new PacketInfo[2]);
		tileInfo1.setTp(new TpInfo[2]);

		TileInfo tileInfo2 = new TileInfo();
		tileInfo2.setTileNo(1);
		tileInfo2.setStartPosition(0);
		tileInfo2.setEndHeader(100);
		tileInfo2.setEndPosition(200);
		tileInfo2.setThresh(new double[] { 0.5, 1.0 });
		tileInfo2.setPWidth(new int[] { 8, 16, 32 });
		tileInfo2.setPHeight(new int[] { 8, 16, 32 });
		tileInfo2.setPDX(new int[] { 2, 3, 4 });
		tileInfo2.setPDY(new int[] { 2, 3, 4 });
		tileInfo2.setNoOfPixel(150);
		tileInfo2.setDistortionTile(0.05);
		tileInfo2.setNoOfTileParts(2);
		tileInfo2.setPacket(new PacketInfo[2]);
		tileInfo2.setTp(new TpInfo[2]);

		assertEquals(tileInfo1, tileInfo2);
	}

	@Test
	void testTileInfoNotEquals() {
		TileInfo tileInfo1 = new TileInfo();
		tileInfo1.setTileNo(1);
		tileInfo1.setStartPosition(0);

		TileInfo tileInfo2 = new TileInfo();
		tileInfo2.setTileNo(2); // Different tile number

		assertNotEquals(tileInfo1, tileInfo2);
	}

	@Test
	void testTileInfoHashCode() {
		TileInfo tileInfo1 = new TileInfo();
		tileInfo1.setTileNo(1);
		tileInfo1.setStartPosition(0);

		TileInfo tileInfo2 = new TileInfo();
		tileInfo2.setTileNo(1);
		tileInfo2.setStartPosition(0);

		assertEquals(tileInfo1.hashCode(), tileInfo2.hashCode());
	}

	@Test
	void testTileInfoCanEqual() {
		TileInfo tileInfo1 = new TileInfo();
		assertTrue(tileInfo1.canEqual(new TileInfo()));
	}

	@Test
	void testTileInfoToString() {
		TileInfo tileInfo = new TileInfo();
		tileInfo.setTileNo(1);
		tileInfo.setStartPosition(0);
		String toStringOutput = tileInfo.toString();
		assertTrue(toStringOutput.contains("tileNo=1"));
		assertTrue(toStringOutput.contains("startPosition=0"));
	}

	@Test
	void testTpInfoEquals() {
		TpInfo tpInfo1 = new TpInfo();
		tpInfo1.setTpStartPosition(0);
		tpInfo1.setTpEndHeader(100);
		tpInfo1.setTpEndPosition(200);
		tpInfo1.setTpStartPacket(0);
		tpInfo1.setTpNoOfPackets(10);

		TpInfo tpInfo2 = new TpInfo();
		tpInfo2.setTpStartPosition(0);
		tpInfo2.setTpEndHeader(100);
		tpInfo2.setTpEndPosition(200);
		tpInfo2.setTpStartPacket(0);
		tpInfo2.setTpNoOfPackets(10);

		assertEquals(tpInfo1, tpInfo2);
	}

	@Test
	void testTpInfoNotEquals() {
		TpInfo tpInfo1 = new TpInfo();
		tpInfo1.setTpStartPosition(0);

		TpInfo tpInfo2 = new TpInfo();
		tpInfo2.setTpStartPosition(1); // Different tpStartPosition

		assertNotEquals(tpInfo1, tpInfo2);
	}

	@Test
	void testTpInfoHashCode() {
		TpInfo tpInfo1 = new TpInfo();
		tpInfo1.setTpStartPosition(0);
		tpInfo1.setTpEndHeader(100);
		tpInfo1.setTpEndPosition(200);
		tpInfo1.setTpStartPacket(0);
		tpInfo1.setTpNoOfPackets(10);

		TpInfo tpInfo2 = new TpInfo();
		tpInfo2.setTpStartPosition(0);
		tpInfo2.setTpEndHeader(100);
		tpInfo2.setTpEndPosition(200);
		tpInfo2.setTpStartPacket(0);
		tpInfo2.setTpNoOfPackets(10);

		assertEquals(tpInfo1.hashCode(), tpInfo2.hashCode());
	}

	@Test
	void testTpInfoCanEqual() {
		TpInfo tpInfo1 = new TpInfo();
		assertTrue(tpInfo1.canEqual(new TpInfo()));
	}

	@Test
	void testTpInfoToString() {
		TpInfo tpInfo = new TpInfo();
		tpInfo.setTpStartPosition(0);
		String toStringOutput = tpInfo.toString();
		assertTrue(toStringOutput.contains("tpStartPosition=0"));
	}

	@Test
	void testV4Equals() {
		V4 v4_1 = new V4();
		v4_1.setF(new double[] { 1.0, 2.0, 3.0, 4.0 });

		V4 v4_2 = new V4();
		v4_2.setF(new double[] { 1.0, 2.0, 3.0, 4.0 });

		assertEquals(v4_1, v4_2);
	}

	@Test
	void testV4NotEquals() {
		V4 v4_1 = new V4();
		v4_1.setF(new double[] { 1.0, 2.0, 3.0, 4.0 });

		V4 v4_2 = new V4();
		v4_2.setF(new double[] { 1.0, 2.0, 3.0, 5.0 }); // Different last element

		assertNotEquals(v4_1, v4_2);
	}

	@Test
	void testV4HashCode() {
		V4 v4_1 = new V4();
		v4_1.setF(new double[] { 1.0, 2.0, 3.0, 4.0 });

		V4 v4_2 = new V4();
		v4_2.setF(new double[] { 1.0, 2.0, 3.0, 4.0 });

		assertEquals(v4_1.hashCode(), v4_2.hashCode());
	}

	@Test
	void testV4CanEqual() {
		V4 v4 = new V4();
		assertTrue(v4.canEqual(new V4()));
	}

	@Test
	void testV4ToString() {
		V4 v4 = new V4();
		v4.setF(new double[] { 1.0, 2.0, 3.0, 4.0 });
		String toStringOutput = v4.toString();
		assertTrue(toStringOutput.contains("f=[1.0, 2.0, 3.0, 4.0]")); // Adjust as per the expected format
	}

	@Test
	void testJ2kStatusFromValueValid() {
		assertEquals(J2kStatus.J2K_STATE_MHSOC, J2kStatus.fromValue(0x0001));
		assertEquals(J2kStatus.J2K_STATE_MHSIZ, J2kStatus.fromValue(0x0002));
		assertEquals(J2kStatus.J2K_STATE_MH, J2kStatus.fromValue(0x0004));
		assertEquals(J2kStatus.J2K_STATE_TPHSOT, J2kStatus.fromValue(0x0008));
		assertEquals(J2kStatus.J2K_STATE_TPH, J2kStatus.fromValue(0x0010));
		assertEquals(J2kStatus.J2K_STATE_MT, J2kStatus.fromValue(0x0020));
		assertEquals(J2kStatus.J2K_STATE_NEOC, J2kStatus.fromValue(0x0040));
		assertEquals(J2kStatus.J2K_STATE_ERR, J2kStatus.fromValue(0x0080));
	}

	@Test
	void testJ2kStatusFromValueInvalid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			J2kStatus.fromValue(0x9999); // Invalid value
		});

		String expectedMessage = "No enum constant for value: 39321"; // 39321 is 0x9999 in decimal
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testJ2kStatusValue() {
		assertEquals(0x0001, J2kStatus.J2K_STATE_MHSOC.value());
		assertEquals(0x0002, J2kStatus.J2K_STATE_MHSIZ.value());
		assertEquals(0x0004, J2kStatus.J2K_STATE_MH.value());
		assertEquals(0x0008, J2kStatus.J2K_STATE_TPHSOT.value());
		assertEquals(0x0010, J2kStatus.J2K_STATE_TPH.value());
		assertEquals(0x0020, J2kStatus.J2K_STATE_MT.value());
		assertEquals(0x0040, J2kStatus.J2K_STATE_NEOC.value());
		assertEquals(0x0080, J2kStatus.J2K_STATE_ERR.value());
	}

	@Test
	void testJ2kStatusToString() {
		assertTrue(J2kStatus.J2K_STATE_MHSOC.toString().contains("J2K_STATE_MHSOC(1)"));
		assertTrue(J2kStatus.J2K_STATE_MHSIZ.toString().contains("J2K_STATE_MHSIZ(2)"));
		assertTrue(J2kStatus.J2K_STATE_MH.toString().contains("J2K_STATE_MH(4)"));
		assertTrue(J2kStatus.J2K_STATE_TPHSOT.toString().contains("J2K_STATE_TPHSOT(8)"));
		assertTrue(J2kStatus.J2K_STATE_TPH.toString().contains("J2K_STATE_TPH(10)"));
		assertTrue(J2kStatus.J2K_STATE_MT.toString().contains("J2K_STATE_MT(20)"));
		assertTrue(J2kStatus.J2K_STATE_NEOC.toString().contains("J2K_STATE_NEOC(40)"));
		assertTrue(J2kStatus.J2K_STATE_ERR.toString().contains("J2K_STATE_ERR(80)"));
	}

	@Test
	void testJ2KT2ModeFromValueValid() {
		assertEquals(J2KT2Mode.THRESH_CALC, J2KT2Mode.fromValue(0));
		assertEquals(J2KT2Mode.FINAL_PASS, J2KT2Mode.fromValue(1));
	}

	@Test
	void testJ2KT2ModeFromValueInvalid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			J2KT2Mode.fromValue(2); // Invalid value
		});

		String expectedMessage = "No enum constant for value: 2";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testJ2KT2ModeValue() {
		assertEquals(0, J2KT2Mode.THRESH_CALC.value());
		assertEquals(1, J2KT2Mode.FINAL_PASS.value());
	}

	@Test
	void testJ2KT2ModeToString() {
		assertTrue(J2KT2Mode.THRESH_CALC.toString().contains("THRESH_CALC(0)"));
		assertTrue(J2KT2Mode.FINAL_PASS.toString().contains("FINAL_PASS(1)"));
	}

	@Test
	void testJP2CinemeaModeFromValueValid() {
		assertEquals(JP2CinemeaMode.CINEMA2K_24, JP2CinemeaMode.fromValue(1));
		assertEquals(JP2CinemeaMode.CINEMA2K_48, JP2CinemeaMode.fromValue(2));
		assertEquals(JP2CinemeaMode.CINEMA4K_24, JP2CinemeaMode.fromValue(3));
		assertEquals(JP2CinemeaMode.OFF, JP2CinemeaMode.fromValue(0));
	}

	@Test
	void testJP2CinemeaModeFromValueInvalid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			JP2CinemeaMode.fromValue(99); // Invalid value
		});

		String expectedMessage = "No enum constant for value: 99";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testJP2CinemeaModeValue() {
		assertEquals(0, JP2CinemeaMode.OFF.value());
		assertEquals(1, JP2CinemeaMode.CINEMA2K_24.value());
		assertEquals(2, JP2CinemeaMode.CINEMA2K_48.value());
		assertEquals(3, JP2CinemeaMode.CINEMA4K_24.value());
	}

	@Test
	void testJP2CinemeaModeToString() {
		assertTrue(JP2CinemeaMode.OFF.toString().contains("OFF(0)"));
		assertTrue(JP2CinemeaMode.CINEMA2K_24.toString().contains("CINEMA2K_24(1)"));
		assertTrue(JP2CinemeaMode.CINEMA2K_48.toString().contains("CINEMA2K_48(2)"));
		assertTrue(JP2CinemeaMode.CINEMA4K_24.toString().contains("CINEMA4K_24(3)"));
	}

	@Test
	void testJP2CodecFormatFromValueValid() {
		assertEquals(JP2CodecFormat.CODEC_J2K, JP2CodecFormat.fromValue(0));
		assertEquals(JP2CodecFormat.CODEC_JPT, JP2CodecFormat.fromValue(1));
		assertEquals(JP2CodecFormat.CODEC_JP2, JP2CodecFormat.fromValue(2));
		assertEquals(JP2CodecFormat.CODEC_UNKNOWN, JP2CodecFormat.fromValue(-1));
	}

	@Test
	void testJP2CodecFormatFromValueInvalid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			JP2CodecFormat.fromValue(3); // Invalid value
		});

		String expectedMessage = "No enum constant for value: 3";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testJP2CodecFormatValue() {
		assertEquals(-1, JP2CodecFormat.CODEC_UNKNOWN.value());
		assertEquals(0, JP2CodecFormat.CODEC_J2K.value());
		assertEquals(1, JP2CodecFormat.CODEC_JPT.value());
		assertEquals(2, JP2CodecFormat.CODEC_JP2.value());
	}

	@Test
	void testJP2CodecFormatToString() {
		assertTrue(JP2CodecFormat.CODEC_J2K.toString().contains("CODEC_J2K(0)"));
		assertTrue(JP2CodecFormat.CODEC_JPT.toString().contains("CODEC_JPT(1)"));
		assertTrue(JP2CodecFormat.CODEC_JP2.toString().contains("CODEC_JP2(2)"));
	}

	@Test
	void testJp2ColorSpaceFromValueValid() {
		assertEquals(Jp2ColorSpace.CLRSPC_SRGB, Jp2ColorSpace.fromValue(1));
		assertEquals(Jp2ColorSpace.CLRSPC_GRAY, Jp2ColorSpace.fromValue(2));
		assertEquals(Jp2ColorSpace.CLRSPC_SYCC, Jp2ColorSpace.fromValue(3));
		assertEquals(Jp2ColorSpace.CLRSPC_UNKNOWN, Jp2ColorSpace.fromValue(-1));
	}

	@Test
	void testJp2ColorSpaceFromValueInvalid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			Jp2ColorSpace.fromValue(4); // Invalid value
		});

		String expectedMessage = "No enum constant for value: 4";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testJp2ColorSpaceValue() {
		assertEquals(-1, Jp2ColorSpace.CLRSPC_UNKNOWN.value());
		assertEquals(1, Jp2ColorSpace.CLRSPC_SRGB.value());
		assertEquals(2, Jp2ColorSpace.CLRSPC_GRAY.value());
		assertEquals(3, Jp2ColorSpace.CLRSPC_SYCC.value());
	}

	@Test
	void testJp2ColorSpaceToString() {
		assertTrue(Jp2ColorSpace.CLRSPC_SRGB.toString().contains("CLRSPC_SRGB(1)"));
		assertTrue(Jp2ColorSpace.CLRSPC_GRAY.toString().contains("CLRSPC_GRAY(2)"));
		assertTrue(Jp2ColorSpace.CLRSPC_SYCC.toString().contains("CLRSPC_SYCC(3)"));
	}

	@Test
	void testLimitDecodingFromValueValid() {
		assertEquals(LimitDecoding.NO_LIMITATION, LimitDecoding.fromValue(0));
		assertEquals(LimitDecoding.LIMIT_TO_MAIN_HEADER, LimitDecoding.fromValue(1));
		assertEquals(LimitDecoding.DECODE_ALL_BUT_PACKETS, LimitDecoding.fromValue(2));
	}

	@Test
	void testLimitDecodingFromValueInvalid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			LimitDecoding.fromValue(3); // Invalid value
		});

		String expectedMessage = "No enum constant for value: 3";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testLimitDecodingValue() {
		assertEquals(0, LimitDecoding.NO_LIMITATION.value());
		assertEquals(1, LimitDecoding.LIMIT_TO_MAIN_HEADER.value());
		assertEquals(2, LimitDecoding.DECODE_ALL_BUT_PACKETS.value());
	}

	@Test
	void testLimitDecodingToString() {
		assertTrue(LimitDecoding.NO_LIMITATION.toString().contains("NO_LIMITATION(0)"));
		assertTrue(LimitDecoding.LIMIT_TO_MAIN_HEADER.toString().contains("LIMIT_TO_MAIN_HEADER(1)"));
		assertTrue(LimitDecoding.DECODE_ALL_BUT_PACKETS.toString().contains("DECODE_ALL_BUT_PACKETS(2)"));
	}

	@Test
	void testProgressionOrderFromValueValid() {
		assertEquals(ProgressionOrder.LRCP, ProgressionOrder.fromValue(0));
		assertEquals(ProgressionOrder.RLCP, ProgressionOrder.fromValue(1));
		assertEquals(ProgressionOrder.RPCL, ProgressionOrder.fromValue(2));
		assertEquals(ProgressionOrder.PCRL, ProgressionOrder.fromValue(3));
		assertEquals(ProgressionOrder.CPRL, ProgressionOrder.fromValue(4));
	}

	@Test
	void testProgressionOrderFromValueInvalid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			ProgressionOrder.fromValue(5); // Invalid value
		});

		String expectedMessage = "No enum constant for value: 5";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testProgressionOrderValue() {
		assertEquals(0, ProgressionOrder.LRCP.value());
		assertEquals(1, ProgressionOrder.RLCP.value());
		assertEquals(2, ProgressionOrder.RPCL.value());
		assertEquals(3, ProgressionOrder.PCRL.value());
		assertEquals(4, ProgressionOrder.CPRL.value());
	}

	@Test
	void testProgressionOrderToString() {
		assertTrue(ProgressionOrder.LRCP.toString().contains("LRCP(0)"));
		assertTrue(ProgressionOrder.RLCP.toString().contains("RLCP(1)"));
		assertTrue(ProgressionOrder.RPCL.toString().contains("RPCL(2)"));
		assertTrue(ProgressionOrder.PCRL.toString().contains("PCRL(3)"));
		assertTrue(ProgressionOrder.CPRL.toString().contains("CPRL(4)"));
	}

	@Test
	void testRsizCapabilitiesFromValueValid() {
		assertEquals(RsizCapabilities.STD_RSIZ, RsizCapabilities.fromValue(0));
		assertEquals(RsizCapabilities.CINEMA2K, RsizCapabilities.fromValue(3));
		assertEquals(RsizCapabilities.CINEMA4K, RsizCapabilities.fromValue(4));
	}

	@Test
	void testRsizCapabilitiesFromValueInvalid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			RsizCapabilities.fromValue(5); // Invalid value
		});

		String expectedMessage = "No enum constant for value: 5";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testRsizCapabilitiesValue() {
		assertEquals(0, RsizCapabilities.STD_RSIZ.value());
		assertEquals(3, RsizCapabilities.CINEMA2K.value());
		assertEquals(4, RsizCapabilities.CINEMA4K.value());
	}

	@Test
	void testRsizCapabilitiesToString() {
		assertTrue(RsizCapabilities.STD_RSIZ.toString().contains("STD_RSIZ(0)"));
		assertTrue(RsizCapabilities.CINEMA2K.toString().contains("CINEMA2K(3)"));
		assertTrue(RsizCapabilities.CINEMA4K.toString().contains("CINEMA4K(4)"));
	}
}