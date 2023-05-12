package io.mosip.imagedecoder.wsq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import io.mosip.imagedecoder.constant.wsq.WsqErrorCode;
import io.mosip.imagedecoder.model.wsq.WsqHuffCode;

public class WsqHuffHelper {
	private Logger LOGGER = LoggerFactory.getLogger(WsqHuffHelper.class);
	// Static variable reference of singleInstance of type Singleton
    private static WsqHuffHelper singleInstance = null;    
    private WsqHuffHelper()
	{ 
		super ();
	} 
  
	//synchronized method to control simultaneous access 
	public static synchronized WsqHuffHelper getInstance()
	{ 
		if (singleInstance == null)
			singleInstance = new WsqHuffHelper();
  
        return singleInstance;
	}
	/**************************************************************************/
	/* This routine defines the huffman code sizes for each difference category */
	/**************************************************************************/
	public int buildHuffSizes(WsqHuffCode[] huffcodeTable, int[] tempSize, int[] huffBits, int maxHuffCounts) {
		int codeSize; /* code sizes */
		int numberOfCodes = 1; /* the number codes for a given code size */

		for (int code_table = 0; code_table < huffcodeTable.length; code_table++) {
			huffcodeTable[code_table] = new WsqHuffCode();
		}

		tempSize[0] = 0;
		for (codeSize = 1; codeSize <= WsqConstant.MAX_HUFFBITS; codeSize++) {
			while (numberOfCodes <= huffBits[codeSize - 1]) {
				huffcodeTable[tempSize[0]].setSize(codeSize);
				tempSize[0]++;
				numberOfCodes++;
			}
			numberOfCodes = 1;
		}
		huffcodeTable[tempSize[0]].setSize(0);

		/*
		int ii;
		
		LOGGER.debug(String.format("In buildHuffSizes:"));
		for (ii = 0; ii < maxHuffCounts + 1; ii++)
			LOGGER.debug(String.format("huffcode size[%d] = %d", ii, huffcodeTable[ii].getSize()));
		*/
		return 0;
	}

	/****************************************************************************/
	/* This routine defines the huffman codes needed for each difference category */
	/****************************************************************************/
	public void buildHuffCodes(WsqHuffCode[] huffcodeTable) {
		int hcTableIndex = 0; /* hcTableIndex to code word information */
		int tempCode = 0; /* used to construct code word */
		int tempSize; /* used to construct code size */

		tempSize = huffcodeTable[hcTableIndex].getSize();
		if (huffcodeTable[hcTableIndex].getSize() == 0)
			return;

		do {
			do {
				huffcodeTable[hcTableIndex].setCode(tempCode);
				tempCode++;
				hcTableIndex++;
			} while (huffcodeTable[hcTableIndex].getSize() == tempSize);

			if (huffcodeTable[hcTableIndex].getSize() == 0)
				return;

			do {
				tempCode <<= 1;
				tempSize++;
			} while (huffcodeTable[hcTableIndex].getSize() != tempSize);
		} while (huffcodeTable[hcTableIndex].getSize() == tempSize);
		return;
	}

	/*********************************************/
	/* checkWsqHuffCodes - Checks for an all 1's code in the code table. */
	/*********************************************/
	public int checkWsqHuffCodes(WsqHuffCode[] hufftable, int lastSize) {
		int i, k;
		int allOnes;

		for (i = 0; i < lastSize; i++) {
			allOnes = 1;
			for (k = 0; (k < hufftable[i].getSize() && allOnes != 0); k++)
				allOnes = (allOnes != 0 && (((hufftable[i].getCode() >> k) & 0x0001) != 0)) ? 1 : 0;
			if (allOnes != 0) {
				LOGGER.warn(String.format(
						"A code in the hufftable contains an : all 1's code. This image may still be  decodable. It is not compliant with the WSQ specification."));
				return (WsqErrorCode.NON_COMPLIANT_WITH_WSQ_SPECS.getErrorId());
			}
		}
		return 0;
	}

	/*********************************************/
	/* routine to generate tables needed to decode */
	/*********************************************/
	public void generateDecodeTable(WsqHuffCode[] huffcodeTable, long[] maxCode, long[] minCode, int[] values,
			int[] huffBits) {
		int i, i2 = 0; /* increment variables */

		for (i = 0; i <= WsqConstant.MAX_HUFFBITS; i++) {
			maxCode[i] = 0;
			minCode[i] = 0;
			values[i] = 0;
		}

		for (i = 1; i <= WsqConstant.MAX_HUFFBITS; i++) {
			if (huffBits[i - 1] == 0) {
				maxCode[i] = -1;
				continue;
			}
			values[i] = i2;
			minCode[i] = huffcodeTable[i2].getCode();
			i2 = i2 + huffBits[i - 1] - 1;
			maxCode[i] = huffcodeTable[i2].getCode();
			i2++;
		}
	}
}
