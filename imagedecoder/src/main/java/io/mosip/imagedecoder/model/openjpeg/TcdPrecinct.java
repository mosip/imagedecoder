package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tile precinct
 */
@Data
@ToString
public class TcdPrecinct {
	private int x0;
	private int y0;
	private int x1;
	private int y1; /*
					 * dimension of the precinct : left upper corner (x0, y0) right low corner
					 * (x1,y1)
					 */
	private int cWidth; /* number of precinct in width */
	private int cHeight; /* number of precinct in heigth */
	/* code-blocks informations */
	private TcdCodeBlockEncoder[] tcdCodeBlockEncoder;
	private TcdCodeBlockDecoder[] tcdCodeBlockDecoder;
	private TgtTree inclTree; /* inclusion tree */
	private TgtTree imsbTree; /* IMSB tree */

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TcdPrecinct that = (TcdPrecinct) o;
		return x0 == that.x0 && y0 == that.y0 && x1 == that.x1 && cWidth == that.cWidth && cHeight == that.cHeight
				&& Arrays.equals(tcdCodeBlockEncoder, that.tcdCodeBlockEncoder)
				&& Arrays.equals(tcdCodeBlockDecoder, that.tcdCodeBlockDecoder)
				&& Objects.equals(inclTree, that.inclTree) && Objects.equals(imsbTree, that.imsbTree);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(x0, y0, x1, cWidth, cHeight, inclTree, imsbTree);
		result = 31 * result + Arrays.hashCode(tcdCodeBlockEncoder);
		result = 31 * result + Arrays.hashCode(tcdCodeBlockDecoder);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TcdPrecinct;
	}
}