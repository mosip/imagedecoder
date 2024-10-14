package io.mosip.imagedecoder.model.openjpeg;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import lombok.Data;
import lombok.ToString;

/**
 * Decompression parameters
 */
@Data
@ToString
public class DecompressionParameters {
	/**
	 * Set the number of highest resolution levels to be discarded. The image
	 * resolution is effectively divided by 2 to the power of the number of
	 * discarded levels. The reduce factor is limited by the smallest total number
	 * of decomposition levels among tiles. if != 0, then original dimension divided
	 * by 2^(reduce); if == 0 or not used, image is decoded to the full resolution
	 */
	private int cpReduce;
	/**
	 * Set the maximum number of quality layers to decode. If there are less quality
	 * layers than the specified number, all the quality layers are decoded. if !=
	 * 0, then only the first "layer" layers are decoded; if == 0 or not used, all
	 * the quality layers are decoded
	 */
	private int cpLayer;

	/** @name command line encoder parameters (not used inside the library) */
	/** input file name */
	private char[] infile = new char[OpenJpegConstant.MAX_PATH_LEN];
	/** output file name */
	private char[] outfile = new char[OpenJpegConstant.MAX_PATH_LEN];
	/** input file format 0: J2K, 1: JP2, 2: JPT */
	private int decodeFormat;
	/** output file format 0: PGX, 1: PxM, 2: BMP */
	private int codecFormat;

	/** @name JPWL decoding parameters */
	/** activates the JPWL correction capabilities */
	private int jpwlCorrect;
	/** expected number of components */
	private int jpwlExpComps;
	/** maximum number of tiles */
	private int jpwlMaxTiles;

	/**
	 * Specify whether the decoding should be done on the entire codestream, or be
	 * limited to the main header Limiting the decoding to the main header makes it
	 * possible to extract the characteristics of the codestream if ==
	 * NO_LIMITATION, the entire codestream is decoded; if == LIMIT_TO_MAIN_HEADER,
	 * only the main header is decoded;
	 */
	private LimitDecoding cpLimitDecoding;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DecompressionParameters))
			return false;
		DecompressionParameters that = (DecompressionParameters) obj;
		return cpReduce == that.cpReduce && cpLayer == that.cpLayer && decodeFormat == that.decodeFormat
				&& codecFormat == that.codecFormat && jpwlCorrect == that.jpwlCorrect
				&& jpwlExpComps == that.jpwlExpComps && jpwlMaxTiles == that.jpwlMaxTiles && canEqual(that)
				&& java.util.Arrays.equals(infile, that.infile) && java.util.Arrays.equals(outfile, that.outfile)
				&& cpLimitDecoding == that.cpLimitDecoding;
	}

	@Override
	public int hashCode() {
		int result = cpReduce;
		result = 31 * result + cpLayer;
		result = 31 * result + java.util.Arrays.hashCode(infile);
		result = 31 * result + java.util.Arrays.hashCode(outfile);
		result = 31 * result + decodeFormat;
		result = 31 * result + codecFormat;
		result = 31 * result + jpwlCorrect;
		result = 31 * result + jpwlExpComps;
		result = 31 * result + jpwlMaxTiles;
		result = 31 * result + (cpLimitDecoding != null ? cpLimitDecoding.hashCode() : 0);
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof DecompressionParameters;
	}
}