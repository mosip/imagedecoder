package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile
*/
public class TcdTile {
	private int x0, y0, x1, y1;		/* dimension of the tile : left upper corner (x0, y0) right low corner (x1,y1) */
	private int noOfComps;			/* number of components in tile */
	private TcdTileComponent[] comps;	/* Components information */
	private int noOfPixels;			/* add fixed_quality */
	private double distortionTile;		/* add fixed_quality */
	private double[] distortionLayer = new double[100];	/* add fixed_quality */
	/** packet number */
	private int packetNo;
}