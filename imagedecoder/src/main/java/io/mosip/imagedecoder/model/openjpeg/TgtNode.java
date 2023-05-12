package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tag node
*/
public class TgtNode {
	private int parent;
	private int value;
	private int low;
	private int known;
}