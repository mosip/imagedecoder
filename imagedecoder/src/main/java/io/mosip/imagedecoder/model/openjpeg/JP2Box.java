package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * JP2 Box
 */
@Data
@ToString
public class JP2Box {
	private int length;
	private int type;
	private int initPosition;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof JP2Box))
			return false;
		JP2Box that = (JP2Box) obj;
		return canEqual(that) && length == that.length && type == that.type && initPosition == that.initPosition;
	}

	@Override
	public int hashCode() {
		return Objects.hash(length, type, initPosition);
	}

	public boolean canEqual(Object obj) {
		return obj instanceof JP2Box;
	}
}