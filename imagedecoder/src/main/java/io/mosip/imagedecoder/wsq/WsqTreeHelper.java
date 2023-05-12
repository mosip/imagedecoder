package io.mosip.imagedecoder.wsq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.imagedecoder.model.wsq.WsqQuantizationTree;
import io.mosip.imagedecoder.model.wsq.WsqWavletTree;

public class WsqTreeHelper {
	private Logger LOGGER = LoggerFactory.getLogger(WsqTreeHelper.class);
	// Static variable reference of singleInstance of type Singleton
    private static WsqTreeHelper singleInstance = null;    
    private WsqTreeHelper()
	{ 
		super ();
	} 
  
	//synchronized method to control simultaneous access 
	public static synchronized WsqTreeHelper getInstance()
	{ 
		if (singleInstance == null)
			singleInstance = new WsqTreeHelper();
  
        return singleInstance;
	}
	/************************************************************************/
	/* Routines used to generate the "trees" used */
	/* in creating the wavelet subbands (waveletTree) */
	/* and when quantizing the subbands (quantizationTree) in */
	/* the WSQ compression/decompression algorithms. */
	/************************************************************************/

	/* Build WSQ decomposition trees. */
	/************************************************************************/
	public void buildWsqTrees(WsqWavletTree waveletTree[], int w_treelen, WsqQuantizationTree quantizationTree[],
			int q_treelen, int width, int height) {
		/* Build a W-TREE structure for the image. */
		buildWaveletTree(waveletTree, width, height);
		/* Build a Q-TREE structure for the image. */
		buildQuantizationTree(waveletTree, quantizationTree);
	}

	/********************************************************************/
	/* Routine to obtain subband "x-y locations" for creating wavelets. */
	/********************************************************************/
	private void buildWaveletTree(WsqWavletTree waveletTree[], /* wavelet tree structure */
			int width, /* image width */
			int height) /* image height */
	{
		int lenx, lenx2, leny, leny2; /*
										 * starting lengths of sections of the image being split into subbands
										 */
		int node;

		for (node = 0; node < 20; node++) {
			waveletTree[node] = new WsqWavletTree();
			waveletTree[node].setInvRow(0);
			waveletTree[node].setInvCol(0);
		}
		waveletTree[2].setInvRow(1);
		waveletTree[4].setInvRow(1);
		waveletTree[7].setInvRow(1);
		waveletTree[9].setInvRow(1);
		waveletTree[11].setInvRow(1);
		waveletTree[13].setInvRow(1);
		waveletTree[16].setInvRow(1);
		waveletTree[18].setInvRow(1);
		waveletTree[3].setInvCol(1);
		waveletTree[5].setInvCol(1);
		waveletTree[8].setInvCol(1);
		waveletTree[9].setInvCol(1);
		waveletTree[12].setInvCol(1);
		waveletTree[13].setInvCol(1);
		waveletTree[17].setInvCol(1);
		waveletTree[18].setInvCol(1);

		waveletTree4(waveletTree, 0, 1, width, height, 0, 0, 1);

		if ((waveletTree[1].getLenX() % 2) == 0) {
			lenx = waveletTree[1].getLenX() / 2;
			lenx2 = lenx;
		} else {
			lenx = (waveletTree[1].getLenX() + 1) / 2;
			lenx2 = lenx - 1;
		}

		if ((waveletTree[1].getLenY() % 2) == 0) {
			leny = waveletTree[1].getLenY() / 2;
			leny2 = leny;
		} else {
			leny = (waveletTree[1].getLenY() + 1) / 2;
			leny2 = leny - 1;
		}

		waveletTree4(waveletTree, 4, 6, lenx2, leny, lenx, 0, 0);
		waveletTree4(waveletTree, 5, 10, lenx, leny2, 0, leny, 0);
		waveletTree4(waveletTree, 14, 15, lenx, leny, 0, 0, 0);

		waveletTree[19].setX(0);
		waveletTree[19].setY(0);
		if ((waveletTree[15].getLenX() % 2) == 0)
			waveletTree[19].setLenX(waveletTree[15].getLenX() / 2);
		else
			waveletTree[19].setLenX((waveletTree[15].getLenX() + 1) / 2);

		if ((waveletTree[15].getLenY() % 2) == 0)
			waveletTree[19].setLenY(waveletTree[15].getLenY() / 2);
		else
			waveletTree[19].setLenY((waveletTree[15].getLenY() + 1) / 2);

		/*
		for (node = 0; node < 20; node++)
			LOGGER.debug(String.format("t%d -> x = %d  y = %d : dx = %d  dy = %d : ir = %d  ic = %d", node,
					waveletTree[node].getX(), waveletTree[node].getY(), waveletTree[node].getLenX(),
					waveletTree[node].getLenY(), waveletTree[node].getInvRow(), waveletTree[node].getInvCol()));
		*/
		return;
	}

