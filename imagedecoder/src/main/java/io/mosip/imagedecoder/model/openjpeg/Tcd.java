package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tile coder/decoder
 */
@Data
@ToString
public class Tcd {
	/** Position of the tilepart flag in Progression order */
	private int tilePartPosition;
	/** Tile part number */
	private int tilePartNo;
	/** Current tile part number */
	private int curTilePartNo;
	/** Total number of tileparts of the current tile */
	private int curTotalNoOfTileParts;
	/** Current Packet iterator number */
	private int curPiNo;
	/** codec context */
	private CodecContextInfo codecContextInfo;

	/** info on each image tile */
	private TcdImage tcdImage;
	/** image */
	private OpenJpegImage image;
	/** coding parameters */
	private CodingParameters codingParameters;
	/** pointer to the current encoded/decoded tile */
	private TcdTile tcdTile;
	/** coding/decoding parameters common to all tiles */
	private Tcp tcp;
	/** current encoded/decoded tile */
	private int tcdTileNo;
	/** Time taken to encode a tile */
	private double encodingTime;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Tcd tcd = (Tcd) o;
		return tilePartPosition == tcd.tilePartPosition && tilePartNo == tcd.tilePartNo
				&& curTilePartNo == tcd.curTilePartNo && curTotalNoOfTileParts == tcd.curTotalNoOfTileParts
				&& curPiNo == tcd.curPiNo && tcdTileNo == tcd.tcdTileNo
				&& Double.compare(tcd.encodingTime, encodingTime) == 0
				&& Objects.equals(codecContextInfo, tcd.codecContextInfo) && Objects.equals(tcdImage, tcd.tcdImage)
				&& Objects.equals(image, tcd.image) && Objects.equals(codingParameters, tcd.codingParameters)
				&& Objects.equals(tcdTile, tcd.tcdTile) && Objects.equals(tcp, tcd.tcp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tilePartPosition, tilePartNo, curTilePartNo, curTotalNoOfTileParts, curPiNo,
				codecContextInfo, tcdImage, image, codingParameters, tcdTile, tcp, tcdTileNo, encodingTime);
	}

	public boolean canEqual(Object other) {
		return other instanceof Tcd;
	}
}