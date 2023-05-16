package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Component parameters structure used by the imageCreate function
 */
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
}
