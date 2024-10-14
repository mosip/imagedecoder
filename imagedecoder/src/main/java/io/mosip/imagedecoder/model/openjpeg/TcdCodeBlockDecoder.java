package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tile block decode
 */
@Data
@ToString
public class TcdCodeBlockDecoder {
	private byte[] data; /* Data */
	private TcdSegment[] segs; /* segments informations */
	private int x0;
	private int y0;
	private int x1;
	private int y1; /*
					 * dimension of the code-blocks : left upper corner (x0, y0) right low corner
					 * (x1,y1)
					 */
	private int noOfBps;
	private int noOfLengthBits;
	private int length; /* length */
	private int noOfNewPasses; /* number of pass added to the code-blocks */
	private int noOfSegs; /* number of segments */

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TcdCodeBlockDecoder that = (TcdCodeBlockDecoder) o;
		return x0 == that.x0 && y0 == that.y0 && x1 == that.x1 && y1 == that.y1 && noOfBps == that.noOfBps
				&& noOfLengthBits == that.noOfLengthBits && length == that.length && noOfNewPasses == that.noOfNewPasses
				&& noOfSegs == that.noOfSegs && Arrays.equals(data, that.data) && Arrays.equals(segs, that.segs);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(x0, y0, x1, y1, noOfBps, noOfLengthBits, length, noOfNewPasses, noOfSegs);
		result = 31 * result + Arrays.hashCode(data);
		result = 31 * result + Arrays.hashCode(segs);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TcdCodeBlockDecoder;
	}
}