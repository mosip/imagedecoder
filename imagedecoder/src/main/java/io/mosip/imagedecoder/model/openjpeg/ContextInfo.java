package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Data
/**
 * Common fields between JPEG-2000 compression and decompression master structs.
 */
public class ContextInfo {
	/* codec handles point back to this context; excluded to avoid toString/hashCode cycles */
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Object clientData;
	/** < Available for use by application */
	private int isDecompressor;
	/** < So common code can tell which is which */
	private JP2CodecFormat codecFormat;
	/** < selected codec */
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Object j2kHandle;
	/** < pointer to the J2K codec */
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Object jp2Handle;
	/** < pointer to the JP2 codec */
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Object mj2Handle; /** < pointer to the MJ2 codec */
}
