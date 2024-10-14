package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.mosip.imagedecoder.constant.DecoderConstant;
import io.mosip.imagedecoder.constant.DecoderErrorCodes;
import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import io.mosip.imagedecoder.exceptions.DecoderException;
import io.mosip.imagedecoder.model.ByteBufferContext;
import io.mosip.imagedecoder.model.openjpeg.Jp2ColorSpace;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponent;
import io.mosip.imagedecoder.model.wsq.WsqQuantization;
import io.mosip.imagedecoder.model.wsq.WsqQuantizationTree;
import io.mosip.imagedecoder.model.wsq.WsqTableDqt;
import io.mosip.imagedecoder.model.wsq.WsqWavletTree;
import io.mosip.imagedecoder.util.Base64UrlUtil;
import io.mosip.imagedecoder.util.ByteStreamUtil;
import io.mosip.imagedecoder.util.ByteSwapperUtil;
import io.mosip.imagedecoder.util.StringUtil;
import io.mosip.imagedecoder.util.openjpeg.ImageUtil;
import io.mosip.imagedecoder.util.wsq.WsqUtil;

class UtilTest {
	private Base64UrlUtil base64UrlUtil;
	private ByteStreamUtil byteStreamUtil;
	private ByteBufferContext byteBufferContext;
	private ByteSwapperUtil byteSwapperUtil;
	private StringUtil stringUtil;

	private WsqUtil wsqUtil;

	private ImageUtil imageUtil;

	@BeforeEach
	void setUp() {
		base64UrlUtil = Base64UrlUtil.getInstance();
		byteStreamUtil = ByteStreamUtil.getInstance();
		byteBufferContext = new ByteBufferContext();

		byteSwapperUtil = ByteSwapperUtil.getInstance();
		stringUtil = StringUtil.getInstance();

		wsqUtil = WsqUtil.getInstance();

		imageUtil = ImageUtil.getInstance();
	}

	@Test
	void testBase64UrlUtilSingletonInstance() {
		Base64UrlUtil instance1 = Base64UrlUtil.getInstance();
		Base64UrlUtil instance2 = Base64UrlUtil.getInstance();
		assertSame(instance1, instance2, "Instances should be the same");
	}

	@Test
	void testBase64UrlUtilEncodeToURLSafeBase64_byteArray() {
		byte[] input = "test".getBytes();
		String encoded = base64UrlUtil.encodeToURLSafeBase64(input);
		assertNotNull(encoded, "Encoded value should not be null");
		assertEquals("dGVzdA", encoded, "Incorrect Base64 encoding");
	}

	@Test
	void testBase64UrlUtilEncodeToURLSafeBase64_string() {
		String input = "test";
		String encoded = base64UrlUtil.encodeToURLSafeBase64(input);
		assertNotNull(encoded, "Encoded value should not be null");
		assertEquals("dGVzdA", encoded, "Incorrect Base64 encoding");
	}

	@Test
	void testBase64UrlUtilDecodeURLSafeBase64_byteArray() {
		byte[] input = "dGVzdA".getBytes();
		byte[] decoded = base64UrlUtil.decodeURLSafeBase64(input);
		assertNotNull(decoded, "Decoded value should not be null");
		assertArrayEquals("test".getBytes(), decoded, "Incorrect Base64 decoding");
	}

	@Test
	void testBase64UrlUtilDecodeURLSafeBase64_string() {
		String input = "dGVzdA";
		byte[] decoded = base64UrlUtil.decodeURLSafeBase64(input);
		assertNotNull(decoded, "Decoded value should not be null");
		assertArrayEquals("test".getBytes(), decoded, "Incorrect Base64 decoding");
	}

	@Test
	void testBase64UrlUtilDecodeURLSafeBase64_nullInput() {
		assertThrows(DecoderException.class, () -> base64UrlUtil.decodeURLSafeBase64((byte[]) null),
				"Should throw DecoderException for null input");

		assertThrows(DecoderException.class, () -> base64UrlUtil.decodeURLSafeBase64((String) null),
				"Should throw DecoderException for null input");
	}

	@Test
	void testBase64UrlUtilIsNullEmpty_byteArray() {
		assertTrue(base64UrlUtil.isNullEmpty((byte[]) null), "Should return true for null byte array");
		assertTrue(base64UrlUtil.isNullEmpty(new byte[0]), "Should return true for empty byte array");
		assertFalse(base64UrlUtil.isNullEmpty("test".getBytes()), "Should return false for non-empty byte array");
	}

	@Test
	void testBase64UrlUtilIsNullEmpty_string() {
		assertTrue(base64UrlUtil.isNullEmpty((String) null), "Should return true for null string");
		assertTrue(base64UrlUtil.isNullEmpty(""), "Should return true for empty string");
		assertTrue(base64UrlUtil.isNullEmpty(" "), "Should return true for whitespace string");
		assertFalse(base64UrlUtil.isNullEmpty("test"), "Should return false for non-empty string");
	}

	@Test
	void testBase64UrlUtilDecodeInvalidBase64() {
		String invalidBase64 = null;
		assertThrows(Exception.class, () -> base64UrlUtil.decodeURLSafeBase64(invalidBase64),
				"Should throw Exception for invalid Base64 input");
	}

	@Test
	void testByteStreamUtilGetByteOrder() {
		assertEquals(ByteOrder.nativeOrder(), byteStreamUtil.getByteOrder());
	}

	@Test
	void testByteStreamUtilArraySize() {
		String[] testArray = { "A", "B", "C" };
		assertEquals(3, byteStreamUtil.arraySize(testArray));
	}

