package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Individual bit input-output stream (BIO)
 */
@Data
@ToString
public class Bio {
	/** pointer to the start of the buffer */
	private int start;
	/** pointer to the end of the buffer */
	private int end;
	private int bpIndex;
	/** pointer to the present position in the buffer */
	private byte[] bp;
	/** temporary place where each byte is read or written */
	private long buf;
	/** coder : number of bits free to write. decoder : number of bits read */
	private int ct;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Bio))
			return false;
		Bio that = (Bio) obj;
		return start == that.start && end == that.end && bpIndex == that.bpIndex && buf == that.buf && ct == that.ct
				&& Arrays.equals(bp, that.bp);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(start, end, bpIndex, buf, ct);
		result = 31 * result + Arrays.hashCode(bp);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof Bio;
	}
}