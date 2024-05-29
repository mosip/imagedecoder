package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Message Header JPT stream structure
 */
public class JPTMessageHeader {
	/** In-class Identifier */
	@SuppressWarnings({ "java:S116" })
	private long Id;
	/** Last byte information */
	private long lastByte;	
	/** Class Identifier */
	private long classId;	
	/** CSn : index identifier */
	private long cSnId;
	/** Message offset */
	private long msgOffset;
	/** Message length */
	private long msgLength;
	/** Auxiliary for JPP case */
	private long layerNb;
}
