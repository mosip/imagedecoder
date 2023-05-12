package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile comp
*/
public class TcdTileComponent {
	private int x0, y0, x1, y1;		/* dimension of component : left upper corner (x0, y0) right low corner (x1,y1) */
	private int noOfResolutions;		/* number of resolutions level */
	private TcdResolution[] resolutions;	/* resolutions information */
	private int[] iData;			/* data of the component */
	private double[] fData;			/* data of the component */
	private int noOfPixels;			/* add fixed_quality */
}