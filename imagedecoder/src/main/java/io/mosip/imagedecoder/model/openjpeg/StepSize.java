package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Quantization stepsize
*/
public class StepSize {
	/** exponent */
	private int expn;
	/** mantissa */
	private int mant;
}