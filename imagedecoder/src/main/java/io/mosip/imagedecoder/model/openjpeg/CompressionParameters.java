package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import lombok.Data;
import lombok.ToString;

/**
 * Compression parameters
 */
@Data
@ToString
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CompressionParameters))
			return false;
		CompressionParameters that = (CompressionParameters) o;
		return tileSizeOn == that.tileSizeOn && cpTileX0 == that.cpTileX0 && cpTileY0 == that.cpTileY0
				&& cpTileDX == that.cpTileDX && cpTileDY == that.cpTileDY
				&& cpDistortionAllocation == that.cpDistortionAllocation && cpFixedAllocation == that.cpFixedAllocation
				&& cpFixedQuality == that.cpFixedQuality && codingStyle == that.codingStyle && noOfPocs == that.noOfPocs
				&& tcpNoOfLayers == that.tcpNoOfLayers && noOfResolution == that.noOfResolution
				&& codeBlockWidthInit == that.codeBlockWidthInit && codeBlockHeightInit == that.codeBlockHeightInit
				&& mode == that.mode && irreversible == that.irreversible && roiCompNo == that.roiCompNo
				&& roiShift == that.roiShift && resSpec == that.resSpec && indexOn == that.indexOn
				&& imageOffsetX0 == that.imageOffsetX0 && imageOffsetY0 == that.imageOffsetY0
				&& subSamplingDX == that.subSamplingDX && subSamplingDY == that.subSamplingDY
				&& decodeFormat == that.decodeFormat && codecFormat == that.codecFormat && jpwlEpcOn == that.jpwlEpcOn
				&& jpwlHprotMH == that.jpwlHprotMH && jpwlSensSize == that.jpwlSensSize
				&& jpwlSensAddr == that.jpwlSensAddr && jpwlSensRange == that.jpwlSensRange
				&& jpwlSensMH == that.jpwlSensMH && maxCompSize == that.maxCompSize && tpOn == that.tpOn
				&& tpFlag == that.tpFlag && tcpMct == that.tcpMct && Arrays.equals(cpMatrice, that.cpMatrice)
				&& Arrays.equals(cpComment, that.cpComment) && Arrays.equals(tcpRates, that.tcpRates)
				&& Arrays.equals(tcpDistortionRatio, that.tcpDistortionRatio)
				&& Arrays.equals(precinctWidthInit, that.precinctWidthInit)
				&& Arrays.equals(precinctHeightInit, that.precinctHeightInit) && Arrays.equals(infile, that.infile)
				&& Arrays.equals(outfile, that.outfile) && Arrays.equals(index, that.index)
				&& Arrays.equals(jpwlHprotTPHTileNo, that.jpwlHprotTPHTileNo)
				&& Arrays.equals(jpwlHprotTPH, that.jpwlHprotTPH)
				&& Arrays.equals(jpwlPprotTileNo, that.jpwlPprotTileNo)
				&& Arrays.equals(jpwlPprotPacketNo, that.jpwlPprotPacketNo) && Arrays.equals(jpwlPprot, that.jpwlPprot)
				&& Arrays.equals(jpwlSensTPHTileNo, that.jpwlSensTPHTileNo)
				&& Arrays.equals(jpwlSensTPH, that.jpwlSensTPH) && cpCinemaMode == that.cpCinemaMode
				&& cpRsizCap == that.cpRsizCap && Arrays.equals(pocs, that.pocs);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(tileSizeOn, cpTileX0, cpTileY0, cpTileDX, cpTileDY, cpDistortionAllocation,
				cpFixedAllocation, cpFixedQuality, codingStyle, noOfPocs, tcpNoOfLayers, noOfResolution,
				codeBlockWidthInit, codeBlockHeightInit, mode, irreversible, roiCompNo, roiShift, resSpec, indexOn,
				imageOffsetX0, imageOffsetY0, subSamplingDX, subSamplingDY, decodeFormat, codecFormat, jpwlEpcOn,
				jpwlHprotMH, jpwlSensSize, jpwlSensAddr, jpwlSensRange, jpwlSensMH, maxCompSize, tpOn, tpFlag, tcpMct,
				cpCinemaMode, cpRsizCap);
		result = 31 * result + Arrays.hashCode(cpMatrice);
		result = 31 * result + Arrays.hashCode(cpComment);
		result = 31 * result + Arrays.hashCode(tcpRates);
		result = 31 * result + Arrays.hashCode(tcpDistortionRatio);
		result = 31 * result + Arrays.hashCode(precinctWidthInit);
		result = 31 * result + Arrays.hashCode(precinctHeightInit);
		result = 31 * result + Arrays.hashCode(infile);
		result = 31 * result + Arrays.hashCode(outfile);
		result = 31 * result + Arrays.hashCode(index);
		result = 31 * result + Arrays.hashCode(jpwlHprotTPHTileNo);
		result = 31 * result + Arrays.hashCode(jpwlHprotTPH);
		result = 31 * result + Arrays.hashCode(jpwlPprotTileNo);
		result = 31 * result + Arrays.hashCode(jpwlPprotPacketNo);
		result = 31 * result + Arrays.hashCode(jpwlPprot);
		result = 31 * result + Arrays.hashCode(jpwlSensTPHTileNo);
		result = 31 * result + Arrays.hashCode(jpwlSensTPH);
		result = 31 * result + Arrays.hashCode(pocs);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof CompressionParameters;
	}
}