package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Message Header JPT stream structure
 */
@Data
@ToString
public class JPTMessageHeader {
	/** In-class Identifier */
	@SuppressWarnings({ "java:S116" })
	private long Id;
	/** Last byte information */
	private long lastByte;
	/** Class Identifier */
	private long classId;
	/** CSn : index identifier */
	private long cSnId;
	/** Message offset */
	private long msgOffset;
	/** Message length */
	private long msgLength;
	/** Auxiliary for JPP case */
	private long layerNb;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof JPTMessageHeader))
			return false;
		JPTMessageHeader that = (JPTMessageHeader) obj;
		return canEqual(that) && Id == that.Id && lastByte == that.lastByte && classId == that.classId
				&& cSnId == that.cSnId && msgOffset == that.msgOffset && msgLength == that.msgLength
				&& layerNb == that.layerNb;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Id, lastByte, classId, cSnId, msgOffset, msgLength, layerNb);
	}

	public boolean canEqual(Object obj) {
		return obj instanceof JPTMessageHeader;
	}
}