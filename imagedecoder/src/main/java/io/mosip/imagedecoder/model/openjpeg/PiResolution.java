package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * pi resolution
 */
@Data
@ToString
public class PiResolution {
	private int pDX;
	private int pDY;
	private int pWidth;
	private int pHeight;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PiResolution))
			return false;
		PiResolution that = (PiResolution) obj;
		return canEqual(that) && pDX == that.pDX && pDY == that.pDY && pWidth == that.pWidth && pHeight == that.pHeight;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pDX, pDY, pWidth, pHeight);
	}

	public boolean canEqual(Object obj) {
		return obj instanceof PiResolution;
	}
}