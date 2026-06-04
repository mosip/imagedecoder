package io.mosip.imagedecoder.util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import io.mosip.imagedecoder.constant.DecoderConstant;
import io.mosip.imagedecoder.constant.DecoderErrorCodes;
import io.mosip.imagedecoder.exceptions.DecoderException;
import io.mosip.imagedecoder.model.ByteBufferContext;

public class ByteStreamUtil {
	// Static variable reference of singleInstance of type Singleton
    private static ByteStreamUtil singleInstance = null;    
    private ByteStreamUtil()
	{ 
		super ();
	} 
  
	//synchronized method to control simultaneous access 
	public static synchronized ByteStreamUtil getInstance()
	{ 
		if (singleInstance == null)
			singleInstance = new ByteStreamUtil();
  
        return singleInstance;
	}
	
	public ByteOrder getByteOrder() {
		return ByteOrder.nativeOrder();
	}

	public int arraySize(Object[] array) {
		return array.length;
	}

	public void init(ByteBufferContext byteBufCont, byte[] buf, int bufSize) {
		if (bufSize < 0)
			throw new DecoderException(DecoderErrorCodes.INVALID_DATA_ERROR.getErrorCode(), DecoderErrorCodes.INVALID_DATA_ERROR.getErrorMessage());
		
		byteBufCont.setBuffer(ByteBuffer.wrap(buf));
		byteBufCont.getBuffer().rewind();
	}

	public int getBuffer(ByteBufferContext byteBufCont, byte[] target, int size) {
		int pos = byteBufCont.getBuffer().position();
		int size2 = Math.min(byteBufCont.getBuffer().capacity() - byteBufCont.getBuffer().position(), size);
		System.arraycopy(byteBufCont.getBuffer().array(), pos, target, 0, size2);
		byteBufCont.getBuffer().position(pos + size2);
		return size2;
	}

	public int getBufferU(ByteBufferContext byteBufCont, byte[] target, int size) {
		int pos = byteBufCont.getBuffer().position();
		 // Validate that the requested size does not exceed remaining bytes
	    int remaining = byteBufCont.getBuffer().remaining();
	    if (size > remaining) {
	    	size = remaining;
    	}
	    
		System.arraycopy(byteBufCont.getBuffer().array(), pos, target, 0, size);
		byteBufCont.getBuffer().position(pos + size);
		return size;
	}

	public int getBufferU(ByteBufferContext byteBufCont, byte[] target, int dstPos, int size) {
		int pos = byteBufCont.getBuffer().position();
		 // Validate that the requested size does not exceed remaining bytes
	    int remaining = byteBufCont.getBuffer().remaining();
	    if (size > remaining) {
	    	size = remaining;
    	}
		System.arraycopy(byteBufCont.getBuffer().array(), pos, target, dstPos, size);
		byteBufCont.getBuffer().position(pos + size);
		return size;
	}

	public int getBytesLeft(ByteBufferContext byteBufCont) {
		return byteBufCont.getBuffer().capacity() - byteBufCont.getBuffer().position();
	}

	public int position(ByteBufferContext byteBufCont) {
		return byteBufCont.getBuffer().position();
	}

	public int currentSize(ByteBufferContext byteBufCont) {
		return byteBufCont.getBuffer().remaining();
	}

	public void skipBytesForSize(ByteBufferContext byteBufCont, int size) {
		int value = Math.min(byteBufCont.getBuffer().capacity() - byteBufCont.getBuffer().position(), size);
		byteBufCont.getBuffer().position(byteBufCont.getBuffer().position() + value);
	}

	public void skipBytesForSizeU(ByteBufferContext byteBufCont, int size) {
		byteBufCont.getBuffer().position(byteBufCont.getBuffer().position() + size);
	}

	public long getByte(ByteBufferContext byteBufCont) {
		return getSignedVarInt(byteBufCont.getBuffer(), 1);
	}

	public long getShort(ByteBufferContext byteBufCont) {
		return getSignedVarInt(byteBufCont.getBuffer(), 2);
	}

