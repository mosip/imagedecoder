package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data

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
}
