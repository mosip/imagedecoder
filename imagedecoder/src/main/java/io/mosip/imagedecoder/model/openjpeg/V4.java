package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class V4 {
	private double[] f = new double[4];

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		V4 v4 = (V4) o;
		return Arrays.equals(f, v4.f);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(f);
	}

	public boolean canEqual(Object other) {
		return other instanceof V4;
	}
}