package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
pass
*/
public class TcdPass {
	private int rate;
	private double distortionDec;
	private int term, length;
}