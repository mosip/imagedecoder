package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tier-2 coding (coding of code-block coefficients)
 */
@Data
@ToString
public class Tier2 {
	/** codec context */
	private CodecContextInfo codecContextInfo;

	/** Encoding: pointer to the src image. Decoding: pointer to the dst image. */
	private OpenJpegImage image;
	/** pointer to the image coding parameters */
	private CodingParameters codingParameters;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Tier2 tier2 = (Tier2) o;
		return Objects.equals(codecContextInfo, tier2.codecContextInfo) && Objects.equals(image, tier2.image)
				&& Objects.equals(codingParameters, tier2.codingParameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(codecContextInfo, image, codingParameters);
	}

	public boolean canEqual(Object other) {
		return other instanceof Tier2;
	}
}