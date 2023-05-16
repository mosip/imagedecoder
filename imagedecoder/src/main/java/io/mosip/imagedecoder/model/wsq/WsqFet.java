package io.mosip.imagedecoder.model.wsq;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WsqFet {
	private int alloc = WsqConstant.MAXFETS;
	private int num = 0;
	private String[] names = new String[WsqConstant.MAXFETS];
	private String[] values = new String[WsqConstant.MAXFETS];
}
