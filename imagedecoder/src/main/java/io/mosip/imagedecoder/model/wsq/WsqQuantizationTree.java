package io.mosip.imagedecoder.model.wsq;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WsqQuantizationTree {
	private short x;     /* UL corner of block */
	private short y;
	private short lenX;  /* block size */
	private short lenY;  /* block size */  
}
