package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Quantization stepsize
 */
@Data
@ToString
public class StepSize {
	/** exponent */
	private int expn;
	/** mantissa */
	private int mant;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof StepSize))
			return false;
		StepSize that = (StepSize) obj;
		return canEqual(that) && expn == that.expn && mant == that.mant;
	}

	@Override
	public int hashCode() {
		return Objects.hash(expn, mant);
	}

	public boolean canEqual(Object obj) {
		return obj instanceof StepSize;
	}
}