package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tile coding parameters : this structure is used to store coding/decoding
 * parameters common to all tiles (information like COD, COC in main header)
 */
@Data
@ToString
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Tcp tcp = (Tcp) o;
		return first == tcp.first && codingStyle == tcp.codingStyle && noOfLayers == tcp.noOfLayers && mct == tcp.mct
				&& noOfPocs == tcp.noOfPocs && isPoc == tcp.isPoc && pptDataIndex == tcp.pptDataIndex && ppt == tcp.ppt
				&& pptStore == tcp.pptStore && pptLength == tcp.pptLength && Arrays.equals(rates, tcp.rates)
				&& Arrays.equals(pocs, tcp.pocs) && Arrays.equals(pptData, tcp.pptData)
				&& Arrays.equals(pptDataFirst, tcp.pptDataFirst) && Arrays.equals(distortionRatio, tcp.distortionRatio)
				&& Arrays.equals(tccps, tcp.tccps) && Objects.equals(progressionOrder, tcp.progressionOrder);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(first, codingStyle, progressionOrder, noOfLayers, mct, noOfPocs, isPoc, pptDataIndex,
				ppt, pptStore, pptLength);
		result = 31 * result + Arrays.hashCode(rates);
		result = 31 * result + Arrays.hashCode(pocs);
		result = 31 * result + Arrays.hashCode(pptData);
		result = 31 * result + Arrays.hashCode(pptDataFirst);
		result = 31 * result + Arrays.hashCode(distortionRatio);
		result = 31 * result + Arrays.hashCode(tccps);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof Tcp;
	}
}