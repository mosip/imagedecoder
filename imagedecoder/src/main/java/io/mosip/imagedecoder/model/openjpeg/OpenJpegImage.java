package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Defines image data and characteristics
 */
public class OpenJpegImage {
	/**
	 * XOsiz: horizontal offset from the origin of the reference grid to the left
	 * side of the image area
	 */
	private int x0;
	/**
	 * YOsiz: vertical offset from the origin of the reference grid to the top side
	 * of the image area
	 */
	private int y0;
	/** Xsiz: width of the reference grid */
	private int x1;
	/** Ysiz: height of the reference grid */
	private int y1;
	/** number of components in the image */
	int noOfComps;
	/** discrete wavelet transform identifier */
	private int qmfbid;
	/** color space: sRGB, Greyscale or YUV */
	private Jp2ColorSpace colorSpace;
	/** image components */
	private OpenJpegImageComponent[] comps;
	private JP2ResolutionBox resolutionBox = null;
}