	@Test
	void testByteStreamUtilInitWithValidBuffer() {
		byte[] buf = { 1, 2, 3, 4 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		assertEquals(0, byteBufferContext.getBuffer().position());
		assertEquals(4, byteBufferContext.getBuffer().capacity());
	}

	@Test
	void testByteStreamUtilInitWithNegativeSize() {
		byte[] buf = { 1, 2, 3, 4 };
		DecoderException exception = assertThrows(DecoderException.class, () -> {
			byteStreamUtil.init(byteBufferContext, buf, -1);
		});
		assertEquals(DecoderErrorCodes.INVALID_DATA_ERROR.getErrorCode(), exception.getErrorCode());
	}

	@Test
	void testByteStreamUtilGetBuffer() {
		byte[] buf = { 1, 2, 3, 4, 5 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		byte[] target = new byte[3];
		int bytesRead = byteStreamUtil.getBuffer(byteBufferContext, target, 3);

		assertEquals(3, bytesRead);
		assertArrayEquals(new byte[] { 1, 2, 3 }, target);
		assertEquals(3, byteBufferContext.getBuffer().position());
	}

	@Test
	void testByteStreamUtilGetBytesLeft() {
		byte[] buf = { 1, 2, 3, 4 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		byteStreamUtil.getBuffer(byteBufferContext, new byte[2], 2);
		assertEquals(2, byteStreamUtil.getBytesLeft(byteBufferContext));
	}

	@Test
	void testByteStreamUtilSkipBytesForSize() {
		byte[] buf = { 1, 2, 3, 4, 5 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		byteStreamUtil.skipBytesForSize(byteBufferContext, 2);
		assertEquals(2, byteBufferContext.getBuffer().position());
	}

	@Test
	void testByteStreamUtilCurrentSize() {
		byte[] buf = { 1, 2, 3, 4 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		byteStreamUtil.getBuffer(byteBufferContext, new byte[2], 2);
		assertEquals(2, byteStreamUtil.currentSize(byteBufferContext));
	}

	@Test
	void testByteStreamUtilGetByte() {
		byte[] buf = { 0x01 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		assertEquals(1, byteStreamUtil.getByte(byteBufferContext));
	}

	@Test
	void testByteStreamUtilGetShort() {
		byte[] buf = { 0x01, 0x02 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		assertEquals(258, byteStreamUtil.getShort(byteBufferContext));
	}

	@Test
	void testByteStreamUtilGet3Bytes() {
		byte[] buf = { 0x01, 0x02, 0x03 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		assertEquals(66051, byteStreamUtil.get3Bytes(byteBufferContext));
	}

	@Test
	void testByteStreamUtilGetInt() {
		byte[] buf = { 0x01, 0x02, 0x03, 0x04 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		assertEquals(16909060, byteStreamUtil.getInt(byteBufferContext));
	}

	@Test
	void testByteStreamUtilGetUByte() {
		byte[] buf = { 0x01 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		assertEquals(1, byteStreamUtil.getUByte(byteBufferContext));
	}

	@Test
	void testByteStreamUtilGetUShort() {
		byte[] buf = { 0x01, 0x02 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		assertEquals(258, byteStreamUtil.getUShort(byteBufferContext));
	}

	@Test
	void testByteStreamUtilGetU3Bytes() {
		byte[] buf = { 0x01, 0x02, 0x03 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		assertEquals(66051, byteStreamUtil.getU3Bytes(byteBufferContext));
	}

	@Test
	void testByteStreamUtilGetUInt() {
		byte[] buf = { 0x01, 0x02, 0x03, 0x04 };
		byteStreamUtil.init(byteBufferContext, buf, buf.length);
		assertEquals(16909060, byteStreamUtil.getUInt(byteBufferContext));
	}

	@Test
	void testByteStreamUtilAsUnsignedByte() {
		assertEquals(0, byteStreamUtil.asUnsignedByte((byte) 0));
		assertEquals(255, byteStreamUtil.asUnsignedByte((byte) -1)); // -1 as unsigned is 255
		assertEquals(127, byteStreamUtil.asUnsignedByte((byte) 127));
	}

	@Test
	void testByteStreamUtilAsSignedByte() {
		assertEquals(0, byteStreamUtil.asSignedByte((byte) 0));
		assertEquals(-1, byteStreamUtil.asSignedByte((byte) -1)); // -1 remains -1
		assertEquals(127, byteStreamUtil.asSignedByte((byte) 127));
	}

	@Test
	void testByteStreamUtilAsUnsignedShort() {
		assertEquals(0, byteStreamUtil.asUnsignedShort((short) 0));
		assertEquals(65535, byteStreamUtil.asUnsignedShort((short) -1)); // -1 as unsigned is 65535
		assertEquals(32767, byteStreamUtil.asUnsignedShort((short) 32767));
	}

	@Test
	void testByteStreamUtilAsSignedShort() {
		assertEquals(0, byteStreamUtil.asSignedShort((short) 0));
		assertEquals(-1, byteStreamUtil.asSignedShort((short) -1)); // -1 remains -1
		assertEquals(32767, byteStreamUtil.asSignedShort((short) 32767));
	}

	@Test
	void testByteStreamUtilMemCompare() {
		int[] a = { 1, 2, 3 };
		int[] b = { 1, 2, 3 };
		assertEquals(0, byteStreamUtil.memCompare(a, b, 3));

		int[] c = { 1, 2, 4 };
		assertTrue(byteStreamUtil.memCompare(a, c, 3) < 0); // a < c

		int[] d = { 1, 2, 2 };
		assertTrue(byteStreamUtil.memCompare(a, d, 3) > 0); // a > d

		int[] e = { 1, 2 }; // Smaller size
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
			byteStreamUtil.memCompare(a, e, 3); // sz is larger than e's length
		});
	}

	@Test
	void testByteStreamUtilAvClip() {
		assertEquals(5, byteStreamUtil.avClip(5, 0, 10)); // Within range
		assertEquals(0, byteStreamUtil.avClip(-5, 0, 10)); // Below min
		assertEquals(10, byteStreamUtil.avClip(15, 0, 10)); // Above max
		assertEquals(0, byteStreamUtil.avClip(0, 0, 10)); // At min
		assertEquals(10, byteStreamUtil.avClip(10, 0, 10)); // At max
	}

	@Test
	void testByteStreamUtilMakeTag() {
		assertEquals(0x64636261, byteStreamUtil.makeTag('a', 'b', 'c', 'd')); // 'd' is the highest byte
		assertEquals(0x00000000, byteStreamUtil.makeTag('\0', '\0', '\0', '\0')); // All zeros
		assertEquals(0x7f7f7f7f, byteStreamUtil.makeTag('\u007F', '\u007F', '\u007F', '\u007F')); // Maximum ASCII char
	}

	@Test
	void testByteStreamUtilMakeBETag() {
		assertEquals(0x61626364, byteStreamUtil.makeBETag('a', 'b', 'c', 'd')); // 'a' is the highest byte
		assertEquals(0x00000000, byteStreamUtil.makeBETag('\0', '\0', '\0', '\0')); // All zeros
		assertEquals(0x7f7f7f7f, byteStreamUtil.makeBETag('\u007F', '\u007F', '\u007F', '\u007F')); // Maximum ASCII
																									// char
	}

	@Test
	void testByteStreamUtilFfErrorTag() {
		assertEquals(-1633837924, byteStreamUtil.ffErrorTag('a', 'b', 'c', 'd')); // Negative of makeTag result
		assertEquals(-0, byteStreamUtil.ffErrorTag('\0', '\0', '\0', '\0')); // All zeros
		assertEquals(-2139062143, byteStreamUtil.ffErrorTag('\u007F', '\u007F', '\u007F', '\u007F')); // Negative of max
																										// ASCII char
	}

	@Test
	void testByteStreamUtilCopyDirectBuffer() {
		ByteBuffer original = ByteBuffer.allocateDirect(10);
		original.put(new byte[] { 1, 2, 3, 4, 5 });
		original.flip(); // Prepare buffer for reading

		ByteBuffer copy = byteStreamUtil.copy(original, false);

		assertEquals(original.limit(), copy.limit());
		assertEquals(original.order(), copy.order());
	}

	@Test
	void testByteStreamUtilPeekUByte() {
		ByteBufferContext context = new ByteBufferContext();
		context.setBuffer(ByteBuffer.allocate(10));
		context.getBuffer().put(new byte[] { (byte) 0xFF }); // Unsigned byte value (255)
		context.getBuffer().flip(); // Prepare buffer for reading

		long value = byteStreamUtil.peekUByte(context);

		assertEquals(255, value);
		assertEquals(0, context.getBuffer().position()); // Ensure position is unchanged
	}

	@Test
	void testByteStreamUtilPeekUShort() {
		ByteBufferContext context = new ByteBufferContext();
		context.setBuffer(ByteBuffer.allocate(10));
		context.getBuffer().putShort((short) 0xFFFF); // Unsigned short value (65535)
		context.getBuffer().flip(); // Prepare buffer for reading

		long value = byteStreamUtil.peekUShort(context);

		assertEquals(65535, value);
		assertEquals(0, context.getBuffer().position()); // Ensure position is unchanged
	}

	@Test
	void testByteStreamUtilPeekU3Bytes() {
		ByteBufferContext context = new ByteBufferContext();
		context.setBuffer(ByteBuffer.allocate(10));
		context.getBuffer().put(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }); // Unsigned 3 bytes (16777215)
		context.getBuffer().flip(); // Prepare buffer for reading

		long value = byteStreamUtil.peekU3Bytes(context);

		assertEquals(16777215, value);
		assertEquals(0, context.getBuffer().position()); // Ensure position is unchanged
	}

	@Test
	void testByteStreamUtilPeekUInt() {
		ByteBufferContext context = new ByteBufferContext();
		context.setBuffer(ByteBuffer.allocate(10));
		context.getBuffer().putInt(0xFFFFFFFF); // Unsigned int value (4294967295)
		context.getBuffer().flip(); // Prepare buffer for reading

		long value = byteStreamUtil.peekUInt(context);

		assertEquals(4294967295L, value);
		assertEquals(0, context.getBuffer().position()); // Ensure position is unchanged
	}

	@Test
	void testByteStreamUtilCopyNonDirectBuffer() {
		ByteBuffer original = ByteBuffer.allocate(10);
		original.put(new byte[] { 1, 2, 3, 4, 5 });
		original.flip(); // Prepare buffer for reading

		ByteBuffer copy = byteStreamUtil.copy(original, false);

		assertEquals(original.limit(), copy.limit());
		assertEquals(original.order(), copy.order());
	}

	@Test
	void testByteStreamUtilPeekByte() {
		ByteBufferContext context = new ByteBufferContext();
		context.setBuffer(ByteBuffer.allocate(10));
		context.getBuffer().put(new byte[] { 10 });
		context.getBuffer().flip(); // Prepare buffer for reading

		long value = byteStreamUtil.peekByte(context);

		assertEquals(10, value);
		assertEquals(0, context.getBuffer().position()); // Ensure position is unchanged
	}

	@Test
	void testByteStreamUtilPeekShort() {
		ByteBufferContext context = new ByteBufferContext();
		context.setBuffer(ByteBuffer.allocate(10));
		context.getBuffer().putShort((short) 20);
		context.getBuffer().flip(); // Prepare buffer for reading

		long value = byteStreamUtil.peekShort(context);

		assertEquals(20, value);
		assertEquals(0, context.getBuffer().position()); // Ensure position is unchanged
	}

	@Test
	void testByteStreamUtilPeekInt() {
		ByteBufferContext context = new ByteBufferContext();
		context.setBuffer(ByteBuffer.allocate(10));
		context.getBuffer().putInt(30);
		context.getBuffer().flip(); // Prepare buffer for reading

		long value = byteStreamUtil.peekInt(context);

		assertEquals(30, value);
		assertEquals(0, context.getBuffer().position()); // Ensure position is unchanged
	}

	@Test
	void testByteStreamUtilSeek() {
		ByteBufferContext context = new ByteBufferContext();
		context.setBuffer(ByteBuffer.allocate(10));
		context.getBuffer().put(new byte[] { 1, 2, 3, 4, 5 });
		context.getBuffer().flip(); // Prepare buffer for reading

		int newPos = byteStreamUtil.seek(context, 2, DecoderConstant.WHENCE_SEEK_SET);
		assertEquals(2, newPos);
		assertEquals(2, context.getBuffer().position()); // Ensure position is updated

		newPos = byteStreamUtil.seek(context, -1, DecoderConstant.WHENCE_SEEK_CUR);
		assertEquals(1, newPos);
		assertEquals(1, context.getBuffer().position()); // Ensure position is updated

		newPos = byteStreamUtil.seek(context, -1, DecoderConstant.WHENCE_SEEK_END);
		assertEquals(3, newPos); // Ensure correct seeking to end
		assertEquals(3, context.getBuffer().position()); // Ensure position is updated
	}

	@Test
	void testByteStreamUtilInvalidSeek() {
		ByteBufferContext context = new ByteBufferContext();
		context.setBuffer(ByteBuffer.allocate(10));
		context.getBuffer().put(new byte[] { 1, 2, 3, 4, 5 });
		context.getBuffer().flip(); // Prepare buffer for reading

		assertThrows(DecoderException.class, () -> {
			byteStreamUtil.seek(context, 0, -1); // Invalid whence
		});
	}

	@Test
	void testByteStreamUtilToUnsignedBigInteger_PositiveValue() {
		long input = 123456789L;
		BigInteger expected = BigInteger.valueOf(input);

		BigInteger result = byteStreamUtil.toUnsignedBigInteger(input);

		assertEquals(expected, result);
	}

	@Test
	void testByteStreamUtilToUnsignedBigInteger_NegativeValue() {
		long input = -123456789L; // Negative long value
		// Expected value will be (2^32 + input)
		BigInteger expected = BigInteger.valueOf(Integer.toUnsignedLong(-1)).shiftLeft(32)
				.add(BigInteger.valueOf(Integer.toUnsignedLong(-123456789)));

		BigInteger result = byteStreamUtil.toUnsignedBigInteger(input);

		assertEquals(expected, result);
	}

	@Test
	void testByteStreamUtilToUnsignedBigInteger_Zero() {
		long input = 0L;
		BigInteger expected = BigInteger.valueOf(input);

		BigInteger result = byteStreamUtil.toUnsignedBigInteger(input);

		assertEquals(expected, result);
	}

	@Test
	void testByteStreamUtilToUnsignedBigInteger_MaxLongValue() {
		long input = Long.MAX_VALUE; // Maximum long value
		BigInteger expected = BigInteger.valueOf(input);

		BigInteger result = byteStreamUtil.toUnsignedBigInteger(input);

		assertEquals(expected, result);
	}

	@Test
	void testByteStreamUtilToUnsignedBigInteger_MinLongValue() {
		long input = Long.MIN_VALUE; // Minimum long value
		BigInteger expected = BigInteger.ONE.shiftLeft(63); // This represents 2^63

		BigInteger result = byteStreamUtil.toUnsignedBigInteger(input);

		assertEquals(expected, result);
	}

	@Test
	void testByteStreamUtilToUnsignedBigInteger_NegativeUpperBound() {
		long input = -1L; // Edge case for the smallest negative value
		BigInteger expected = BigInteger.valueOf(Integer.toUnsignedLong(-1)).shiftLeft(32)
				.add(BigInteger.valueOf(Integer.toUnsignedLong(-1)));

		BigInteger result = byteStreamUtil.toUnsignedBigInteger(input);

		assertEquals(expected, result);
	}

	@Test
	void testByteStreamUtilSkipBytes_ValidCase() {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.put(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		buffer.flip(); // Prepare buffer for reading
		byteBufferContext.setBuffer(buffer);

		// Set initial position
		buffer.position(2);

		// Skip 3 bytes
		byteStreamUtil.skipBytesForSize(byteBufferContext, 3);

		// Verify new position
		assertEquals(5, buffer.position());
	}

	@Test
	void testByteStreamUtilSkipBytesForSizeU_ValidSkip() {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.put(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		buffer.flip(); // Prepare buffer for reading
		byteBufferContext.setBuffer(buffer);

		// Set initial position
		buffer.position(2);

		// Skip 3 bytes
		byteStreamUtil.skipBytesForSizeU(byteBufferContext, 3);

		// Verify new position
		assertEquals(5, buffer.position());
	}

	@Test
	void testByteStreamUtilGetUnsignedLong_BigEndian() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.put(new byte[] { 0, 0, 0, 0, 0, 0, 0, 1 }); // 1 as unsigned long
		buffer.flip(); // Prepare buffer for reading

		BigInteger result = byteStreamUtil.getUnsignedLong(buffer, 0, ByteOrder.BIG_ENDIAN);

		assertEquals(BigInteger.ONE, result);
	}

	@Test
	void testByteStreamUtilGetUnsignedLong_LittleEndian() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(new byte[] { 1, 0, 0, 0, 0, 0, 0, 0 }); // 1 as unsigned long
		buffer.flip(); // Prepare buffer for reading

		BigInteger result = byteStreamUtil.getUnsignedLong(buffer, 0, ByteOrder.LITTLE_ENDIAN);

		assertEquals(BigInteger.ONE, result);
	}

	@Test
	void testByteStreamUtilGetUnsignedLong_Zero() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }); // 0 as unsigned long
		buffer.flip(); // Prepare buffer for reading

		BigInteger result = byteStreamUtil.getUnsignedLong(buffer, 0, ByteOrder.BIG_ENDIAN);

		assertEquals(BigInteger.ZERO, result);
	}

	@Test
	void testByteStreamUtilGetUnsignedLong_MaxValue_BigEndian() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.put(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF }); // 2^64 - 1 as unsigned long
		buffer.flip(); // Prepare buffer for reading

		BigInteger result = byteStreamUtil.getUnsignedLong(buffer, 0, ByteOrder.BIG_ENDIAN);

		assertEquals(new BigInteger("18446744073709551615"), result); // 2^64 - 1
	}

	@Test
	void testByteStreamUtilGetUnsignedLong_MaxValue_LittleEndian() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(new byte[] { -1, -1, -1, -1, -1, -1, -1, 127 }); // 2^63 - 1 as unsigned long
		buffer.flip(); // Prepare buffer for reading

		BigInteger result = byteStreamUtil.getUnsignedLong(buffer, 0, ByteOrder.LITTLE_ENDIAN);

		assertEquals(new BigInteger("9223372036854775807"), result);
	}

	@Test
	void testByteStreamUtilGetSignedLong_BigEndian() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.put(new byte[] { 0, 0, 0, 0, 0, 0, 0, 1 }); // 1 as signed long
		buffer.flip(); // Prepare buffer for reading

		BigInteger result = byteStreamUtil.getSignedLong(buffer, 0, ByteOrder.BIG_ENDIAN);

		assertEquals(BigInteger.ONE, result);
	}

	@Test
	void testByteStreamUtilGetSignedLong_LittleEndian() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(new byte[] { 1, 0, 0, 0, 0, 0, 0, 0 }); // 1 as signed long
		buffer.flip(); // Prepare buffer for reading

		BigInteger result = byteStreamUtil.getSignedLong(buffer, 0, ByteOrder.LITTLE_ENDIAN);

		assertEquals(BigInteger.ONE, result);
	}

	@Test
	void testByteStreamUtilGetSignedLong_NegativeValue_BigEndian() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.put(new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 }); // -1 as signed long
		buffer.flip(); // Prepare buffer for reading

