package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Marker structure
 */
@Data
@ToString
public class MarkerInfo {
	/** marker type */
	private int type;
	/** position in codestream */
	private int position;
	/** length, marker val included */
	private int length;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MarkerInfo))
			return false;
		MarkerInfo that = (MarkerInfo) obj;
		return canEqual(that) && type == that.type && position == that.position && length == that.length;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, position, length);
	}

	public boolean canEqual(Object obj) {
		return obj instanceof MarkerInfo;
	}
}