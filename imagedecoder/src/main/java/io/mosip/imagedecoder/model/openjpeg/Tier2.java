package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tier-2 coding (coding of code-block coefficients)
*/
public class Tier2 {
	/** codec context */
	private CodecContextInfo codecContextInfo;

	/** Encoding: pointer to the src image. Decoding: pointer to the dst image. */
	private OpenJpegImage image;
	/** pointer to the image coding parameters */
	private CodingParameters codingParameters;
}