	/***************************************************************/
	/* Gives location and size of subband splits for buildWaveletTree. */
	/***************************************************************/
	private void waveletTree4(WsqWavletTree waveletTree[], /* wavelet tree structure */
			int start1, /* waveletTree locations to start calculating */
			int start2, /* subband split locations and sizes */
			int lenx, /* (temp) subband split location and sizes */
			int leny, int x, int y, int stop1) /* 0 normal operation, 1 used to avoid marking */
	/* size and location of subbands 60-63 */
	{
		int evenX, evenY; /* Check length of subband for even or odd */
		int p1 = start1, p2 = start2; /*
										 * waveletTree locations for storing subband sizes and locations
										 */
		evenX = lenx % 2;
		evenY = leny % 2;

		waveletTree[p1].setX(x);
		waveletTree[p1].setY(y);
		waveletTree[p1].setLenX(lenx);
		waveletTree[p1].setLenY(leny);

		waveletTree[p2].setX(x);
		waveletTree[p2 + 2].setX(x);
		waveletTree[p2].setY(y);
		waveletTree[p2 + 1].setY(y);

		if (evenX == 0) {
			waveletTree[p2].setLenX(lenx / 2);
			waveletTree[p2 + 1].setLenX(waveletTree[p2].getLenX());
		} else {
			if (p1 == 4) {
				waveletTree[p2].setLenX((lenx - 1) / 2);
				waveletTree[p2 + 1].setLenX(waveletTree[p2].getLenX() + 1);
			} else {
				waveletTree[p2].setLenX((lenx + 1) / 2);
				waveletTree[p2 + 1].setLenX(waveletTree[p2].getLenX() - 1);
			}
		}
		waveletTree[p2 + 1].setX(waveletTree[p2].getLenX() + x);
		if (stop1 == 0) {
			waveletTree[p2 + 3].setLenX(waveletTree[p2 + 1].getLenX());
			waveletTree[p2 + 3].setX(waveletTree[p2 + 1].getX());
		}
		waveletTree[p2 + 2].setLenX(waveletTree[p2].getLenX());

		if (evenY == 0) {
			waveletTree[p2].setLenY(leny / 2);
			waveletTree[p2 + 2].setLenY(waveletTree[p2].getLenY());
		} else {
			if (p1 == 5) {
				waveletTree[p2].setLenY((leny - 1) / 2);
				waveletTree[p2 + 2].setLenY(waveletTree[p2].getLenY() + 1);
			} else {
				waveletTree[p2].setLenY((leny + 1) / 2);
				waveletTree[p2 + 2].setLenY(waveletTree[p2].getLenY() - 1);
			}
		}
		waveletTree[p2 + 2].setY(waveletTree[p2].getLenY() + y);
		if (stop1 == 0) {
			waveletTree[p2 + 3].setLenY(waveletTree[p2 + 2].getLenY());
			waveletTree[p2 + 3].setY(waveletTree[p2 + 2].getY());
		}
		waveletTree[p2 + 1].setLenY(waveletTree[p2].getLenY());
	}

