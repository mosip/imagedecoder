package io.mosip.imagedecoder.model.openjpeg;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile-component coding parameters
*/
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
	/** discrete wavelet transform identifier [Lossless = 1 and Lossy = 0]*/
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
}