package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Byte input-output stream (CIO)
 */
@Data
@ToString
public class Cio {
	/** codec context */
	private CodecContextInfo codecContextInfo;

	/** open mode (read/write) either stream read or stream write */
	private int openMode;
	/** pointer to the start of the buffer */
	private byte[] buffer;
	/** buffer size in bytes */
	private int length;

	/** pointer to the start of the stream */
	private int start;
	/** pointer to the end of the stream */
	private int end;
	/** pointer to the current position */
	private int bpIndex = -1;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Cio))
			return false;
		Cio that = (Cio) obj;
		return openMode == that.openMode && length == that.length && start == that.start && end == that.end
				&& bpIndex == that.bpIndex && Objects.equals(codecContextInfo, that.codecContextInfo)
				&& Arrays.equals(buffer, that.buffer);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(codecContextInfo, openMode, length, start, end, bpIndex);
		result = 31 * result + Arrays.hashCode(buffer);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof Cio;
	}
}