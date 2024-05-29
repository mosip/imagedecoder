package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile resolution
*/
public class TcdResolution {
	private int x0;
	private int y0;
	private int x1;
	private int y1;		/* dimension of the resolution level : left upper corner (x0, y0) right low corner (x1,y1) */
	private int pWidth;
	private int pHeight;
	private int noOfBands;			/* number sub-band for the resolution level */
	private TcdBand[] bands = new TcdBand[3];		/* subband information */
}