	/****************************************************************/
	private void buildQuantizationTree(WsqWavletTree[] waveletTree, /* wavelet tree structure */
		WsqQuantizationTree[] quantizationTree) /* quantization tree structure */
	{
		int node;

		for (node = 0; node < quantizationTree.length; node++) {
			quantizationTree[node] = new WsqQuantizationTree();
		}

		quantizationTree16(quantizationTree, 3, waveletTree[14].getLenX(), waveletTree[14].getLenY(),
				waveletTree[14].getX(), waveletTree[14].getY(), 0, 0);
		quantizationTree16(quantizationTree, 19, waveletTree[4].getLenX(), waveletTree[4].getLenY(),
				waveletTree[4].getX(), waveletTree[4].getY(), 0, 1);
		quantizationTree16(quantizationTree, 48, waveletTree[0].getLenX(), waveletTree[0].getLenY(),
				waveletTree[0].getX(), waveletTree[0].getY(), 0, 0);
		quantizationTree16(quantizationTree, 35, waveletTree[5].getLenX(), waveletTree[5].getLenY(),
				waveletTree[5].getX(), waveletTree[5].getY(), 1, 0);
		quantizationTree4(quantizationTree, 0, waveletTree[19].getLenX(), waveletTree[19].getLenY(),
				waveletTree[19].getX(), waveletTree[19].getY());

		/*
		for (node = 0; node < 60; node++)
			LOGGER.error(String.format("t%d -> x = %d  y = %d : lx = %d  ly = %d", node,
					quantizationTree[node].getX(), quantizationTree[node].getY(), quantizationTree[node].getLenX(),
					quantizationTree[node].getLenY()));
		*/
		return;
	}