	public long get3Bytes(ByteBufferContext byteBufCont) {
		return getSignedVarInt(byteBufCont.getBuffer(), 3);
	}

	public long getInt(ByteBufferContext byteBufCont) {
		return getSignedVarInt(byteBufCont.getBuffer(), 4);
	}

	public BigInteger getLong(ByteBufferContext byteBufCont) {
		return getSignedLong(byteBufCont.getBuffer(), 8);
	}

	public long getUByte(ByteBufferContext byteBufCont) {
		return getUnsignedVarInt(byteBufCont.getBuffer(), 1);
	}

	public long getUShort(ByteBufferContext byteBufCont) {
		return getUnsignedVarInt(byteBufCont.getBuffer(), 2);
	}

	public long getU3Bytes(ByteBufferContext byteBufCont) {
		return getUnsignedVarInt(byteBufCont.getBuffer(), 3);
	}

	public long getUInt(ByteBufferContext byteBufCont) {
		return getUnsignedVarInt(byteBufCont.getBuffer(), 4);
	}

	public BigInteger getULong(ByteBufferContext byteBufCont) {
		return getUnsignedLong(byteBufCont.getBuffer(), 8);
	}

	public long putByte(ByteBufferContext byteBufCont, int value) throws IOException {
		if (byteBufCont.getBuffer().remaining() < 1)
            throw new IOException("not enough space in buffer to write signed byte");
		
		byteBufCont.getBuffer().put((byte)value);
		return byteBufCont.getBuffer().position();
	}

	public long putShort(ByteBufferContext byteBufCont, int value) throws IOException {
		if (byteBufCont.getBuffer().remaining() < 2)
            throw new IOException("not enough space in buffer to write signed short");

		if (byteBufCont.getBuffer().order() == ByteOrder.BIG_ENDIAN)
			value = ByteSwapperUtil.getInstance().swapShort((short)value);

		byteBufCont.getBuffer().putShort((short)value);
		return byteBufCont.getBuffer().position();
	}

	public long put3Bytes(ByteBufferContext byteBufCont, int value) throws IOException {
		if (byteBufCont.getBuffer().remaining() < 3)
            throw new IOException("not enough space in buffer to write signed 3 bytes");

		if (byteBufCont.getBuffer().order() == ByteOrder.BIG_ENDIAN)
			value = ByteSwapperUtil.getInstance().swap3Bytes(value);

		byteBufCont.getBuffer().put((byte) ((value >> 0) & 0xff));
		byteBufCont.getBuffer().put((byte) ((value >> 8) & 0xff));
		byteBufCont.getBuffer().put((byte) ((value >> 16) & 0xff));
		return byteBufCont.getBuffer().position();
	}

	public long putInt(ByteBufferContext byteBufCont, int value) throws IOException {
		if (byteBufCont.getBuffer().remaining() < 4)
            throw new IOException("not enough space in buffer to write signed Integer");

		if (byteBufCont.getBuffer().order() == ByteOrder.BIG_ENDIAN)
			value = ByteSwapperUtil.getInstance().swapInt(value);

		byteBufCont.getBuffer().putInt(value);
		return byteBufCont.getBuffer().position();
	}

	public long putLong(ByteBufferContext byteBufCont, long value) throws IOException {
		if (byteBufCont.getBuffer().remaining() < 8)
            throw new IOException("not enough space in buffer to write signed Long");

		if (byteBufCont.getBuffer().order() == ByteOrder.BIG_ENDIAN)
			value = ByteSwapperUtil.getInstance().swapLong(value);

		byteBufCont.getBuffer().putLong(value);
		return byteBufCont.getBuffer().position();
	}

	public long putUByte(ByteBufferContext byteBufCont, int value) throws IOException {
		if (byteBufCont.getBuffer().remaining() < 1)
            throw new IOException("not enough space in buffer to write unsigned byte");
		
		if (value > 255)
            throw new IOException("int value exceeds maximum unsigned byte value of 255!");
		
		byteBufCont.getBuffer().put((byte)(value & 0xff));
		return byteBufCont.getBuffer().position();
	}

