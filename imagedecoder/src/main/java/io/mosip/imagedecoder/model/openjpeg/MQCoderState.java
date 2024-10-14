package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * This struct defines the state of a context.
 */
@Data
@ToString
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MQCoderState))
			return false;
		MQCoderState that = (MQCoderState) obj;
		return canEqual(that) && qeval == that.qeval && mps == that.mps && nmpsIndex == that.nmpsIndex
				&& nlpsIndex == that.nlpsIndex && Objects.equals(nmps, that.nmps) && Objects.equals(nlps, that.nlps);
	}

	@Override
	public int hashCode() {
		return Objects.hash(qeval, mps, nmpsIndex, nlpsIndex, nmps, nlps);
	}

	public boolean canEqual(Object obj) {
		return obj instanceof MQCoderState;
	}
}