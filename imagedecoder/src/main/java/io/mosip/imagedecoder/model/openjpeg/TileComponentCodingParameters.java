package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import lombok.Data;
import lombok.ToString;

/**
 * Tile-component coding parameters
 */
@Data
@ToString
public class TileComponentCodingParameters {
	/** coding style */
	private int codingStyle;
	/** number of resolutions */
	private int noOfResolutions;
	/** code-blocks width */
	private int codeBlockWidth;
	/** code-blocks height */
	private int codeBlockHeight;
	/** code-block coding style */
	private int codeBlockStyle;
	/** discrete wavelet transform identifier [Lossless = 1 and Lossy = 0] */
	private int qmfbid;
	/** quantisation style */
	private int quantisationStyle;
	/** stepsizes used for quantization */
	private StepSize[] stepsizes = new StepSize[OpenJpegConstant.J2K_MAXBANDS];
	/** number of guard bits */
	private int noOfGaurdBits;
	/** Region Of Interest shift */
	private int roiShift;
	/** precinct width */
	private int[] precinctWidth = new int[OpenJpegConstant.J2K_MAXRLVLS];
	/** precinct height */
	private int[] precinctHeight = new int[OpenJpegConstant.J2K_MAXRLVLS];

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TileComponentCodingParameters that = (TileComponentCodingParameters) o;
		return codingStyle == that.codingStyle && noOfResolutions == that.noOfResolutions
				&& codeBlockWidth == that.codeBlockWidth && codeBlockHeight == that.codeBlockHeight
				&& codeBlockStyle == that.codeBlockStyle && qmfbid == that.qmfbid
				&& quantisationStyle == that.quantisationStyle && noOfGaurdBits == that.noOfGaurdBits
				&& roiShift == that.roiShift && Arrays.equals(stepsizes, that.stepsizes)
				&& Arrays.equals(precinctWidth, that.precinctWidth)
				&& Arrays.equals(precinctHeight, that.precinctHeight);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(codingStyle, noOfResolutions, codeBlockWidth, codeBlockHeight, codeBlockStyle, qmfbid,
				quantisationStyle, noOfGaurdBits, roiShift);
		result = 31 * result + Arrays.hashCode(stepsizes);
		result = 31 * result + Arrays.hashCode(precinctWidth);
		result = 31 * result + Arrays.hashCode(precinctHeight);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TileComponentCodingParameters;
	}
}