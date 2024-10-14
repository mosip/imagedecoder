package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tile comp
 */
@Data
@ToString
public class TcdTileComponent {
	private int x0;
	private int y0;
	private int x1;
	private int y1; /*
					 * dimension of component : left upper corner (x0, y0) right low corner (x1,y1)
					 */
	private int noOfResolutions; /* number of resolutions level */
	private TcdResolution[] resolutions; /* resolutions information */
	private int[] iData; /* data of the component */
	private double[] fData; /* data of the component */
	private int noOfPixels; /* add fixed_quality */

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TcdTileComponent that = (TcdTileComponent) o;
		return x0 == that.x0 && y0 == that.y0 && x1 == that.x1 && y1 == that.y1
				&& noOfResolutions == that.noOfResolutions && noOfPixels == that.noOfPixels
				&& Arrays.equals(resolutions, that.resolutions) && Arrays.equals(iData, that.iData)
				&& Arrays.equals(fData, that.fData);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(x0, y0, x1, y1, noOfResolutions, noOfPixels);
		result = 31 * result + Arrays.hashCode(resolutions);
		result = 31 * result + Arrays.hashCode(iData);
		result = 31 * result + Arrays.hashCode(fData);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TcdTileComponent;
	}
}