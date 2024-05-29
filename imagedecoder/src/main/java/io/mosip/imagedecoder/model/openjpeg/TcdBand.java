package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile band
*/
public class TcdBand {
	private int x0;	/* dimension of the subband : left upper corner (x0, y0) right low corner (x1,y1) */
	private int y0;	/* dimension of the subband : left upper corner (x0, y0) right low corner (x1,y1) */
	private int x1;	/* dimension of the subband : left upper corner (x0, y0) right low corner (x1,y1) */
	private int y1;	/* dimension of the subband : left upper corner (x0, y0) right low corner (x1,y1) */
	private int bandNo;
	private TcdPrecinct[] precincts;	/* precinct information */
	private int noOfBps;
	private float stepSize;
}