package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * pi iterator
 */
public class PiIterator {
	/** Enabling Tile part generation*/
	private int tilePartOn;
	/** precise if the packet has been already used (usefull for progression order change) */
	private int[] include;
	/** layer step used to localize the packet in the include vector */
	private int stepL;
	/** resolution step used to localize the packet in the include vector */
	private int stepR;	
	/** component step used to localize the packet in the include vector */
	private int stepC;	
	/** precinct step used to localize the packet in the include vector */
	private int stepP;	
	/** component that identify the packet */
	private int compNo;
	/** resolution that identify the packet */
	private int resNo;
	/** precinct that identify the packet */
	private int precNo;
	/** layer that identify the packet */
	private int layNo;   
	/** 0 if the first packet */
	private int first;
	/** progression order change information */
	private Poc poc;
	/** number of components in the image */
	private int noOfComps;
	/** Components*/
	private PiComponent[] comps;
	private int tX0, tY0, tX1, tY1;
	private int x, y, dX, dY;
}
