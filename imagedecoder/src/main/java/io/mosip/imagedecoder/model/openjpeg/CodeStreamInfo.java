package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * CodeStreaminfo 
 */
public class CodeStreamInfo {
	/** maximum distortion reduction on the whole image */
	private double distortionMax;
	/** packet number */
	private int packetNo;
	/** writing the packet in the index with t2EncodePackets */
	private int indexWrite;
	/** image width */
	private int imageWidth;
	/** image height */
	private int imageHeight;
	/** progression order */
	private ProgressionOrder progOrder;
	/** tile size in x */
	private int tileX;
	/** tile size in y */
	private int tileY;
	/** */
	private int tileOX;
	/** */
	private int tileOY;
	/** number of tiles in X */
	private int tileWidth;
	/** number of tiles in Y */
	private int tileHeight;
	/** component numbers */
	private int noOfComps;
	/** number of layer */
	private int noOfLayers;
	/** number of decomposition for each component */
	private int[] noOfDecompositionComps;
	/* UniPG>> */
	/** number of markers */
	private int markerNo;
	/** list of markers */
	private MarkerInfo[] markers;
	/** actual size of markers array */
	private int maxMarkerNo;
	/* <<UniPG */
	/** main header position */
	private int mainHeadStart;
	/** main header position */
	private int mainHeadEnd;
	/** codestream's size */
	private int codeStreamSize;
	/** information regarding tiles inside image */
	private TileInfo[] tileInfo;
}
