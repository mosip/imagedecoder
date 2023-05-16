package io.mosip.imagedecoder.model.wsq;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WsqWavletTree {
	private int x;
	private int y;
	private int lenX;
	private int lenY;
	private int invRow;
	private int invCol;
}
