package io.mosip.imagedecoder.model.openjpeg;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * MQ coder
 */
public class MQCoder {
	private long c;
	private long a;
	private long ct;
	private byte[] bp;
	private int bpIndex;
	private int start;
	private int end;
	private int contextIndex;
	private MQCoderState[] contexts = new MQCoderState[OpenJpegConstant.MQC_NUMCTXS];
	private MQCoderState currentContext;
}
