package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile coding parameters : 
this structure is used to store coding/decoding parameters common to all
tiles (information like COD, COC in main header)
*/
public class Tcp {
	/** 1 : first part-tile of a tile */
	private int first;
	/** coding style */
	int codingStyle;
	/** progression order */
	private ProgressionOrder progressionOrder;
	/** number of layers */
	private int noOfLayers;
	/** multi-component transform identifier */
	private int mct;
	/** rates of layers */
	private float[] rates = new float[100];
	/** number of progression order changes */
	int noOfPocs;
	/** indicates if a POC marker has been used O:NO, 1:YES */
	private int isPoc;
	/** progression order changes */
	private Poc[] pocs = new Poc[32];
	private int pptDataIndex;
	/** packet header store there for futur use in t2_decode_packet */
	private byte[] pptData;
	/** pointer remaining on the first byte of the first header if ppt is used */
	private byte[] pptDataFirst;
	/** If ppt == 1 --> there was a PPT marker for the present tile */
	private int ppt;
	/** used in case of multiple marker PPT (number of info already stored) */
	private int pptStore;
	/** ppmbug1 */
	private int pptLength;
	/** add fixed_quality */
	private float[] distortionRatio = new float[100];
	/** tile-component coding parameters */
	private TileComponentCodingParameters[] tccps;
}