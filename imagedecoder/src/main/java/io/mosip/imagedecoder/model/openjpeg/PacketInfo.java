package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Index structure : Information concerning a packet inside tile
 */
@Data
@ToString
public class PacketInfo {
	/** packet start position (including SOP marker if it exists) */
	private int startPosition;
	/** end of packet header position (including EPH marker if it exists) */
	private int endPHPosition;
	/** packet end position */
	private int endPosition;
	/** packet distortion */
	private double distortion;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PacketInfo))
			return false;
		PacketInfo that = (PacketInfo) obj;
		return canEqual(that) && startPosition == that.startPosition && endPHPosition == that.endPHPosition
				&& endPosition == that.endPosition && Double.compare(that.distortion, distortion) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(startPosition, endPHPosition, endPosition, distortion);
	}

	public boolean canEqual(Object obj) {
		return obj instanceof PacketInfo;
	}
}