	public long putUShort(ByteBufferContext byteBufCont, int value) throws IOException {
		if (byteBufCont.getBuffer().remaining() < 2)
            throw new IOException("not enough space in buffer to write unsigned short");

		if (value > 65535)
            throw new IOException("int value exceeds maximum unsigned short value of 65535!");
		
		if (byteBufCont.getBuffer().order() == ByteOrder.BIG_ENDIAN)
			value = ByteSwapperUtil.getInstance().swapShort((short)value);

		byte b1 = (byte) (0xff & (value));
        byte b2 = (byte) (0xff & (value >> 8));

        byteBufCont.getBuffer().put(b1);
        byteBufCont.getBuffer().put(b2);
        return byteBufCont.getBuffer().position();
	}

	public long putU3Bytes(ByteBufferContext byteBufCont, int value) throws IOException {
		if (byteBufCont.getBuffer().remaining() < 3)
            throw new IOException("not enough space in buffer to write unsigned 3 bytes");

		if (byteBufCont.getBuffer().order() == ByteOrder.BIG_ENDIAN)
			value = ByteSwapperUtil.getInstance().swap3Bytes(value);

        byteBufCont.getBuffer().put((byte) ((value) & 0xff));
		byteBufCont.getBuffer().put((byte) ((value >> 8) & 0xff));
		byteBufCont.getBuffer().put((byte) ((value >> 16) & 0xff));
		return byteBufCont.getBuffer().position();
	}

	public long putUInt(ByteBufferContext byteBufCont, long value) throws IOException {
		if (byteBufCont.getBuffer().remaining() < 4)
            throw new IOException("not enough space in buffer to write unsigned Integer");

		if (byteBufCont.getBuffer().order() == ByteOrder.BIG_ENDIAN)
			value = ByteSwapperUtil.getInstance().swapLong(value);

		byteBufCont.getBuffer().putInt((int) (value & 0xffffffffL));
		return byteBufCont.getBuffer().position();
	}

	public ByteBuffer copy(ByteBuffer bb, boolean forceDirect) {
		int capacity = bb.limit();
		int pos = bb.position();
		ByteOrder order = bb.order();
		ByteBuffer copy;

		if (bb.isDirect() || forceDirect) {
			copy = ByteBuffer.allocateDirect(capacity);
		} else {
			copy = ByteBuffer.allocate(capacity);
		}

		bb.rewind();

		copy.order(order);
		copy.put(bb);
		copy.position(pos);

		bb.position(pos);

		return copy;
	}

	public ByteBuffer bytebuffercopy(ByteBuffer bb) {
		return copy(bb, false);
	}

	public long peekByte(ByteBufferContext byteBufCont) {
		int pos = byteBufCont.getBuffer().position();
		long value = getSignedVarInt(byteBufCont.getBuffer(), 1);
		byteBufCont.getBuffer().position(pos);
		return value;
	}

	public long peekShort(ByteBufferContext byteBufCont) {
		int pos = byteBufCont.getBuffer().position();
		long value = getSignedVarInt(byteBufCont.getBuffer(), 2);
		byteBufCont.getBuffer().position(pos);
		return value;
	}

	public long peek3Bytes(ByteBufferContext byteBufCont) {
		int pos = byteBufCont.getBuffer().position();
		long value = getSignedVarInt(byteBufCont.getBuffer(), 3);
		byteBufCont.getBuffer().position(pos);
		return value;
	}

	public long peekInt(ByteBufferContext byteBufCont) {
		int pos = byteBufCont.getBuffer().position();
		long value = getSignedVarInt(byteBufCont.getBuffer(), 4);
		byteBufCont.getBuffer().position(pos);
		return value;
	}

	public BigInteger peekLong(ByteBufferContext byteBufCont) {
		int pos = byteBufCont.getBuffer().position();
		BigInteger value = getSignedLong(byteBufCont.getBuffer(), 8);
		byteBufCont.getBuffer().position(pos);
		return value;
	}

