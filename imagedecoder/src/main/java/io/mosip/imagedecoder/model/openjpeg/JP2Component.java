package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/** 
JP2 component
*/
public class JP2Component {
	private int depth;		  
	private int sgnd;		   
	private int bpcc;
}