package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Byte input-output stream (CIO)
 */
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
}
