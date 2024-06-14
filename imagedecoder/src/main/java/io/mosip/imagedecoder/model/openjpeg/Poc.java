package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Progression order changes
 */
public class Poc {
	/** Resolution num start, Component num start, given by POC */
	private int resNo0;
	private int compNo0;
	/** Layer num end,Resolution num end, Component num end, given by POC */
	private int layNo1;
	private int resNo1;
	private int compNo1;
	/** Layer num start,Precinct num start, Precinct num end */
	private int layNo0;
	private int precNo0;
	private int precNo1;
	/** Progression order enum */
	private ProgressionOrder progressionOrder;
	private ProgressionOrder progressionOrder1;
	/** Progression order string */
	private char[] progressionName = new char[5];
	/** Tile number */
	private int tile;
	/** Start and end values for Tile width and height */
	private int tX0;
	private int tX1;
	private int tY0;
	private int tY1;
	/** Start value, initialised in piInitEncode */
	private int layS;
	private int resS;
	private int compS;
	private int prcS;
	/** End value, initialised in piInitEncode */
	private int layE;
	private int resE;
	private int compE;
	private int prcE;
	/**
	 * Start and end values of Tile width and height, initialised in
	 * piInitEncode
	 */
	private int tXS;
	private int tXE;
	private int tYS;
	private int tYE;
	private int dX;
	private int dY;
	/** Temporary values for Tile parts, initialised in piCreateEncode */
	private int layTmp;
	private int resTmp;
	private int compTmp;
	private int prcTmp;
	private int tx0Tmp;
	private int ty0Tmp;
}