	public long peekUByte(ByteBufferContext byteBufCont) {
		int pos = byteBufCont.getBuffer().position();
		long value = getUnsignedVarInt(byteBufCont.getBuffer(), 1);
		byteBufCont.getBuffer().position(pos);
		return value;
	}

	public long peekUShort(ByteBufferContext byteBufCont) {
		int pos = byteBufCont.getBuffer().position();
		long value = getUnsignedVarInt(byteBufCont.getBuffer(), 2);
		byteBufCont.getBuffer().position(pos);
		return value;
	}

	public long peekU3Bytes(ByteBufferContext byteBufCont) {
		int pos = byteBufCont.getBuffer().position();
		long value = getUnsignedVarInt(byteBufCont.getBuffer(), 3);
		byteBufCont.getBuffer().position(pos);
		return value;
	}

	public long peekUInt(ByteBufferContext byteBufCont) {
		int pos = byteBufCont.getBuffer().position();
		long value = getUnsignedVarInt(byteBufCont.getBuffer(), 4);
		byteBufCont.getBuffer().position(pos);
		return value;
	}

	public BigInteger peekULong(ByteBufferContext byteBufCont) {
		int pos = byteBufCont.getBuffer().position();
		BigInteger value = getUnsignedLong(byteBufCont.getBuffer(), 8);
		byteBufCont.getBuffer().position(pos);
		return value;
	}

	public int seek(ByteBufferContext byteBufCont, int offset, int whence) {
		DecoderErrorCodes errorCode = null;
		switch (whence) {
		case DecoderConstant.WHENCE_SEEK_CUR:
			offset = avClip(offset, -(byteBufCont.getBuffer().position()), byteBufCont.getBuffer().remaining());
			byteBufCont.getBuffer().position(byteBufCont.getBuffer().position() + offset);
			break;
		case DecoderConstant.WHENCE_SEEK_END:
			offset = avClip(offset, -(byteBufCont.getBuffer().position()), 0);
			byteBufCont.getBuffer().position(byteBufCont.getBuffer().remaining() + offset);
			break;
		case DecoderConstant.WHENCE_SEEK_SET:
			offset = avClip(offset, 0, byteBufCont.getBuffer().remaining());
			byteBufCont.getBuffer().position(0 + offset);
			break;
		default:
			errorCode = DecoderErrorCodes.INVALID_DATA_ERROR;
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}
		return position(byteBufCont);
	}

	public int avClip(int a, int amin, int amax) {
		if (a < amin)
			return amin;
		else if (a > amax)
			return amax;
		else
			return a;
	}

	public int makeTag(char aInfo, char bInfo, char cInfo, char dInfo) {
		return ((aInfo) | ((bInfo) << 8) | ((cInfo) << 16) | ((dInfo) << 24));
	}

	public int makeBETag(char aInfo, char bInfo, char cInfo, char dInfo) {
		return ((dInfo) | ((cInfo) << 8) | ((bInfo) << 16) | ((aInfo) << 24));
	}

	public int ffErrorTag(char aInfo, char bInfo, char cInfo, char dInfo) {
		return ((dInfo) | ((cInfo) << 8) | ((bInfo) << 16) | ((aInfo) << 24)) * -1;
	}

	/**
	 * Read an unsigned variable length int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the variable length int
	 * @return The unsigned long
	 */
	public long getUnsignedVarInt(ByteBuffer byteBuffer, int numBytes) {
		int pos = byteBuffer.position();
		long rtn = getUnsignedVarInt(byteBuffer, pos, numBytes);
		byteBuffer.position(pos + numBytes);
		return rtn;
	}

	/**
	 * Read an Signed variable length int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the variable length int
	 * @return The Signed long
	 */
	public long getSignedVarInt(ByteBuffer byteBuffer, int numBytes) {
		int pos = byteBuffer.position();
		long rtn = getSignedVarInt(byteBuffer, pos, numBytes);
		byteBuffer.position(pos + numBytes);
		return rtn;
	}