	/*****************************************************************/
	private void quantizationTree16(WsqQuantizationTree[] quantizationTree, /* quantization tree structure */
			int start, /* quantizationTree location of first subband */
			/* in the subband group being calculated */
			int lenx, /* (temp) subband location and sizes */
			int leny, int x, int y, int row, /* NEW */ /* spectral invert 1st row/col splits */
			int column) /* NEW */
	{
		int tempX, temp2X; /* temporary x values */
		int tempY, temp2Y; /* temporary y values */
		int evenX, evenY; /* Check length of subband for even or odd */
		int p; /* indicates subband information being stored */

		p = start;
		evenX = lenx % 2;
		evenY = leny % 2;

		if (evenX == 0) {
			tempX = lenx / 2;
			temp2X = tempX;
		} else {
			if (column != 0) {
				temp2X = (lenx + 1) / 2;
				tempX = temp2X - 1;
			} else {
				tempX = (lenx + 1) / 2;
				temp2X = tempX - 1;
			}
		}

		if (evenY == 0) {
			tempY = leny / 2;
			temp2Y = tempY;
		} else {
			if (row != 0) {
				temp2Y = (leny + 1) / 2;
				tempY = temp2Y - 1;
			} else {
				tempY = (leny + 1) / 2;
				temp2Y = tempY - 1;
			}
		}

		evenX = tempX % 2;
		evenY = tempY % 2;

		quantizationTree[p].setX((short) x);
		quantizationTree[p + 2].setX((short) x);
		quantizationTree[p].setY((short) y);
		quantizationTree[p + 1].setY((short) y);
		if (evenX == 0) {
			quantizationTree[p].setLenX((short) (tempX / 2));
			quantizationTree[p + 1].setLenX(quantizationTree[p].getLenX());
			quantizationTree[p + 2].setLenX(quantizationTree[p].getLenX());
			quantizationTree[p + 3].setLenX(quantizationTree[p].getLenX());
		} else {
			quantizationTree[p].setLenX((short) ((tempX + 1) / 2));
			quantizationTree[p + 1].setLenX((short) (quantizationTree[p].getLenX() - 1));
			quantizationTree[p + 2].setLenX(quantizationTree[p].getLenX());
			quantizationTree[p + 3].setLenX(quantizationTree[p + 1].getLenX());
		}
		quantizationTree[p + 1].setX((short) (x + quantizationTree[p].getLenX()));
		quantizationTree[p + 3].setX(quantizationTree[p + 1].getX());
		if (evenY == 0) {
			quantizationTree[p].setLenY((short) (tempY / 2));
			quantizationTree[p + 1].setLenY(quantizationTree[p].getLenY());
			quantizationTree[p + 2].setLenY(quantizationTree[p].getLenY());
			quantizationTree[p + 3].setLenY(quantizationTree[p].getLenY());
		} else {
			quantizationTree[p].setLenY((short) ((tempY + 1) / 2));
			quantizationTree[p + 1].setLenY(quantizationTree[p].getLenY());
			quantizationTree[p + 2].setLenY((short) (quantizationTree[p].getLenY() - 1));
			quantizationTree[p + 3].setLenY(quantizationTree[p + 2].getLenY());
		}
		quantizationTree[p + 2].setY((short) (y + quantizationTree[p].getLenY()));
		quantizationTree[p + 3].setY(quantizationTree[p + 2].getY());

		evenX = temp2X % 2;

		quantizationTree[p + 4].setX((short) (x + tempX));
		quantizationTree[p + 6].setX(quantizationTree[p + 4].getX());
		quantizationTree[p + 4].setY((short) y);
		quantizationTree[p + 5].setY((short) y);
		quantizationTree[p + 6].setY(quantizationTree[p + 2].getY());
		quantizationTree[p + 7].setY(quantizationTree[p + 2].getY());
		quantizationTree[p + 4].setLenY(quantizationTree[p].getLenY());
		quantizationTree[p + 5].setLenY(quantizationTree[p].getLenY());
		quantizationTree[p + 6].setLenY(quantizationTree[p + 2].getLenY());
		quantizationTree[p + 7].setLenY(quantizationTree[p + 2].getLenY());
		if (evenX == 0) {
			quantizationTree[p + 4].setLenX((short) (temp2X / 2));
			quantizationTree[p + 5].setLenX(quantizationTree[p + 4].getLenX());
			quantizationTree[p + 6].setLenX(quantizationTree[p + 4].getLenX());
			quantizationTree[p + 7].setLenX(quantizationTree[p + 4].getLenX());
		} else {
			quantizationTree[p + 5].setLenX((short) ((temp2X + 1) / 2));
			quantizationTree[p + 4].setLenX((short) (quantizationTree[p + 5].getLenX() - 1));
			quantizationTree[p + 6].setLenX(quantizationTree[p + 4].getLenX());
			quantizationTree[p + 7].setLenX(quantizationTree[p + 5].getLenX());
		}
		quantizationTree[p + 5].setX((short) (quantizationTree[p + 4].getX() + quantizationTree[p + 4].getLenX()));
		quantizationTree[p + 7].setX(quantizationTree[p + 5].getX());

		evenY = temp2Y % 2;

		quantizationTree[p + 8].setX((short) x);
		quantizationTree[p + 9].setX(quantizationTree[p + 1].getX());
		quantizationTree[p + 10].setX((short) x);
		quantizationTree[p + 11].setX(quantizationTree[p + 1].getX());
		quantizationTree[p + 8].setY((short) (y + tempY));
		quantizationTree[p + 9].setY(quantizationTree[p + 8].getY());
		quantizationTree[p + 8].setLenX(quantizationTree[p].getLenX());
		quantizationTree[p + 9].setLenX(quantizationTree[p + 1].getLenX());
		quantizationTree[p + 10].setLenX(quantizationTree[p].getLenX());
		quantizationTree[p + 11].setLenX(quantizationTree[p + 1].getLenX());
		if (evenY == 0) {
			quantizationTree[p + 8].setLenY((short) (temp2Y / 2));
			quantizationTree[p + 9].setLenY(quantizationTree[p + 8].getLenY());
			quantizationTree[p + 10].setLenY(quantizationTree[p + 8].getLenY());
			quantizationTree[p + 11].setLenY(quantizationTree[p + 8].getLenY());
		} else {
			quantizationTree[p + 10].setLenY((short) ((temp2Y + 1) / 2));
			quantizationTree[p + 11].setLenY(quantizationTree[p + 10].getLenY());
			quantizationTree[p + 8].setLenY((short) (quantizationTree[p + 10].getLenY() - 1));
			quantizationTree[p + 9].setLenY(quantizationTree[p + 8].getLenY());
		}
		quantizationTree[p + 10].setY((short) (quantizationTree[p + 8].getY() + quantizationTree[p + 8].getLenY()));
		quantizationTree[p + 11].setY(quantizationTree[p + 10].getY());

		quantizationTree[p + 12].setX(quantizationTree[p + 4].getX());
		quantizationTree[p + 13].setX(quantizationTree[p + 5].getX());
		quantizationTree[p + 14].setX(quantizationTree[p + 4].getX());
		quantizationTree[p + 15].setX(quantizationTree[p + 5].getX());
		quantizationTree[p + 12].setY(quantizationTree[p + 8].getY());
		quantizationTree[p + 13].setY(quantizationTree[p + 8].getY());
		quantizationTree[p + 14].setY(quantizationTree[p + 10].getY());
		quantizationTree[p + 15].setY(quantizationTree[p + 10].getY());
		quantizationTree[p + 12].setLenX(quantizationTree[p + 4].getLenX());
		quantizationTree[p + 13].setLenX(quantizationTree[p + 5].getLenX());
		quantizationTree[p + 14].setLenX(quantizationTree[p + 4].getLenX());
		quantizationTree[p + 15].setLenX(quantizationTree[p + 5].getLenX());
		quantizationTree[p + 12].setLenY(quantizationTree[p + 8].getLenY());
		quantizationTree[p + 13].setLenY(quantizationTree[p + 8].getLenY());
		quantizationTree[p + 14].setLenY(quantizationTree[p + 10].getLenY());
		quantizationTree[p + 15].setLenY(quantizationTree[p + 10].getLenY());
	}

