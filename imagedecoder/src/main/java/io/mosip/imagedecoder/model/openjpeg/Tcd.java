package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile coder/decoder
*/
public class Tcd {
	/** Position of the tilepart flag in Progression order*/
	private int tilePartPosition;
	/** Tile part number*/
	private int tilePartNo;
	/** Current tile part number*/
	private int curTilePartNo;
	/** Total number of tileparts of the current tile*/
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
	int tcdTileNo;
	/** Time taken to encode a tile*/
	double encodingTime;
}