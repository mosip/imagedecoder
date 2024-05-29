package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile block encode
*/
public class TcdCodeBlockEncoder {
	private int dataIndex;	/* Data Index*/
	private byte [] data;	/* Data */
	private TcdLayer[] layers;	/* layer information */
	private TcdPass[] passes;	/* information about the passes */
	private int x0;
	private int y0;
	private int x1;
	private int y1;		/* dimension of the code-blocks : left upper corner (x0, y0) right low corner (x1,y1) */
	private int noOfBps;
	private int noOfLengthBits;
	private int noOfPasses;		/* number of pass already done for the code-blocks */
	private int noOfPassesInLayers;	/* number of passes in the layer */
	private int totalPasses;		/* total number of passes */
}