package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Defines image data and characteristics
 */
@Data
@ToString
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof OpenJpegImage))
			return false;
		OpenJpegImage that = (OpenJpegImage) obj;
		return canEqual(that) && x0 == that.x0 && y0 == that.y0 && x1 == that.x1 && y1 == that.y1
				&& noOfComps == that.noOfComps && qmfbid == that.qmfbid && Objects.equals(colorSpace, that.colorSpace)
				&& Arrays.equals(comps, that.comps) && Objects.equals(resolutionBox, that.resolutionBox);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x0, y0, x1, y1, noOfComps, qmfbid, colorSpace, resolutionBox) + Arrays.hashCode(comps);
	}

	public boolean canEqual(Object obj) {
		return obj instanceof OpenJpegImage;
	}
}