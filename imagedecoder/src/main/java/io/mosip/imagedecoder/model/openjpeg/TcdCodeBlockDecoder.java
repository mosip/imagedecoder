package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile block decode
*/
public class TcdCodeBlockDecoder {
	private byte[] data;	/* Data */
	private TcdSegment[] segs;		/* segments informations */
	private int x0, y0, x1, y1;		/* dimension of the code-blocks : left upper corner (x0, y0) right low corner (x1,y1) */
	private int noOfBps;
	private int noOfLengthBits;
	private int length;			/* length */
	private int noOfNewPasses;		/* number of pass added to the code-blocks */
	private int noOfSegs;			/* number of segments */
}