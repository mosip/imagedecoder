package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tag tree
*/
public class TgtTree {
	private int noOfLeafSH;
	private int noOfLeafSV;
	private int noOfNodes;
	private TgtNode[] nodes;
}