package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tile resolution
 */
@Data
@ToString
public class TcdResolution {
	private int x0;
	private int y0;
	private int x1;
	private int y1; /*
					 * dimension of the resolution level : left upper corner (x0, y0) right low
					 * corner (x1,y1)
					 */
	private int pWidth;
	private int pHeight;
	private int noOfBands; /* number sub-band for the resolution level */
	private TcdBand[] bands = new TcdBand[3]; /* subband information */

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TcdResolution that = (TcdResolution) o;
		return x0 == that.x0 && y0 == that.y0 && x1 == that.x1 && y1 == that.y1 && pWidth == that.pWidth
				&& pHeight == that.pHeight && noOfBands == that.noOfBands && Arrays.equals(bands, that.bands);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(x0, y0, x1, y1, pWidth, pHeight, noOfBands);
		result = 31 * result + Arrays.hashCode(bands);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TcdResolution;
	}
}