	/**
	 * Read an unsigned variable length int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the variable length int
	 * @param offset Offset at which to read the value
	 * @return The unsigned long
	 */
	public long getUnsignedVarInt(ByteBuffer byteBuffer, int offset, int numBytes) {
		switch (numBytes) {
		case 1:
			return getUnsignedByte(byteBuffer, offset);
		case 2:
			return getUnsignedShort(byteBuffer, offset);
		case 3:
			return get3UnsignedByteInt(byteBuffer, offset);
		case 4:
			return getUnsignedInt(byteBuffer, offset);
		default:
			throw new DecoderException(DecoderErrorCodes.TECHNICAL_ERROR_EXCEPTION.getErrorCode(), "Invalid num bytes " + numBytes);
		}
	}

	/**
	 * Read an Signed variable length int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the variable length int
	 * @param offset Offset at which to read the value
	 * @return The Signed long
	 */
	public long getSignedVarInt(ByteBuffer byteBuffer, int offset, int numBytes) {
		switch (numBytes) {
		case 1:
			return getSignedByte(byteBuffer, offset);
		case 2:
			return getSignedShort(byteBuffer, offset);
		case 3:
			return get3SignedByteInt(byteBuffer, offset);
		case 4:
			return getSignedInt(byteBuffer, offset);
		default:
			throw new IllegalArgumentException("Invalid num bytes " + numBytes);
		}
	}

	/**
	 * Read an unsigned byte from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @return The unsigned byte as an int
	 */
	public int getUnsignedByte(ByteBuffer byteBuffer) {
		int pos = byteBuffer.position();
		int rtn = getUnsignedByte(byteBuffer, pos);
		byteBuffer.position(pos + 1);
		return rtn;
	}

	/**
	 * Read an Signed byte from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @return The Signed byte as an int
	 */
	public int getSignedByte(ByteBuffer byteBuffer) {
		int pos = byteBuffer.position();
		int rtn = getSignedByte(byteBuffer, pos);
		byteBuffer.position(pos + 1);
		return rtn;
	}

	/**
	 * Read an unsigned byte from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to read the byte
	 * @return The unsigned byte as an int
	 */
	public int getUnsignedByte(ByteBuffer byteBuffer, int offset) {
		return asUnsignedByte(byteBuffer.get(offset));
	}

	/**
	 * Read an Signed byte from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to read the byte
	 * @return The Signed byte as an int
	 */
	public int getSignedByte(ByteBuffer byteBuffer, int offset) {
		return asSignedByte(byteBuffer.get(offset));
	}

	/**
	 * Read an Signed short from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the short
	 * @return The Signed short as an int
	 */
	public int getUnsignedShort(ByteBuffer byteBuffer) {
		int pos = byteBuffer.position();
		int rtn = getUnsignedShort(byteBuffer, pos);
		byteBuffer.position(pos + 2);
		return rtn;
	}

	/**
	 * Read an Signed short from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the short
	 * @return The Signed short as an int
	 */
	public int getSignedShort(ByteBuffer byteBuffer) {
		int pos = byteBuffer.position();
		int rtn = getSignedShort(byteBuffer, pos);
		byteBuffer.position(pos + 2);
		return rtn;
	}

	/**
	 * Read an unsigned short from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the short
	 * @param offset Offset at which to read the short
	 * @return The unsigned short as an int
	 */
	public int getUnsignedShort(ByteBuffer byteBuffer, int offset) {
		return asUnsignedShort(byteBuffer.getShort(offset));
	}

	/**
	 * Read an Signed short from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the short
	 * @param offset Offset at which to read the short
	 * @return The Signed short as an int
	 */
	public int getSignedShort(ByteBuffer byteBuffer, int offset) {
		return asSignedShort(byteBuffer.getShort(offset));
	}

