package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Defines a single image component
 */
@Data
@ToString
public class OpenJpegImageComponent {
	/**
	 * XRsiz: horizontal separation of a sample of ith component with respect to the
	 * reference grid
	 */
	private int dX;
	/**
	 * YRsiz: vertical separation of a sample of ith component with respect to the
	 * reference grid
	 */
	private int dY;
	/** data width */
	private int width;
	/** data height */
	private int height;
	/** x component offset compared to the whole image */
	private int x0;
	/** y component offset compared to the whole image */
	private int y0;
	/** precision */
	private int prec;
	/** image depth in bits */
	private int bpp;
	/** signed (1) / unsigned (0) */
	private int sgnd;
	/** number of decoded resolution */
	private int resNoDecoded;
	/**
	 * number of division by 2 of the out image compared to the original size of
	 * image
	 */
	private int factor;
	/** image component data */
	private int[] data;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof OpenJpegImageComponent))
			return false;
		OpenJpegImageComponent that = (OpenJpegImageComponent) obj;
		return canEqual(that) && dX == that.dX && dY == that.dY && width == that.width && height == that.height
				&& x0 == that.x0 && y0 == that.y0 && prec == that.prec && bpp == that.bpp && sgnd == that.sgnd
				&& resNoDecoded == that.resNoDecoded && factor == that.factor && Arrays.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dX, dY, width, height, x0, y0, prec, bpp, sgnd, resNoDecoded, factor)
				+ Arrays.hashCode(data);
	}

	public boolean canEqual(Object obj) {
		return obj instanceof OpenJpegImageComponent;
	}
}