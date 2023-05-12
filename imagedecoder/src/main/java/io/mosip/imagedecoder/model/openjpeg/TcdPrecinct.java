package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile precinct
*/
public class TcdPrecinct {
	private int x0, y0, x1, y1;		/* dimension of the precinct : left upper corner (x0, y0) right low corner (x1,y1) */
	private int cWidth, cHeight;			/* number of precinct in width and heigth */
	/* code-blocks informations */
	private TcdCodeBlockEncoder[] tcdCodeBlockEncoder;
	private TcdCodeBlockDecoder[] tcdCodeBlockDecoder;
	private TgtTree inclTree;		/* inclusion tree */
	private TgtTree imsbTree;		/* IMSB tree */
}