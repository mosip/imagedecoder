package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile layers
*/
public class TcdLayer {
	private int noOfPasses;		/* Number of passes in the layer */
	private int length;			/* len of information */
	private double distortion;			/* add for index */
	private byte[] data;		/* data */
}