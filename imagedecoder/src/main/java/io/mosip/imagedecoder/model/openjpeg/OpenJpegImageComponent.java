package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Defines a single image component
 */
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
}
