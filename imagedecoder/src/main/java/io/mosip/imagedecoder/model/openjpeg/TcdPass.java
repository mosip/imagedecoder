package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * pass
 */
@Data
@ToString
public class TcdPass {
	private int rate;
	private double distortionDec;
	private int term;
	private int length;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TcdPass tcdPass = (TcdPass) o;
		return rate == tcdPass.rate && term == tcdPass.term && length == tcdPass.length
				&& Double.compare(tcdPass.distortionDec, distortionDec) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(rate, distortionDec, term, length);
	}

	public boolean canEqual(Object other) {
		return other instanceof TcdPass;
	}
}