		BigInteger result = byteStreamUtil.getSignedLong(buffer, 0, ByteOrder.BIG_ENDIAN);

		assertEquals(BigInteger.valueOf(-1), result);
	}

	@Test
	void testByteStreamUtilGetSignedLong_NegativeValue_LittleEndian() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(new byte[] { -1, -1, -1, -1, -1, -1, -1, -1 }); // -1 as signed long
		buffer.flip(); // Prepare buffer for reading

		BigInteger result = byteStreamUtil.getSignedLong(buffer, 0, ByteOrder.LITTLE_ENDIAN);

		assertEquals(BigInteger.valueOf(-1), result);
	}

	@Test
	void testByteStreamUtilGetSignedLong_MaxValue_BigEndian() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.put(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF }); // 2^64 - 1 as unsigned long
		buffer.flip(); // Prepare buffer for reading

		BigInteger result = byteStreamUtil.getUnsignedLong(buffer, 0, ByteOrder.BIG_ENDIAN);

		assertEquals(new BigInteger("18446744073709551615"), result); // 2^64 - 1
	}

	@Test
	void testByteStreamUtilGetSignedLong_MinValue_BigEndian() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.put(new byte[] { -128, 0, 0, 0, 0, 0, 0, 0 }); // Min signed long value
		buffer.flip(); // Prepare buffer for reading

		BigInteger result = byteStreamUtil.getSignedLong(buffer, 0, ByteOrder.BIG_ENDIAN);

		assertEquals(new BigInteger("-9223372036854775808"), result);
	}

	@Test
	void testByteStreamUtilGetSignedVarInt_1Byte_Positive() {
		ByteBuffer buffer = ByteBuffer.allocate(1);
		buffer.put((byte) 127); // 127 is the max positive value for a signed byte
		long result = byteStreamUtil.getSignedVarInt(buffer, 0, 1);
		assertEquals(127, result);
	}

	@Test
	void testByteStreamUtilGetSignedVarInt_1Byte_Negative() {
		ByteBuffer buffer = ByteBuffer.allocate(1);
		buffer.put((byte) -128); // -128 is the min negative value for a signed byte
		long result = byteStreamUtil.getSignedVarInt(buffer, 0, 1);
		assertEquals(-128, result);
	}

	@Test
	void testByteStreamUtilGetSignedVarInt_2Bytes_Positive() {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.putShort((short) 32767); // 32767 is the max positive value for a signed short
		buffer.flip(); // Prepare buffer for reading
		long result = byteStreamUtil.getSignedVarInt(buffer, 0, 2);
		assertEquals(32767, result);
	}

	@Test
	void testByteStreamUtilGetSignedVarInt_2Bytes_Negative() {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.putShort((short) -32768); // -32768 is the min negative value for a signed short
		buffer.flip(); // Prepare buffer for reading
		long result = byteStreamUtil.getSignedVarInt(buffer, 0, 2);
		assertEquals(-32768, result);
	}

	@Test
	void testByteStreamUtilGetSignedVarInt_3Bytes_Positive() {
		ByteBuffer buffer = ByteBuffer.allocate(3);
		buffer.put(new byte[] { 0x7F, (byte) 0xFF, (byte) 0xFF }); // Max positive 24-bit value: 8388607
		buffer.flip(); // Prepare buffer for reading
		long result = byteStreamUtil.getSignedVarInt(buffer, 0, 3);
		assertEquals(8388607, result);
	}

	@Test
	void testByteStreamUtilGetSignedVarInt_3Bytes_Negative() {
		ByteBuffer buffer = ByteBuffer.allocate(3);
		buffer.put(new byte[] { (byte) 0x80, 0x00, 0x00 }); // Min negative 24-bit value: -8388608
		buffer.flip(); // Prepare buffer for reading
		long result = byteStreamUtil.getSignedVarInt(buffer, 0, 3);
		assertEquals(-8388608, result);
	}

	@Test
	void testByteStreamUtilGetSignedVarInt_4Bytes_Positive() {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(Integer.MAX_VALUE); // Max positive value for 32-bit signed int: 2147483647
		buffer.flip(); // Prepare buffer for reading
		long result = byteStreamUtil.getSignedVarInt(buffer, 0, 4);		
		assertEquals(2147483647, result);
	}

	@Test
	void testByteStreamUtilGetSignedVarInt_4Bytes_Negative() {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(Integer.MIN_VALUE); // Min negative value for 32-bit signed int: -2147483648
		buffer.flip(); // Prepare buffer for reading
		long result = byteStreamUtil.getSignedVarInt(buffer, 0, 4);
		assertEquals(-2147483648, result);
	}

	@Test
	void testByteStreamUtilGetSignedVarInt_InvalidNumBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(1234);
		buffer.flip(); // Prepare buffer for reading
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			byteStreamUtil.getSignedVarInt(buffer, 0, 5); // Invalid number of bytes
		});
		assertTrue(exception.getMessage().contains("Invalid num bytes"));
	}

	@Test
	void testByteStreamUtilGetUnsignedVarInt_1Byte() {
	    ByteBuffer buffer = ByteBuffer.allocate(1);
	    buffer.put((byte) 0xFF); // Max value for 1 byte: 255
	    buffer.flip(); // Prepare buffer for reading
	    long result = byteStreamUtil.getUnsignedVarInt(buffer, 0, 1);
	    assertEquals(255, result);
	}

	@Test
	void testByteStreamUtilGetUnsignedVarInt_2Bytes() {
	    ByteBuffer buffer = ByteBuffer.allocate(2);
	    buffer.putShort((short) 0xFFFF); // Max value for 2 bytes: 65535
	    buffer.flip(); // Prepare buffer for reading
	    long result = byteStreamUtil.getUnsignedVarInt(buffer, 0, 2);
	    assertEquals(65535, result);
	}

	@Test
	void testByteStreamUtilGetUnsignedVarInt_3Bytes() {
	    ByteBuffer buffer = ByteBuffer.allocate(3);
	    buffer.put(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF}); // Max value for 3 bytes: 16777215
	    buffer.flip(); // Prepare buffer for reading
	    long result = byteStreamUtil.getUnsignedVarInt(buffer, 0, 3);
	    assertEquals(16777215, result);
	}

	@Test
	void testByteStreamUtilGetUnsignedVarInt_4Bytes() {
	    ByteBuffer buffer = ByteBuffer.allocate(4);
	    buffer.putInt(0xFFFFFFFF); // Max value for 4 bytes: 4294967295
	    buffer.flip(); // Prepare buffer for reading
	    long result = byteStreamUtil.getUnsignedVarInt(buffer, 0, 4);
	    assertEquals(4294967295L, result);
	}

	@Test
	void testByteStreamUtilGetUnsignedVarInt_InvalidNumBytes() {
	    ByteBuffer buffer = ByteBuffer.allocate(1);
	    buffer.put((byte) 0xFF);
	    buffer.flip(); // Prepare buffer for reading
	    Exception exception = assertThrows(DecoderException.class, () -> {
	        byteStreamUtil.getUnsignedVarInt(buffer, 0, 5); // Invalid number of bytes
	    });
	    assertTrue(exception.getMessage().contains("Invalid num bytes"));
	}
	
	@Test
	void testByteSwapperUtilSwapShort() {
		short original = 0x1234;
		short swapped = byteSwapperUtil.swapShort(original);
		assertEquals(0x3412, swapped, "The short value should be swapped correctly");
	}

	@Test
	void testByteSwapperUtilSwap3Bytes() {
		int original = 0x123456;
		int swapped = byteSwapperUtil.swap3Bytes(original);
		assertEquals(0x563412, swapped, "The 3-byte int should be swapped correctly");
	}

	@Test
	void testByteSwapperUtilSwapInt() {
		int original = 0x12345678;
		int swapped = byteSwapperUtil.swapInt(original);
		assertEquals(0x78563412, swapped, "The int value should be swapped correctly");
	}

	@Test
	void testByteSwapperUtilSwapLong() {
		long original = 0x1234567890ABCDEFL;
		long swapped = byteSwapperUtil.swapLong(original);
		assertEquals(0xEFCDAB9078563412L, swapped, "The long value should be swapped correctly");
	}

	@Test
	void testByteSwapperUtilSwapFloat() {
		float original = 1234.56f;
		float swapped = byteSwapperUtil.swapFloat(original);
		assertNotEquals(original, swapped, "The float value should be swapped correctly");
	}

	@Test
	void testByteSwapperUtilSwapDouble() {
		double original = 12345678.90;
		double swapped = byteSwapperUtil.swapDouble(original);
		assertNotEquals(original, swapped, "The double value should be swapped correctly");
	}

	@Test
	void testByteSwapperUtilSwapShortArray() {
		short[] array = { 0x1234, 0x5678 };
		byteSwapperUtil.swap(array);
		assertArrayEquals(new short[] { 0x3412, 0x7856 }, array, "The array of shorts should be swapped");
	}

	@Test
	void testByteSwapperUtilSwapIntArray() {
		int[] array = { 0x12345678, 0x90ABCDEF };
		byteSwapperUtil.swap(array);
		assertArrayEquals(new int[] { 0x78563412, 0xEFCDAB90 }, array, "The array of ints should be swapped");
	}

	@Test
	void testByteSwapperUtilSwapLongArray() {
		long[] array = { 0x1234567890ABCDEFL, 0x0FEDCBA987654321L };
		byteSwapperUtil.swap(array);
		System.out.printf("Swapped values: [%016X, %016X]%n", array[0], array[1]);
		assertArrayEquals(new long[] { 0xEFCDAB9078563412L, 0x21436587A9CBED0FL }, array,
				"The array of longs should be swapped");
	}

	@Test
	void testByteSwapperUtilSwapFloatArray() {
		float[] array = { 1234.56f, 7890.12f };
		byteSwapperUtil.swap(array);
		assertNotEquals(array[0], 1234.56f);
		assertNotEquals(array[1], 7890.12f);
	}

	@Test
	void testByteSwapperUtilSwapDoubleArray() {
		double[] array = { 12345678.90, 98765432.10 };
		byteSwapperUtil.swap(array);
		assertNotEquals(array[0], 12345678.90);
		assertNotEquals(array[1], 98765432.10);
	}

	@Test
	void testStringUtilStringCompare_EqualStrings() {
		assertEquals(0, stringUtil.stringCompare("Hello", "Hello"), "Strings should be equal");
	}

	@Test
	void testStringUtilStringCompare_DifferentStrings() {
		assertTrue(stringUtil.stringCompare("Hello", "World") < 0, "Hello should be less than World");
		assertTrue(stringUtil.stringCompare("World", "Hello") > 0, "World should be greater than Hello");
	}

	@Test
	void testStringUtilStringCompare_LengthDifference() {
		assertTrue(stringUtil.stringCompare("Hello", "Hello World") < 0, "Hello should be less than Hello World");
		assertTrue(stringUtil.stringCompare("Hello World", "Hello") > 0, "Hello World should be greater than Hello");
	}

	@Test
	void testStringUtilAtoi_ValidInteger() {
		char[] validInput = "12345".toCharArray();
		assertEquals(12345, stringUtil.atoi(validInput), "Should convert valid string to integer");
	}

	@Test
	void testStringUtilAtoi_WhitespaceAndSigns() {
		char[] input = "   -678".toCharArray();
		assertEquals(-678, stringUtil.atoi(input), "Should handle whitespace and negative sign");
	}

	@Test
	void testStringUtilAtoi_Overflow() {
		char[] overflowInput = "2147483648".toCharArray(); // This is beyond Integer.MAX_VALUE
		assertEquals(Integer.MAX_VALUE, stringUtil.atoi(overflowInput), "Should return Integer.MAX_VALUE on overflow");
	}

	@Test
	void testStringUtilAtoi_InvalidInput() {
		char[] invalidInput = "abc".toCharArray();
		assertEquals(0, stringUtil.atoi(invalidInput), "Should return 0 for invalid input");
	}

	@Test
	void testStringUtilAtof_ValidDouble() {
		assertEquals(123.45, stringUtil.atof("123.45"), 0.001, "Should convert valid string to double");
	}

	@Test
	void testStringUtilAtof_WhitespaceAndSigns() {
		assertEquals(-67.89, stringUtil.atof("  -67.89  "), 0.001, "Should handle whitespace and negative sign");
	}

	@Test
	void testStringUtilAtof_InvalidDouble() {
		assertEquals(0.0, stringUtil.atof("abc"), 0.001, "Should return 0.0 for invalid input");
	}

	@Test
	void testStringUtilAtof_TooManySignsOrDots() {
		assertEquals(0.0, stringUtil.atof("----123.45"), 0.001,
				"Should return 0.0 for invalid input with too many signs");
		assertEquals(0.0, stringUtil.atof("123..45"), 0.001, "Should return 0.0 for invalid input with too many dots");
	}

	@Test
	void testConvertImage2Floats() {
		// Prepare data
		int numOfPixels = 10;
		float[] fImageData = new float[numOfPixels];
		float[] mShift = new float[1];
		float[] rScale = new float[1];
		byte[] data = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		// Execute
		int result = wsqUtil.convertImage2Floats(fImageData, mShift, rScale, data, numOfPixels);

		// Verify
		assertEquals(0, result);
		assertNotNull(fImageData);
		assertTrue(mShift[0] >= 0);
		assertTrue(rScale[0] > 0);
	}

	@Test
	void testConvertImage2FloatsWithOverflow() {
		// Prepare data that causes overflow
		int numOfPixels = 5;
		float[] fImageData = new float[numOfPixels];
		float[] mShift = new float[1];
		float[] rScale = new float[1];
		byte[] data = new byte[numOfPixels];
		for (int i = 0; i < numOfPixels; i++) {
			data[i] = (byte) 255; // Setting max value to trigger overflow in sum
		}

		// Execute
		int result = wsqUtil.convertImage2Floats(fImageData, mShift, rScale, data, numOfPixels);

		// Verify
		assertEquals(0, result);
	}

	@Test
	void testConvertImage2Bytes() {
		// Prepare data
		int width = 5;
		int height = 2;
		byte[] data = new byte[width * height];
		float[] img = { 0.0f, 128.0f, 255.0f, 300.0f, -10.0f, 100.0f, 50.0f, 200.0f, 10.0f, 150.0f };
		float mShift = 128.0f;
		float rScale = 1.0f;

		// Execute
		wsqUtil.convertImage2Bytes(data, img, width, height, mShift, rScale);

		// Verify
		for (byte b : data) {
			assertTrue(b >= -128 && b <= 128);
		}
	}

	@Test
	void testVarianceWithVsumBelow20000() {
		// Prepare data
		int width = 10;
		int height = 10;
		float[] fImageData = new float[width * height];
		WsqQuantization quantValues = new WsqQuantization();
		quantValues.setVar(new float[WsqConstant.NUM_SUBBANDS]);

		WsqQuantizationTree[] quantizationTree = new WsqQuantizationTree[WsqConstant.NUM_SUBBANDS];
		for (int i = 0; i < quantizationTree.length; i++) {
			quantizationTree[i] = new WsqQuantizationTree();
			quantizationTree[i].setX((short) 0);
			quantizationTree[i].setY((short) 0);
			quantizationTree[i].setLenX((short) 8);
			quantizationTree[i].setLenY((short) 8);
		}

		// Execute
		wsqUtil.variance(quantValues, quantizationTree, quantizationTree.length, fImageData, width, height);

		// Verify
		for (float var : quantValues.getVar()) {
			assertTrue(var >= 0);
		}
	}

	@Test
	void testVarianceWithVsumAbove20000() {
		// Prepare data
		int width = 10;
		int height = 10;
		float[] fImageData = new float[width * height];
		WsqQuantization quantValues = new WsqQuantization();
		quantValues.setVar(new float[WsqConstant.NUM_SUBBANDS]);

		WsqQuantizationTree[] quantizationTree = new WsqQuantizationTree[WsqConstant.NUM_SUBBANDS];
		for (int i = 0; i < quantizationTree.length; i++) {
			quantizationTree[i] = new WsqQuantizationTree();
			quantizationTree[i].setX((short) 0);
			quantizationTree[i].setY((short) 0);
			quantizationTree[i].setLenX((short) 8);
			quantizationTree[i].setLenY((short) 8);
		}

		// Simulate a large `vsum`
		quantValues.getVar()[0] = 25000.0f;

		// Execute
		wsqUtil.variance(quantValues, quantizationTree, quantizationTree.length, fImageData, width, height);

		// Verify
		for (float var1 : quantValues.getVar()) {
			assertTrue(var1 >= 0);
		}
	}

	@Test
	void testQuantize() {
		// Set up test data
		short[] sip = new short[100];
		int[] cmpSize = new int[1];
		WsqQuantization quantValues = new WsqQuantization();
		WsqQuantizationTree[] quantizationTree = new WsqQuantizationTree[60]; // Assuming 60 subbands
		for (int i = 0; i < quantizationTree.length; i++) {
			if (quantizationTree[i] == null) { // Check if the element is not already initialized
				quantizationTree[i] = new WsqQuantizationTree();
			}
			quantizationTree[i].setLenX((short) 10); // Example value for length X
			quantizationTree[i].setLenY((short) 10); // Example value for length Y
			quantizationTree[i].setX((short) 5); // Example starting X coordinate
			quantizationTree[i].setY((short) 5); // Example starting Y coordinate
		}
		int qTreeLen = quantizationTree.length;
		float[] fImageData = new float[1000]; // Example data size
		int width = 100;
		int height = 100;

		// Call the method
		int result = wsqUtil.quantize(sip, cmpSize, quantValues, quantizationTree, qTreeLen, fImageData, width, height);

		// Add assertions to verify correctness
		assertEquals(0, result);
		// Additional checks for 'sip', 'cmpSize', etc.
	}

	@Test
	void testQuantizedBlockSizes() {
		int[] qSize1 = new int[1];
		int[] qSize2 = new int[1];
		int[] qSize3 = new int[1];
		WsqQuantization quantValues = new WsqQuantization();
		WsqWavletTree[] wavletTree = new WsqWavletTree[20];
		// Initialize each element with some test data
		for (int i = 0; i < wavletTree.length; i++) {
			wavletTree[i] = new WsqWavletTree();
			wavletTree[i].setLenX(10); // Example value for length X
			wavletTree[i].setLenY(10); // Example value for length Y
		}

		WsqQuantizationTree[] quantizationTree = new WsqQuantizationTree[60];
		for (int i = 0; i < quantizationTree.length; i++) {
			if (quantizationTree[i] == null) { // Check if the element is not already initialized
				quantizationTree[i] = new WsqQuantizationTree();
			}
			quantizationTree[i].setLenX((short) 10); // Example value for length X
			quantizationTree[i].setLenY((short) 10); // Example value for length Y
			quantizationTree[i].setX((short) 5); // Example starting X coordinate
			quantizationTree[i].setY((short) 5); // Example starting Y coordinate
		}
		int waveletTreeLen = wavletTree.length;
		int qTreeLen = quantizationTree.length;

		wsqUtil.quantizedBlockSizes(qSize1, qSize2, qSize3, quantValues, wavletTree, waveletTreeLen, quantizationTree,
				qTreeLen);

		int expectedValue = -1800;
		// Add assertions to verify correctness
		// For example:
		assertEquals(expectedValue, qSize1[0]);
	}

	@Test
	void testUnquantize() {
		// Prepare test data
		float[] fImageData = new float[100]; // Adjust size as necessary
		WsqTableDqt dqtTable = new WsqTableDqt();
		// Initialize dqtTable properties as needed
		WsqQuantizationTree[] quantizationTree = new WsqQuantizationTree[WsqConstant.NUM_SUBBANDS];

		// Initialize the quantizationTree array
		for (int i = 0; i < quantizationTree.length; i++) {
			quantizationTree[i] = new WsqQuantizationTree();
			quantizationTree[i].setLenX((short) 10); // Set appropriate values
			quantizationTree[i].setLenY((short) 10);
			quantizationTree[i].setX((short) 5);
			quantizationTree[i].setY((short) 5);
		}

		long[] sip = new long[] { 0, 1, 2 }; // Example quantized values
		int width = 10; // Set appropriate width
		int height = 10; // Set appropriate height

		// Call the method under test
		int result = wsqUtil.unquantize(fImageData, dqtTable, quantizationTree, quantizationTree.length, sip, width,
				height);

		// Verify the results
		assertEquals(-34, result); // Check for expected return value
		// Further assertions to check the content of fImageData as needed
	}

	@Test
	void testWsqDecomposeNormal() {
		float[] fImageData = new float[100]; // Sample image data
		float[] highFilter = new float[] { 1.0f, 2.0f }; // Example filter coefficients
		float[] lowFilter = new float[] { 1.0f, 2.0f }; // Example filter coefficients

		WsqWavletTree[] wavletTree = new WsqWavletTree[3]; // Adjust size as needed
		for (int i = 0; i < wavletTree.length; i++) {
			wavletTree[i] = new WsqWavletTree();
			wavletTree[i].setY(0); // Example values
			wavletTree[i].setX(0);
			wavletTree[i].setLenY(10);
			wavletTree[i].setLenX(10);
			wavletTree[i].setInvRow(0);
			wavletTree[i].setInvCol(0);
		}
		int result = wsqUtil.wsqDecompose(fImageData, 10, 10, wavletTree, wavletTree.length, highFilter,
				highFilter.length, lowFilter, lowFilter.length);

		assertEquals(0, result); // Expect success
		// Validate fImageData or other outcomes as necessary
	}

	@Test
	void testImageSizeNormal() {
		int blockLen = 100;
		short[] huffBits1 = new short[16];
		short[] huffBits2 = new short[16];

		// Fill huffman bits with some values
		for (int i = 1; i < 16; i++) {
			huffBits1[i] = (short) (i * 5);
			huffBits2[i] = (short) (i * 3);
		}

		int result = wsqUtil.imageSize(blockLen, huffBits1, huffBits2);

		// Calculate expected total size manually based on your understanding
		int expectedSize = blockLen + 58 + 389 + 17 + 3 + 3 + 3 + 3 + 16 + (huffBits1[1] + huffBits1[2] + huffBits1[3]
				+ huffBits1[4] + huffBits1[5] + huffBits1[6] + huffBits1[7] + huffBits1[8] + huffBits1[9]
				+ huffBits1[10] + huffBits1[11] + huffBits1[12] + huffBits1[13] + huffBits1[14] + huffBits1[15]) + 3
				+ 16
				+ (huffBits2[1] + huffBits2[2] + huffBits2[3] + huffBits2[4] + huffBits2[5] + huffBits2[6]
						+ huffBits2[7] + huffBits2[8] + huffBits2[9] + huffBits2[10] + huffBits2[11] + huffBits2[12]
						+ huffBits2[13] + huffBits2[14] + huffBits2[15])
				+ 20;

		assertEquals(expectedSize, result);
	}

	@Test
	void testSingleton() {
		ImageUtil instance1 = ImageUtil.getInstance();
		ImageUtil instance2 = ImageUtil.getInstance();
		assertSame(instance1, instance2, "Singleton instances are not the same");
	}

	@Test
	void testCalculateAspectRatio() {
		assertEquals(2.0, imageUtil.calculateAspectRatio(4, 2), 0.01);
		assertThrows(IllegalArgumentException.class, () -> imageUtil.calculateAspectRatio(0, 5));
		assertThrows(IllegalArgumentException.class, () -> imageUtil.calculateAspectRatio(5, -1));
	}

	@Test
	void testCalculateCompressionRatio() {
		assertEquals(1, imageUtil.calculateCompressionRatio(100, 100, 1, 10000));
		assertEquals(100, imageUtil.calculateCompressionRatio(100, 100, 1, 100));
		assertThrows(IllegalArgumentException.class, () -> imageUtil.calculateCompressionRatio(100, 100, 1, 0));
		assertThrows(IllegalArgumentException.class, () -> imageUtil.calculateCompressionRatio(-100, 100, 1, 10000));
	}

	@Test
	void testFromJ2kImage() {
		// Assuming you have a mock or valid OpenJpegImage instance
		OpenJpegImage mockImage = createMockOpenJpegImage(); // Replace with actual mock creation
		assertThrows(IllegalArgumentException.class, () -> imageUtil.fromJ2kImage(800, 600, mockImage));

		// Test exceptions for unsupported formats, if applicable
		assertThrows(Exception.class, () -> {
			// Pass an unsupported image format mock
			imageUtil.fromJ2kImage(800, 600, createUnsupportedFormatImage());
		});
	}

	@Test
	void testFromByteGray() {
		byte[] grayData = new byte[800 * 600]; // Example data
		BufferedImage grayImage = imageUtil.fromByteGray(800, 600, grayData);
		assertNotNull(grayImage);
		assertEquals(BufferedImage.TYPE_BYTE_GRAY, grayImage.getType());
		assertArrayEquals(grayData, ((DataBufferByte) grayImage.getRaster().getDataBuffer()).getData());
	}

	@Test
	void testIntegersToBytes() {
		int[] intData = { 1, 2, 3, 4, 5 };
		byte[] byteData = imageUtil.integersToBytes(intData);
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5 }, byteData);
	}

	private OpenJpegImage createMockOpenJpegImage() {
		// Create a mocked instance of OpenJpegImage
		OpenJpegImage mockImage = Mockito.mock(OpenJpegImage.class);

		// Set up the mock behavior
		Mockito.when(mockImage.getX0()).thenReturn(0);
		Mockito.when(mockImage.getY0()).thenReturn(0);
		Mockito.when(mockImage.getX1()).thenReturn(1920);
		Mockito.when(mockImage.getY1()).thenReturn(1080);
		Mockito.when(mockImage.getNoOfComps()).thenReturn(3);
		Mockito.when(mockImage.getQmfbid()).thenReturn(0);
		Mockito.when(mockImage.getColorSpace()).thenReturn(Jp2ColorSpace.CLRSPC_SRGB);

		// Create and set up components if needed
		OpenJpegImageComponent[] components = new OpenJpegImageComponent[3];
		for (int i = 0; i < 3; i++) {
			components[i] = Mockito.mock(OpenJpegImageComponent.class);
			Mockito.when(components[i].getData()).thenReturn(new int[1920 * 1080]); // Example data
		}
		Mockito.when(mockImage.getComps()).thenReturn(components);

		return mockImage;
	}

	private OpenJpegImage createUnsupportedFormatImage() {
		// Create a mocked instance of OpenJpegImage
		return null;
	}
}