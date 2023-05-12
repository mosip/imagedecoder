package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Index structure : Information concerning a packet inside tile
 */
public class PacketInfo {
	/** packet start position (including SOP marker if it exists) */
	private int startPosition;
	/** end of packet header position (including EPH marker if it exists) */
	private int endPHPosition;
	/** packet end position */
	private int endPosition;
	/** packet distortion*/
	private double distortion;
}
