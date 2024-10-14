package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Index structure : information regarding tiles
 */
@Data
@ToString
public class TileInfo {
	/** value of threshold for each layer by tile cfr. Marcela */
	private double[] thresh;
	/** number of tile */
	private int tileNo;
	/** start position */
	private int startPosition;
	/** end position of the header */
	private int endHeader;
	/** end position */
	private int endPosition;
	/** precinct number for each resolution level (width) */
	private int[] pWidth = new int[33];
	/** precinct number for each resolution level (height) */
	private int[] pHeight = new int[33];
	/** precinct size (in power of 2), in X for each resolution level */
	private int[] pDX = new int[33];
	/** precinct size (in power of 2), in Y for each resolution level */
	private int[] pDY = new int[33];
	/** information concerning packets inside tile */
	private PacketInfo[] packet;
	/** add fixed_quality */
	private int noOfPixel;
	/** add fixed_quality */
	private double distortionTile;
	/** number of tile parts */
	private int noOfTileParts;
	/** information concerning tile parts */
	private TpInfo[] tp;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TileInfo that = (TileInfo) o;
		return tileNo == that.tileNo && startPosition == that.startPosition && endHeader == that.endHeader
				&& endPosition == that.endPosition && noOfPixel == that.noOfPixel
				&& Double.compare(that.distortionTile, distortionTile) == 0 && noOfTileParts == that.noOfTileParts
				&& Arrays.equals(thresh, that.thresh) && Arrays.equals(pWidth, that.pWidth)
				&& Arrays.equals(pHeight, that.pHeight) && Arrays.equals(pDX, that.pDX) && Arrays.equals(pDY, that.pDY)
				&& Arrays.equals(packet, that.packet) && Arrays.equals(tp, that.tp);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(tileNo, startPosition, endHeader, endPosition, noOfPixel, distortionTile,
				noOfTileParts);
		result = 31 * result + Arrays.hashCode(thresh);
		result = 31 * result + Arrays.hashCode(pWidth);
		result = 31 * result + Arrays.hashCode(pHeight);
		result = 31 * result + Arrays.hashCode(pDX);
		result = 31 * result + Arrays.hashCode(pDY);
		result = 31 * result + Arrays.hashCode(packet);
		result = 31 * result + Arrays.hashCode(tp);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TileInfo;
	}
}