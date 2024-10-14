package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * JP2 component
 */
@Data
@ToString
public class JP2Component {
	private int depth;
	private int sgnd;
	private int bpcc;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof JP2Component))
			return false;
		JP2Component that = (JP2Component) obj;
		return canEqual(that) && depth == that.depth && sgnd == that.sgnd && bpcc == that.bpcc;
	}

	@Override
	public int hashCode() {
		return Objects.hash(depth, sgnd, bpcc);
	}

	public boolean canEqual(Object obj) {
		return obj instanceof JP2Component;
	}
}