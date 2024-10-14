package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Component parameters structure used by the imageCreate function
 */
@Data
@ToString
public class OpenJpegImageComponentParameters {
	/**
	 * XRsiz: horizontal separation of a sample of ith component with respect to the
	 * reference grid
	 */
	private int dx;
	/**
	 * YRsiz: vertical separation of a sample of ith component with respect to the
	 * reference grid
	 */
	private int dy;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof OpenJpegImageComponentParameters))
			return false;
		OpenJpegImageComponentParameters that = (OpenJpegImageComponentParameters) obj;
		return canEqual(that) && dx == that.dx && dy == that.dy && width == that.width && height == that.height
				&& x0 == that.x0 && y0 == that.y0 && prec == that.prec && bpp == that.bpp && sgnd == that.sgnd;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dx, dy, width, height, x0, y0, prec, bpp, sgnd);
	}

	public boolean canEqual(Object obj) {
		return obj instanceof OpenJpegImageComponentParameters;
	}
}