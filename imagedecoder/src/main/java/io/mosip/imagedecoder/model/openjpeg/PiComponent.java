package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * pi Component
 */
@Data
@ToString
public class PiComponent {
	private int dX;
	private int dY;
	/** number of resolution levels */
	private int noOfResolutions;
	private PiResolution[] resolutions;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PiComponent))
			return false;
		PiComponent that = (PiComponent) obj;
		return canEqual(that) && dX == that.dX && dY == that.dY && noOfResolutions == that.noOfResolutions
				&& Arrays.equals(resolutions, that.resolutions);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(dX, dY, noOfResolutions);
		result = 31 * result + Arrays.hashCode(resolutions);
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof PiComponent;
	}
}