package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tag tree
 */
@Data
@ToString
public class TgtTree {
	private int noOfLeafSH;
	private int noOfLeafSV;
	private int noOfNodes;
	private TgtNode[] nodes;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TgtTree tgtTree = (TgtTree) o;
		return noOfLeafSH == tgtTree.noOfLeafSH && noOfLeafSV == tgtTree.noOfLeafSV && noOfNodes == tgtTree.noOfNodes
				&& Arrays.equals(nodes, tgtTree.nodes);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(noOfLeafSH, noOfLeafSV, noOfNodes);
		result = 31 * result + Arrays.hashCode(nodes);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TgtTree;
	}
}