package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tile layers
 */
@Data
@ToString
public class TcdLayer {
	private int noOfPasses; /* Number of passes in the layer */
	private int length; /* len of information */
	private double distortion; /* add for index */
	private byte[] data; /* data */

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TcdLayer tcdLayer = (TcdLayer) o;
		return noOfPasses == tcdLayer.noOfPasses && length == tcdLayer.length
				&& Double.compare(tcdLayer.distortion, distortion) == 0 && Arrays.equals(data, tcdLayer.data);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(noOfPasses, length, distortion);
		result = 31 * result + Arrays.hashCode(data);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TcdLayer;
	}
}