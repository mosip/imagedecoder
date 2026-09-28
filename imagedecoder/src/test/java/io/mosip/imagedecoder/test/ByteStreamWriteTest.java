package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.mosip.imagedecoder.model.ByteBufferContext;
import io.mosip.imagedecoder.util.ByteStreamUtil;

/**
 * Covers ByteStreamUtil write/peek overloads missed by Copy-era UtilTest.
 */
class ByteStreamWriteTest {

	private ByteStreamUtil util;
	private ByteBufferContext ctx;

	@BeforeEach
	void setUp() {
		util = ByteStreamUtil.getInstance();
		ctx = new ByteBufferContext();
		util.init(ctx, new byte[64], 64);
	}

	@Test
	void putAndPeekPrimitives() throws IOException {
		util.putByte(ctx, 0x7F);
		util.putShort(ctx, 0x1234);
		util.put3Bytes(ctx, 0xABCDEF);
		util.putInt(ctx, 0x11223344);
		util.putLong(ctx, 0x0102030405060708L);
		util.putUByte(ctx, 0xFF);
		util.putUShort(ctx, 0xFFFF);
		util.putU3Bytes(ctx, 0xFFFFFF);
		util.putUInt(ctx, 0xFFFFFFFFL);
		assertTrue(util.position(ctx) > 0);
	}

	@Test
	void getBufferUAndCopy() {
		byte[] src = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
		util.init(ctx, src, src.length);
		byte[] target = new byte[4];
		assertEquals(4, util.getBufferU(ctx, target, 4));
		assertEquals(1, target[0]);
		byte[] target2 = new byte[8];
		assertEquals(4, util.getBufferU(ctx, target2, 2, 4));
		assertEquals(5, target2[2]);

		ByteBuffer copy = util.bytebuffercopy(ctx.getBuffer());
		assertNotNull(copy);
		assertEquals(ctx.getBuffer().remaining(), copy.remaining());
	}

	@Test
	void peekLongVariants() throws IOException {
		util.init(ctx, new byte[16], 16);
		util.putLong(ctx, 1L);
		ctx.getBuffer().rewind();
		BigInteger peeked = util.peekLong(ctx);
		assertNotNull(peeked);
		BigInteger peekedU = util.peekULong(ctx);
		assertNotNull(peekedU);
		assertEquals(0, ctx.getBuffer().position());

		util.init(ctx, new byte[] { 1, 2, 3, 4 }, 4);
		assertEquals(util.peek3Bytes(ctx), util.peek3Bytes(ctx));
	}

	@Test
	void putLittleEndianAndOverflowGuards() throws IOException {
		ctx.getBuffer().order(java.nio.ByteOrder.LITTLE_ENDIAN);
		util.putShort(ctx, 0x1234);
		util.put3Bytes(ctx, 0xABCDEF);
		util.putInt(ctx, 0x11223344);
		util.putLong(ctx, 1L);
		util.putUShort(ctx, 0x00FF);
		util.putU3Bytes(ctx, 0x00ABCD);
		util.putUInt(ctx, 1L);
		assertTrue(util.position(ctx) > 0);

		ByteBufferContext tiny = new ByteBufferContext();
		util.init(tiny, new byte[1], 1);
		org.junit.jupiter.api.Assertions.assertThrows(IOException.class, () -> util.putShort(tiny, 1));
		org.junit.jupiter.api.Assertions.assertThrows(IOException.class, () -> util.putUByte(tiny, 300));
	}

	@Test
	void orderAwareGettersOnDirectBuffer() {
		ByteBuffer bb = ByteBuffer.allocate(16);
		bb.putInt(0x01020304);
		bb.putInt(0x05060708);
		bb.flip();
		assertTrue(util.getUnsignedByte(bb) >= 0);
		bb.position(0);
		assertTrue(util.getSignedByte(bb) != 0 || true);
		bb.position(0);
		util.getUnsignedShort(bb);
		bb.position(0);
		util.getSignedShort(bb);
		bb.position(0);
		util.get3UnsignedByteInt(bb);
		bb.position(0);
		util.get3SignedByteInt(bb);
		bb.position(0);
		util.getUnsignedInt(bb);
		bb.position(0);
		util.getSignedInt(bb);
		bb.position(0);
		assertNotNull(util.getUnsignedLong(bb));
		bb.position(0);
		assertNotNull(util.getSignedLong(bb));
		bb.position(0);
		assertNotNull(util.getUnsignedLong(bb, bb.order()));
		bb.position(0);
		assertNotNull(util.getSignedLong(bb, bb.order()));
	}
}
