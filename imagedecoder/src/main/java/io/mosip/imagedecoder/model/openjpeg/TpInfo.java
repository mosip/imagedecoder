package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Index structure : Information concerning tile-parts
 */
@Data
@ToString
public class TpInfo {
	/** start position of tile part */
	private int tpStartPosition;
	/** end position of tile part header */
	private int tpEndHeader;
	/** end position of tile part */
	private int tpEndPosition;
	/** start packet of tile part */
	private int tpStartPacket;
	/** number of packets of tile part */
	private int tpNoOfPackets;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TpInfo tpInfo = (TpInfo) o;
		return tpStartPosition == tpInfo.tpStartPosition && tpEndHeader == tpInfo.tpEndHeader
				&& tpEndPosition == tpInfo.tpEndPosition && tpStartPacket == tpInfo.tpStartPacket
				&& tpNoOfPackets == tpInfo.tpNoOfPackets;
	}

	@Override
	public int hashCode() {
		return Objects.hash(tpStartPosition, tpEndHeader, tpEndPosition, tpStartPacket, tpNoOfPackets);
	}

	public boolean canEqual(Object other) {
		return other instanceof TpInfo;
	}
}