	/**
	 * Read a 3 Unsigned byte int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @return The int
	 */
	public int get3UnsignedByteInt(ByteBuffer byteBuffer) {
		return get3UnsignedByteInt(byteBuffer, byteBuffer.order());
	}

	/**
	 * Read a 3 Signed byte int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @return The int
	 */
	public int get3SignedByteInt(ByteBuffer byteBuffer) {
		return get3SignedByteInt(byteBuffer, byteBuffer.order());
	}

	/**
	 * Read a 3 Unsigned byte int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param order  the order of the bytes of the int
	 * @return The int
	 */
	public int get3UnsignedByteInt(ByteBuffer byteBuffer, ByteOrder order) {
		int pos = byteBuffer.position();
		int rtn = get3UnsignedByteInt(byteBuffer, pos, order);
		byteBuffer.position(pos + 3);
		return rtn;
	}

	/**
	 * Read a 3 Signed byte int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param order  the order of the bytes of the int
	 * @return The int
	 */
	public int get3SignedByteInt(ByteBuffer byteBuffer, ByteOrder order) {
		int pos = byteBuffer.position();
		int rtn = get3SignedByteInt(byteBuffer, pos, order);
		byteBuffer.position(pos + 3);
		return rtn;
	}

	/**
	 * Read a 3 Unsigned byte int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @return The int
	 */
	public int get3UnsignedByteInt(ByteBuffer byteBuffer, int offset) {
		return get3UnsignedByteInt(byteBuffer, offset, byteBuffer.order());
	}

	/**
	 * Read a 3 Signed byte int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @return The int
	 */
	public int get3SignedByteInt(ByteBuffer byteBuffer, int offset) {
		return get3SignedByteInt(byteBuffer, offset, byteBuffer.order());
	}

	/**
	 * Read a 3 Unsigned byte int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @param order  the order of the bytes of the int
	 * @return The int
	 */
	public int get3UnsignedByteInt(ByteBuffer byteBuffer, int offset, ByteOrder order) {

		int offInc = 1;
		if (order == ByteOrder.BIG_ENDIAN) {
			offInc = -1;
			offset += 2;
		}

		int rtn = getUnsignedByte(byteBuffer, offset);
		rtn += (getUnsignedByte(byteBuffer, offset + (1 * offInc)) << 8);
		rtn += (getUnsignedByte(byteBuffer, offset + (2 * offInc)) << 16);
		return rtn & 0xFFFFFF;
	}

	/**
	 * Read a 3 Signed byte int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @param order  the order of the bytes of the int
	 * @return The int
	 */
	public int get3SignedByteInt(ByteBuffer byteBuffer, int offset, ByteOrder order) {
		int offInc = 1;
		if (order == ByteOrder.BIG_ENDIAN) {
			offInc = -1;
			offset += 2;
		}

		int rtn = getSignedByte(byteBuffer, offset);
		rtn += (getSignedByte(byteBuffer, offset + (1 * offInc)) << 8);
		rtn += (getSignedByte(byteBuffer, offset + (2 * offInc)) << 16);
		return rtn;
	}

	/**
	 * Read a Unsigned int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @return The long
	 */
	public long getUnsignedInt(ByteBuffer byteBuffer) {
		return getUnsignedInt(byteBuffer, byteBuffer.order());
	}

	/**
	 * Read a Signed int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @return The long
	 */
	public long getSignedInt(ByteBuffer byteBuffer) {
		return getSignedInt(byteBuffer, byteBuffer.order());
	}

	/**
	 * Read a Unsigned int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param order  the order of the bytes of the int
	 * @return The long
	 */
	public long getUnsignedInt(ByteBuffer byteBuffer, ByteOrder order) {
		int pos = byteBuffer.position();
		long rtn = getUnsignedInt(byteBuffer, pos, order);
		byteBuffer.position(pos + 4);
		return rtn;
	}

	/**
	 * Read a Signed int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param order  the order of the bytes of the int
	 * @return The long
	 */
	public long getSignedInt(ByteBuffer byteBuffer, ByteOrder order) {
		int pos = byteBuffer.position();
		long rtn = getSignedInt(byteBuffer, pos, order);
		byteBuffer.position(pos + 4);
		return rtn;
	}

