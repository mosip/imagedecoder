package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tile
 */
@Data
@ToString
public class TcdTile {
	private int x0;
	private int y0;
	private int x1;
	private int y1; /*
					 * dimension of the tile : left upper corner (x0, y0) right low corner (x1,y1)
					 */
	private int noOfComps; /* number of components in tile */
	private TcdTileComponent[] comps; /* Components information */
	private int noOfPixels; /* add fixed_quality */
	private double distortionTile; /* add fixed_quality */
	private double[] distortionLayer = new double[100]; /* add fixed_quality */
	/** packet number */
	private int packetNo;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TcdTile tcdTile = (TcdTile) o;
		return x0 == tcdTile.x0 && y0 == tcdTile.y0 && x1 == tcdTile.x1 && y1 == tcdTile.y1
				&& noOfComps == tcdTile.noOfComps && noOfPixels == tcdTile.noOfPixels
				&& Double.compare(tcdTile.distortionTile, distortionTile) == 0 && packetNo == tcdTile.packetNo
				&& Arrays.equals(comps, tcdTile.comps) && Arrays.equals(distortionLayer, tcdTile.distortionLayer);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(x0, y0, x1, y1, noOfComps, noOfPixels, distortionTile, packetNo);
		result = 31 * result + Arrays.hashCode(comps);
		result = 31 * result + Arrays.hashCode(distortionLayer);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TcdTile;
	}
}