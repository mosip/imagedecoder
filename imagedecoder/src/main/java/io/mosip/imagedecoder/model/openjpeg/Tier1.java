package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tier-1 coding (coding of code-block coefficients)
*/
public class Tier1 {
	/** codec context */
	private CodecContextInfo codecContextInfo;

	/** MQC component */
	private MQCoder mqc;
	/** RAW component */
	private Raw raw;

	private int[] data;
	private int[] flags;
	private int width;
	private int height;
	private int dataSize;
	private int flagsSize;
	private int flagsStride;
}
