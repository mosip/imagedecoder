package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Segment
*/
public class TcdSegment {
	private byte[] data;
	private int dataIndex;
	private int noOfPasses;
	private int length;
	private int maxPasses;
	private int noOfNewPasses;
	private int newLength;
}