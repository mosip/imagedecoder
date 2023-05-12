package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
RAW encoding operations
*/
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
}
