package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DecoderFunctionInfo {
	/** marker value */
	private int id;
	/** value of the state when the marker can appear */
	private int states;
	/** action linked to the marker */
	private String j2kFunctionName;

	public DecoderFunctionInfo(int id, int states, String j2kFunctionName) {
		super();
		this.id = id;
		this.states = states;
		this.j2kFunctionName = j2kFunctionName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DecoderFunctionInfo))
			return false;
		DecoderFunctionInfo that = (DecoderFunctionInfo) obj;
		return id == that.id && states == that.states && canEqual(that)
				&& (j2kFunctionName != null ? j2kFunctionName.equals(that.j2kFunctionName)
						: that.j2kFunctionName == null);
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + states;
		result = 31 * result + (j2kFunctionName != null ? j2kFunctionName.hashCode() : 0);
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof DecoderFunctionInfo;
	}
}