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
	private int resNo0, compNo0;
	/** Layer num end,Resolution num end, Component num end, given by POC */
	private int layNo1, resNo1, compNo1;
	/** Layer num start,Precinct num start, Precinct num end */
	private int layNo0, precNo0, precNo1;
	/** Progression order enum */
	private ProgressionOrder progressionOrder, progressionOrder1;
	/** Progression order string */
	private char[] progressionName = new char[5];
	/** Tile number */
	private int tile;
	/** Start and end values for Tile width and height */
	private int tX0, tX1, tY0, tY1;
	/** Start value, initialised in piInitEncode */
	private int layS, resS, compS, prcS;
	/** End value, initialised in piInitEncode */
	private int layE, resE, compE, prcE;
	/**
	 * Start and end values of Tile width and height, initialised in
	 * piInitEncode
	 */
	private int tXS, tXE, tYS, tYE, dX, dY;
	/** Temporary values for Tile parts, initialised in piCreateEncode */
	private int layTmp, resTmp, compTmp, prcTmp, tx0Tmp, ty0Tmp;
}

