package io.mosip.imagedecoder.model.openjpeg;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Compression parameters
 */
public class CompressionParameters {
	/**
	 * size of tile: tile_size_on = false (not in argument) or = true (in argument)
	 */
	private int tileSizeOn;
	/** XTOsiz */
	private int cpTileX0;
	/** YTOsiz */
	private int cpTileY0;
	/** XTsiz */
	private int cpTileDX;
	/** YTsiz */
	private int cpTileDY;
	/** allocation by rate/distortion */
	private int cpDistortionAllocation;
	/** allocation by fixed layer */
	private int cpFixedAllocation;
	/** add fixed_quality */
	private int cpFixedQuality;
	/** fixed layer */
	private int[] cpMatrice;
	/** comment for coding */
	private char[] cpComment;
	/** csty : codprivate int style */
	private int codingStyle;
	/** progression order (default LRCP) */
	private ProgressionOrder progressionOrder;
	/** progression order changes */
	private Poc[] pocs = new Poc[32];
	/** number of progression order changes (POC), default to 0 */
	private int noOfPocs;
	/** number of layers */
	private int tcpNoOfLayers;
	/** rates of layers */
	private float[] tcpRates = new float[100];
	/** different psnr for successive layers */
	private float[] tcpDistortionRatio = new float[100];
	/** number of resolutions */
	private int noOfResolution;
	/** initial code block width, default to 64 */
	private int codeBlockWidthInit;
	/** initial code block height, default to 64 */
	private int codeBlockHeightInit;
	/** mode switch (cblk_style) */
	private int mode;
	/** 1 : use the irreversible DWT 9-7, 0 : use lossless compression (default) */
	private int irreversible;
	/** region of interest: affected component in [0..3], -1 means no ROI */
	private int roiCompNo;
	/** region of interest: upshift value */
	private int roiShift;
	/* number of precinct size specifications */
	private int resSpec;
	/** initial precinct width */
	private int[] precinctWidthInit = new int[OpenJpegConstant.J2K_MAXRLVLS];
	/** initial precinct height */
	private int[] precinctHeightInit = new int[OpenJpegConstant.J2K_MAXRLVLS];

	/** input file name */
	private char[] infile = new char[OpenJpegConstant.MAX_PATH_LEN];
	/** output file name */
	private char[] outfile = new char[OpenJpegConstant.MAX_PATH_LEN];
	/**
	 * DEPRECATED. Index generation is now handeld with the encodeWithInfo()
	 * function. Set to NULL
	 */
	int indexOn;
	/**
	 * DEPRECATED. Index generation is now handeld with the encodeWithInfo()
	 * function. Set to NULL
	 */
	private char[] index = new char[OpenJpegConstant.MAX_PATH_LEN];
	/** subimage encoding: origin image offset in x direction */
	private int imageOffsetX0;
	/** subimage encoding: origin image offset in y direction */
	private int imageOffsetY0;
	/** subsampling value for dx */
	private int subSamplingDX;
	/** subsampling value for dy */
	private int subSamplingDY;
	/** input file format 0: PGX, 1: PxM, 2: BMP 3:TIF */
	private int decodeFormat;
	/** output file format 0: J2K, 1: JP2, 2: JPT */
	private int codecFormat;

	/* UniPG>> */
	/** @name JPWL encoding parameters */
	/** enables writing of EPC in MH, thus activating JPWL */
	private int jpwlEpcOn;
	/** error protection method for MH (0,1,16,32,37-128) */
	private int jpwlHprotMH;
	/** tile number of header protection specification (>=0) */
	private int[] jpwlHprotTPHTileNo = new int[OpenJpegConstant.JPWL_MAX_NO_TILESPECS];
	/** error protection methods for TPHs (0,1,16,32,37-128) */
	private int[] jpwlHprotTPH = new int[OpenJpegConstant.JPWL_MAX_NO_TILESPECS];
	/** tile number of packet protection specification (>=0) */
	private int[] jpwlPprotTileNo = new int[OpenJpegConstant.JPWL_MAX_NO_PACKSPECS];
	/** packet number of packet protection specification (>=0) */
	private int[] jpwlPprotPacketNo = new int[OpenJpegConstant.JPWL_MAX_NO_PACKSPECS];
	/** error protection methods for packets (0,1,16,32,37-128) */
	private int[] jpwlPprot = new int[OpenJpegConstant.JPWL_MAX_NO_PACKSPECS];
	/** enables writing of ESD, (0=no/1/2 bytes) */
	private int jpwlSensSize;
	/** sensitivity addressing size (0=auto/2/4 bytes) */
	private int jpwlSensAddr;
	/** sensitivity range (0-3) */
	private int jpwlSensRange;
	/** sensitivity method for MH (-1=no,0-7) */
	private int jpwlSensMH;
	/** tile number of sensitivity specification (>=0) */
	private int[] jpwlSensTPHTileNo = new int[OpenJpegConstant.JPWL_MAX_NO_TILESPECS];
	/** sensitivity methods for TPHs (-1=no,0-7) */
	private int[] jpwlSensTPH = new int[OpenJpegConstant.JPWL_MAX_NO_TILESPECS];
	/* <<UniPG */

	/** Digital Cinema compliance 0-not compliant, 1-compliant */
	private JP2CinemeaMode cpCinemaMode;
	/**
	 * Maximum rate for each component. If == 0, component size limitation is not
	 * considered
	 */
	private int maxCompSize;
	/** Profile name */
	private RsizCapabilities cpRsizCap;
	/** Tile part generation */
	private int tpOn;
	/** Flag for Tile part generation */
	private int tpFlag;
	/** MCT (multiple component transform) */
	private int tcpMct;
}