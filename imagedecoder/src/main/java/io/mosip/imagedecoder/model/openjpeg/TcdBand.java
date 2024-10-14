package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tile band
 */
@Data
@ToString
public class TcdBand {
	private int x0; /*
					 * dimension of the subband : left upper corner (x0, y0) right low corner
					 * (x1,y1)
					 */
	private int y0; /*
					 * dimension of the subband : left upper corner (x0, y0) right low corner
					 * (x1,y1)
					 */
	private int x1; /*
					 * dimension of the subband : left upper corner (x0, y0) right low corner
					 * (x1,y1)
					 */
	private int y1; /*
					 * dimension of the subband : left upper corner (x0, y0) right low corner
					 * (x1,y1)
					 */
	private int bandNo;
	private TcdPrecinct[] precincts; /* precinct information */
	private int noOfBps;
	private float stepSize;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TcdBand tcdBand = (TcdBand) o;
		return x0 == tcdBand.x0 && y0 == tcdBand.y0 && x1 == tcdBand.x1 && y1 == tcdBand.y1 && bandNo == tcdBand.bandNo
				&& noOfBps == tcdBand.noOfBps && Float.compare(tcdBand.stepSize, stepSize) == 0
				&& Arrays.equals(precincts, tcdBand.precincts);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(x0, y0, x1, y1, bandNo, noOfBps, stepSize);
		result = 31 * result + Arrays.hashCode(precincts);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TcdBand;
	}
}