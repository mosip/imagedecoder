package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
JP2 Box
*/
public class JP2Box {
	private int length;
	private int type;
	private int initPosition;
}