	/********************************************************************/
	private void quantizationTree4(WsqQuantizationTree[] quantizationTree, /* quantization tree structure */
			int start, /* quantizationTree location of first subband */
			/* in the subband group being calculated */
			int lenX, /* (temp) subband location and sizes */
			int lenY, int x, int y) {
		int evenX, evenY; /* Check length of subband for even or odd */
		int p; /* indicates where subband information being stored */

		p = start;
		evenX = lenX % 2;
		evenY = lenY % 2;

		quantizationTree[p].setX((short) x);
		quantizationTree[p + 2].setX((short) x);
		quantizationTree[p].setY((short) y);
		quantizationTree[p + 1].setY((short) y);
		if (evenX == 0) {
			quantizationTree[p].setLenX((short) (lenX / 2));
			quantizationTree[p + 1].setLenX(quantizationTree[p].getLenX());
			quantizationTree[p + 2].setLenX(quantizationTree[p].getLenX());
			quantizationTree[p + 3].setLenX(quantizationTree[p].getLenX());
		} else {
			quantizationTree[p].setLenX((short) ((lenX + 1) / 2));
			quantizationTree[p + 1].setLenX((short) (quantizationTree[p].getLenX() - 1));
			quantizationTree[p + 2].setLenX(quantizationTree[p].getLenX());
			quantizationTree[p + 3].setLenX(quantizationTree[p + 1].getLenX());
		}
		quantizationTree[p + 1].setX((short) (x + quantizationTree[p].getLenX()));
		quantizationTree[p + 3].setX(quantizationTree[p + 1].getX());
		if (evenY == 0) {
			quantizationTree[p].setLenY((short) (lenY / 2));
			quantizationTree[p + 1].setLenY(quantizationTree[p].getLenY());
			quantizationTree[p + 2].setLenY(quantizationTree[p].getLenY());
			quantizationTree[p + 3].setLenY(quantizationTree[p].getLenY());
		} else {
			quantizationTree[p].setLenY((short) ((lenY + 1) / 2));
			quantizationTree[p + 1].setLenY(quantizationTree[p].getLenY());
			quantizationTree[p + 2].setLenY((short) (quantizationTree[p].getLenY() - 1));
			quantizationTree[p + 3].setLenY(quantizationTree[p + 2].getLenY());
		}
		quantizationTree[p + 2].setY((short) (y + quantizationTree[p].getLenY()));
		quantizationTree[p + 3].setY(quantizationTree[p + 2].getY());
	}
}
