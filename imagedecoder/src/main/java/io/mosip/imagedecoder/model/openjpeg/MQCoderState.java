package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * This struct defines the state of a context.
 */
public class MQCoderState {
	/** the probability of the Least Probable Symbol (0.75->0x8000, 1.5->0xffff) */
	private long qeval;
	/** the Most Probable Symbol (0 or 1) */
	private int mps;
	/** next state if the next encoded symbol is the MPS */
	private int nmpsIndex;
	private MQCoderState nmps;
	/** next state if the next encoded symbol is the LPS */
	private int nlpsIndex;
	private MQCoderState nlps;
	public MQCoderState(long qeval, int mps, int nmpsIndex, int nlpsIndex) {
		super();
		this.qeval = qeval;
		this.mps = mps;
		this.nmpsIndex = nmpsIndex;
		this.nlpsIndex = nlpsIndex;
	}
}
