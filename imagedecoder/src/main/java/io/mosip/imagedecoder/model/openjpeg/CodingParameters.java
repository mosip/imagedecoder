package io.mosip.imagedecoder.model.openjpeg;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Coding parameters
 */
public class CodingParameters {
	/** Digital cinema profile */
	private JP2CinemeaMode cinemaMode;
	/**
	 * Maximum rate for each component. If == 0, component size limitation is not
	 * considered
	 */
	private int maxCompSize;
	/** Size of the image in bits */
	private int imageSize;
	/** Rsiz */
	private RsizCapabilities rsizCap;
	/** Enabling Tile part generation */
	private int tilePartOn;
	/** Flag determining tile part generation */
	private int tilePartFlag;
	/** Position of tile part flag in progression order */
	private int tilePartPosition;
	/** allocation by rate/distortion */
	private int distortionAllocation;
	/** allocation by fixed layer */
	private int fixedAllocation;
	/** add fixed_quality */
	private int fixedQuality;
	/**
	 * if != 0, then original dimension divided by 2^(reduce); if == 0 or not used,
	 * image is decoded to the full resolution
	 */
	private int reduce;
	/**
	 * if != 0, then only the first "layer" layers are decoded; if == 0 or not used,
	 * all the quality layers are decoded
	 */
	private int layer;
	/**
	 * if == NO_LIMITATION, decode entire codestream; if == LIMIT_TO_MAIN_HEADER
	 * then only decode the main header
	 */
	private LimitDecoding limitDecoding;
	/** XTOsiz */
	private int tileX0;
	/** YTOsiz */
	private int tileY0;
	/** XTsiz */
	private int tileDX;
	/** YTsiz */
	private int tileDY;
	/** comment for coding */
	private char[] comment;
	/** number of tiles in width */
	private int tileWidth;
	/** number of tiles in heigth */
	private int tileHeight;
	/** ID number of the tiles present in the codestream */
	private int[] tileNo;
	/** size of the vector tileno */
	private int tileNoSize;
	/** packet header store there for futur use in t2_decode_packet */
	private int ppmDataIndex;
	private byte[] ppmData;
	/** pointer remaining on the first byte of the first header if ppm is used */
	private byte[] ppmDataFirst;
	/** if ppm == 1 --> there was a PPM marker for the present tile */
	private int ppm;
	/** use in case of multiple marker PPM (number of info already store) */
	private int ppmStore;
	/** use in case of multiple marker PPM (case on non-finished previous info) */
	private int ppmPrevious;
	/** ppmbug1 */
	private int ppmLength;
	/** tile coding parameters */
	private Tcp[] tcps;
	/** fixed layer */
	private int[] matrice;

	// USE_JPWL START
	/** enables writing of EPC in MH, thus activating JPWL */
	private int epcOn;
	/** enables writing of EPB, in case of activated JPWL */
	private int epbOn;
	/** enables writing of ESD, in case of activated JPWL */
	private int esdOn;
	/**
	 * enables writing of informative techniques of ESD, in case of activated JPWL
	 */
	private int infoOn;
	/** enables writing of RED, in case of activated JPWL */
	private int redOn;
	/** error protection method for MH (0,1,16,32,37-128) */
	private int hprotMH;
	/** tile number of header protection specification (>=0) */
	private int[] hprotTPHTileNo = new int[OpenJpegConstant.JPWL_MAX_NO_TILESPECS];
	/** error protection methods for TPHs (0,1,16,32,37-128) */
	private int[] hprotTPH = new int[OpenJpegConstant.JPWL_MAX_NO_TILESPECS];
	/** tile number of packet protection specification (>=0) */
	private int[] pprotTileNo = new int[OpenJpegConstant.JPWL_MAX_NO_PACKSPECS];
	/** packet number of packet protection specification (>=0) */
	private int[] pprotPacketNo = new int[OpenJpegConstant.JPWL_MAX_NO_PACKSPECS];
	/** error protection methods for packets (0,1,16,32,37-128) */
	private int[] pprot = new int[OpenJpegConstant.JPWL_MAX_NO_PACKSPECS];
	/** enables writing of ESD, (0/2/4 bytes) */
	private int sensSize;
	/** sensitivity addressing size (0=auto/2/4 bytes) */
	private int sensAddr;
	/** sensitivity range (0-3) */
	private int sensRange;
	/** sensitivity method for MH (-1,0-7) */
	private int sensMH;
	/** tile number of sensitivity specification (>=0) */
	private int[] sensTPHTileNo = new int[OpenJpegConstant.JPWL_MAX_NO_TILESPECS];
	/** sensitivity methods for TPHs (-1,0-7) */
	private int[] sensTPH = new int[OpenJpegConstant.JPWL_MAX_NO_TILESPECS];
	/** enables JPWL correction at the decoder */
	private int correct;
	/** expected number of components at the decoder */
	private int expComps;
	/** maximum number of tiles at the decoder */
	private int maxTiles;
	// USE_JPWL END
}