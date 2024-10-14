package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tag node
 */
@Data
@ToString
public class TgtNode {
	private int parent;
	private int value;
	private int low;
	private int known;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TgtNode tgtNode = (TgtNode) o;
		return parent == tgtNode.parent && value == tgtNode.value && low == tgtNode.low && known == tgtNode.known;
	}

	@Override
	public int hashCode() {
		return Objects.hash(parent, value, low, known);
	}

	public boolean canEqual(Object other) {
		return other instanceof TgtNode;
	}
}