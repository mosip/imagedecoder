package io.mosip.imagedecoder.model.wsq;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WsqTableDqt {
	private float binCenter;
	private float[] qBin = new float[WsqConstant.MAX_SUBBANDS];
	private float[] zBin = new float[WsqConstant.MAX_SUBBANDS];
	private int dqtDef;
}