	/**
	 * Read a Unsigned int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @return The long
	 */
	public long getUnsignedInt(ByteBuffer byteBuffer, int offset) {
		return getUnsignedInt(byteBuffer, offset, byteBuffer.order());
	}

	/**
	 * Read a Signed byte int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @return The long
	 */
	public long getSignedInt(ByteBuffer byteBuffer, int offset) {
		return getSignedInt(byteBuffer, offset, byteBuffer.order());
	}

	/**
	 * Read a Unsigned int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @param order  the order of the bytes of the int
	 * @return The long
	 */
	public long getUnsignedInt(ByteBuffer byteBuffer, int offset, ByteOrder order) {

		int offInc = 1;
		if (order == ByteOrder.BIG_ENDIAN) {
			offInc = -1;
			offset += 3;
		}

		long rtn = getUnsignedByte(byteBuffer, offset);
		rtn += (getUnsignedByte(byteBuffer, offset + (1 * offInc)) << 8);
		rtn += (getUnsignedByte(byteBuffer, offset + (2 * offInc)) << 16);
		rtn += (getUnsignedByte(byteBuffer, offset + (3 * offInc)) << 24);
		return rtn & 0xffffffffL;
	}

	/**
	 * Read a Signed int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @param order  the order of the bytes of the int
	 * @return The long
	 */
	public long getSignedInt(ByteBuffer byteBuffer, int offset, ByteOrder order) {

		int offInc = 1;
		if (order == ByteOrder.BIG_ENDIAN) {
			offInc = -1;
			offset += 3;
		}

		long rtn = getSignedByte(byteBuffer, offset);
		rtn += (getSignedByte(byteBuffer, offset + (1 * offInc)) << 8);
		rtn += (getSignedByte(byteBuffer, offset + (2 * offInc)) << 16);
		rtn += (getSignedByte(byteBuffer, offset + (3 * offInc)) << 24);
		return rtn;
	}

	/**
	 * Read a Unsigned long from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @return The BigInteger
	 */
	public BigInteger getUnsignedLong(ByteBuffer byteBuffer) {
		return getUnsignedLong(byteBuffer, byteBuffer.order());
	}

	/**
	 * Read a Signed Long from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @return The BigInteger
	 */
	public BigInteger getSignedLong(ByteBuffer byteBuffer) {
		return getSignedLong(byteBuffer, byteBuffer.order());
	}

	/**
	 * Read a Unsigned Long from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param order  the order of the bytes of the int
	 * @return The BigInteger
	 */
	public BigInteger getUnsignedLong(ByteBuffer byteBuffer, ByteOrder order) {
		int pos = byteBuffer.position();
		BigInteger rtn = getUnsignedLong(byteBuffer, pos, order);
		byteBuffer.position(pos + 8);
		return rtn;
	}

	/**
	 * Read a Signed Long from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param order  the order of the bytes of the int
	 * @return The BigInteger
	 */
	public BigInteger getSignedLong(ByteBuffer byteBuffer, ByteOrder order) {
		int pos = byteBuffer.position();
		BigInteger rtn = getSignedLong(byteBuffer, pos, order);
		byteBuffer.position(pos + 8);
		return rtn;
	}

	/**
	 * Read a Unsigned Long from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @return The BigInteger
	 */
	public BigInteger getUnsignedLong(ByteBuffer byteBuffer, int offset) {
		return getUnsignedLong(byteBuffer, offset, byteBuffer.order());
	}

	/**
	 * Read a Signed byte int from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @return The BigInteger
	 */
	public BigInteger getSignedLong(ByteBuffer byteBuffer, int offset) {
		return getSignedLong(byteBuffer, offset, byteBuffer.order());
	}

