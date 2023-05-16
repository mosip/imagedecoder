package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Common fields between JPEG-2000 compression and decompression master structs.
 */
public class ContextInfo {
	private Object clientData;
	/** < Available for use by application */
	private int isDecompressor;
	/** < So common code can tell which is which */
	private JP2CodecFormat codecFormat;
	/** < selected codec */
	private Object j2kHandle;
	/** < pointer to the J2K codec */
	private Object jp2Handle;
	/** < pointer to the JP2 codec */
	private Object mj2Handle; /** < pointer to the MJ2 codec */
}