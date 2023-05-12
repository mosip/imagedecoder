package io.mosip.imagedecoder.model.wsq;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WsqTableDtt {
	private float[] lowFilter;
	private float[] highFilter;
	private int lowSize;
	private int highSize;
	private int lowDef;
	private int highDef;
}