	/**
	 * Read a Unsigned Long from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @param order  the order of the bytes of the int
	 * @return The BigInteger
	 */
	public BigInteger getUnsignedLong(ByteBuffer byteBuffer, int offset, ByteOrder order) {

		int offInc = 1;
		if (order == ByteOrder.BIG_ENDIAN) {
			offInc = -1;
			offset += 8;
		}

		long rtn = getSignedByte(byteBuffer, offset);
		rtn += (getSignedByte(byteBuffer, offset + (1 * offInc)) << 8);
		rtn += (getSignedByte(byteBuffer, offset + (2 * offInc)) << 16);
		rtn += (getSignedByte(byteBuffer, offset + (3 * offInc)) << 24);
		rtn += ((long)getSignedByte(byteBuffer, offset + (4 * offInc)) << 32l);  
		rtn += ((long)getSignedByte(byteBuffer, offset + (5 * offInc)) << 40l);
		rtn += ((long)getSignedByte(byteBuffer, offset + (6 * offInc)) << 48l);
		rtn += ((long)getSignedByte(byteBuffer, offset + (7 * offInc)) << 56l);

		return toUnsignedBigInteger (rtn);
	}

	public BigInteger toUnsignedBigInteger(long i) {
	    if (i >= 0L)
	        return BigInteger.valueOf(i);
	    else {
	        int upper = (int) (i >>> 32);
	        int lower = (int) i;

	        // return (upper << 32) + lower
	        return (BigInteger.valueOf(Integer.toUnsignedLong(upper))).shiftLeft(32).
	            add(BigInteger.valueOf(Integer.toUnsignedLong(lower)));
	    }
	}
	/**
	 * Read a Signed Long from a byteBuffer
	 * 
	 * @param byteBuffer Buffer containing the bytes
	 * @param offset Offset at which to start reading the int
	 * @param order  the order of the bytes of the int
	 * @return The BigInteger
	 */
	public BigInteger getSignedLong(ByteBuffer byteBuffer, int offset, ByteOrder order) {

		int offInc = 1;
		if (order == ByteOrder.BIG_ENDIAN) {
			offInc = -1;
			offset += 8;
		}

		BigInteger rtn = BigInteger.valueOf(getSignedByte(byteBuffer, offset));
		rtn = rtn.add((BigInteger.valueOf(getSignedByte(byteBuffer, offset + (1 * offInc))).shiftLeft(8)));
		rtn = rtn.add((BigInteger.valueOf(getSignedByte(byteBuffer, offset + (2 * offInc))).shiftLeft(16)));
		rtn = rtn.add((BigInteger.valueOf(getSignedByte(byteBuffer, offset + (3 * offInc))).shiftLeft(24)));
		rtn = rtn.add((BigInteger.valueOf(getSignedByte(byteBuffer, offset + (4 * offInc))).shiftLeft(32)));
		rtn = rtn.add((BigInteger.valueOf(getSignedByte(byteBuffer, offset + (5 * offInc))).shiftLeft(40)));
		rtn = rtn.add((BigInteger.valueOf(getSignedByte(byteBuffer, offset + (7 * offInc))).shiftLeft(48)));
		rtn = rtn.add((BigInteger.valueOf(getSignedByte(byteBuffer, offset + (8 * offInc))).shiftLeft(56)));
		return rtn;
	}

	/**
	 * @return the byte value converted to an unsigned int value
	 */
	public int asUnsignedByte(byte value) {
		return value & 0xFF;
	}

	/**
	 * @return the byte value converted to an Signed int value
	 */
	public int asSignedByte(byte value) {
		return value;
	}

	/**
	 * @return the short value converted to an unsigned int value
	 */
	public int asUnsignedShort(short s) {
		return s & 0xFFFF;
	}

	/**
	 * @return the short value converted to an Signed int value
	 */
	public int asSignedShort(short s) {
		return s;
	}
	
	public int memCompare(int[] a, int[] b, int sz) {
	    for (int i = 0; i < sz; i++) {
	        if (a[i] != b[i]) {
	            return a[i] - b[i];
	        }
	    }
	    return 0;
	}
}
