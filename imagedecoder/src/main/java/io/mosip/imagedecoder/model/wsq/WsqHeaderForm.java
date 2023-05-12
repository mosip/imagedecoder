package io.mosip.imagedecoder.model.wsq;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WsqHeaderForm {
	private int black;
	private int white;
	private int width;
	private int height;
	private float[] mShift = new float[1];
	private float[] rScale = new float[1];
	private int wsqEncoder;
	private long software;
}
