package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class J2KProgressionOrder {
	private ProgressionOrder progressionOrder;
	private char[] progressionName = new char[4];
	public J2KProgressionOrder(ProgressionOrder progressionOrder, char[] progressionName) {
		super();
		this.progressionOrder = progressionOrder;
		this.progressionName = progressionName;
	}	
}
