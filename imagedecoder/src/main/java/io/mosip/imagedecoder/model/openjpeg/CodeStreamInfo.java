package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * CodeStreaminfo
 */
@Data
@ToString
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CodeStreamInfo))
			return false;
		CodeStreamInfo that = (CodeStreamInfo) obj;
		return Double.compare(that.distortionMax, distortionMax) == 0 && packetNo == that.packetNo
				&& indexWrite == that.indexWrite && imageWidth == that.imageWidth && imageHeight == that.imageHeight
				&& tileX == that.tileX && tileY == that.tileY && tileOX == that.tileOX && tileOY == that.tileOY
				&& tileWidth == that.tileWidth && tileHeight == that.tileHeight && noOfComps == that.noOfComps
				&& noOfLayers == that.noOfLayers && markerNo == that.markerNo && maxMarkerNo == that.maxMarkerNo
				&& mainHeadStart == that.mainHeadStart && mainHeadEnd == that.mainHeadEnd
				&& codeStreamSize == that.codeStreamSize
				&& Arrays.equals(noOfDecompositionComps, that.noOfDecompositionComps)
				&& Arrays.equals(markers, that.markers) && Arrays.equals(tileInfo, that.tileInfo);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(distortionMax, packetNo, indexWrite, imageWidth, imageHeight, tileX, tileY, tileOX,
				tileOY, tileWidth, tileHeight, noOfComps, noOfLayers, markerNo, maxMarkerNo, mainHeadStart, mainHeadEnd,
				codeStreamSize);
		result = 31 * result + Arrays.hashCode(noOfDecompositionComps);
		result = 31 * result + Arrays.hashCode(markers);
		result = 31 * result + Arrays.hashCode(tileInfo);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof CodeStreamInfo;
	}
}