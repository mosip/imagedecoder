package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Index structure : Information concerning tile-parts
 */
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
}
