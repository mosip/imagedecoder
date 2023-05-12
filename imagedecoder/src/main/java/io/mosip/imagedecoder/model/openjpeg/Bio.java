package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Individual bit input-output stream (BIO)
*/
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
}
