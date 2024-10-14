package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * RAW encoding operations
 */
@Data
@ToString
public class Raw {
	/** temporary buffer where bits are coded or decoded */
	private int c;
	/** number of bits already read or free to write */
	private long ct;
	/** maximum length to decode */
	private long lengthMax;
	/** length decoded */
	private long length;
	/** pointer to the current position in the buffer */
	private int bpIndex;
	/** buffer */
	private byte[] bp;
	/** pointer to the start of the buffer */
	private int start;
	/** pointer to the end of the buffer */
	private int end;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Raw))
			return false;
		Raw that = (Raw) obj;
		return canEqual(that) && c == that.c && ct == that.ct && lengthMax == that.lengthMax && length == that.length
				&& bpIndex == that.bpIndex && start == that.start && end == that.end && Arrays.equals(bp, that.bp);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(c, ct, lengthMax, length, bpIndex, start, end);
		result = 31 * result + Arrays.hashCode(bp);
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof Raw;
	}
}