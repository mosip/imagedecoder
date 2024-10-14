package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tile block encode
 */
@Data
@ToString
public class TcdCodeBlockEncoder {
	private int dataIndex; /* Data Index */
	private byte[] data; /* Data */
	private TcdLayer[] layers; /* layer information */
	private TcdPass[] passes; /* information about the passes */
	private int x0;
	private int y0;
	private int x1;
	private int y1; /*
					 * dimension of the code-blocks : left upper corner (x0, y0) right low corner
					 * (x1,y1)
					 */
	private int noOfBps;
	private int noOfLengthBits;
	private int noOfPasses; /* number of pass already done for the code-blocks */
	private int noOfPassesInLayers; /* number of passes in the layer */
	private int totalPasses; /* total number of passes */

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TcdCodeBlockEncoder that = (TcdCodeBlockEncoder) o;
		return dataIndex == that.dataIndex && x0 == that.x0 && y0 == that.y0 && x1 == that.x1 && y1 == that.y1
				&& noOfBps == that.noOfBps && noOfLengthBits == that.noOfLengthBits && noOfPasses == that.noOfPasses
				&& noOfPassesInLayers == that.noOfPassesInLayers && totalPasses == that.totalPasses
				&& Arrays.equals(data, that.data) && Arrays.equals(layers, that.layers)
				&& Arrays.equals(passes, that.passes);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(dataIndex, x0, y0, x1, y1, noOfBps, noOfLengthBits, noOfPasses, noOfPassesInLayers,
				totalPasses);
		result = 31 * result + Arrays.hashCode(data);
		result = 31 * result + Arrays.hashCode(layers);
		result = 31 * result + Arrays.hashCode(passes);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TcdCodeBlockEncoder;
	}
}