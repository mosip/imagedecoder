package io.mosip.imagedecoder.model.wsq;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WsqInfo {
	private byte[] data;
	private int width;
	private int height;
	private int depth;
	private int ppi;
	private int lossyFlag;
	private double bitRate; // bits per pixels
	private String colorSpace;
}
