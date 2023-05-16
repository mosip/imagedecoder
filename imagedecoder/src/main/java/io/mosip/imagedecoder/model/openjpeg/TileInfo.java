package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Index structure : information regarding tiles
 */
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
}
