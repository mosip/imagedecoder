package io.mosip.imagedecoder.wsq;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;

import java.text.MessageFormat;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import io.mosip.imagedecoder.constant.wsq.WsqErrorCode;
import io.mosip.imagedecoder.model.wsq.WsqFet;
import io.mosip.imagedecoder.util.StringUtil;

public class WsqFetHelper {
	private Logger logger = ImageDecoderLogger.getLogger(WsqFetHelper.class);

	// Static variable reference of singleInstance of type Singleton
	private static WsqFetHelper singleInstance = null;

	private WsqFetHelper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized WsqFetHelper getInstance() {
		if (singleInstance == null)
			singleInstance = new WsqFetHelper();

		return singleInstance;
	}

	/*****************************************************************/
	@SuppressWarnings({ "java:S135", "java:S1854", "java:S3626", "java:S3776" })
	public int string2fet(WsqFet fet, char[] arrData) {
		int ret;
		char[] name = new char[WsqConstant.MAXFETLENGTH];
		char[] value = new char[WsqConstant.MAXFETLENGTH];
		char[] vptr;

		int dataIndex = 0;
		int valueIndex = 0;
		int nameIndex = 0;
		while (arrData[dataIndex] != '\0') {
			/* Get next name */
			nameIndex = 0;
			name = new char[WsqConstant.MAXFETLENGTH];
			while ((arrData[dataIndex] != '\0') && (arrData[dataIndex] != ' ') && (arrData[dataIndex] != '\t'))
				name[nameIndex++] = arrData[dataIndex++];
			name[nameIndex] = '\0';

			/* Skip white space */
			while ((arrData[dataIndex] != '\0') && ((arrData[dataIndex] == ' ') || (arrData[dataIndex] == '\t')))
				dataIndex++;

			/* Get next value */
			valueIndex = 0;
			value = new char[WsqConstant.MAXFETLENGTH];
			while ((arrData[dataIndex] != '\0') && (arrData[dataIndex] != '\n'))
				value[valueIndex++] = arrData[dataIndex++];
			value[valueIndex] = '\0';

			/* Skip white space */
			while ((arrData[dataIndex] != '\0')
					&& ((arrData[dataIndex] == ' ') || (arrData[dataIndex] == '\t') || (arrData[dataIndex] == '\n')))
				dataIndex++;

			/* Test (name,value) pair */
			if (new String(name).length() == 0) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, "string2fet : empty name string found.");
				return (WsqErrorCode.EMPTY_STRING_FOUND.getErrorId());
			}
			if (new String(value).length() == 0)
				vptr = null;
			else
				vptr = value;

			/* Store name and value pair into FET. */
			if ((ret = updateFet(name, vptr, fet)) != 0) {
				return ret;
			}
		}
		return 0;
	}

	/********************************************************************/
	public WsqFet allocFet(int numfeatures) {
		WsqFet fet = new WsqFet();

		/* calloc here is required */
		fet.setNames(new String[numfeatures]);
		fet.setValues(new String[numfeatures]);
		fet.setAlloc(numfeatures);
		fet.setNum(0);
		return (fet);
	}

	/***********************************************************************/
	@SuppressWarnings({ "java:S135", "java:S3626", "java:S3776", "java:S6035" })
	public int updateFet(char[] feature, char[] value, WsqFet fet) {
		int ret;
		int item;
		int increased;
		int incr;

		for (item = 0; (item < fet.getNum()); item++) {
			if (fet.getNames()[item] != null && feature != null
					&& StringUtil.getInstance().stringCompare(fet.getNames()[item], new String(feature)) != 0) {
				continue;
			} else {
				break;
			}
		}

		if (item < fet.getNum()) {
			if (fet.getValues()[item] != null) {
				fet.getValues()[item] = null;
			}
			if (value != null) {
				fet.getValues()[item] = new String(value);
			}
		} else {
			if (fet.getNum() >= fet.getAlloc()) {
				incr = fet.getAlloc() / 10; /* add 10% or 10 which- */
				increased = fet.getAlloc() + Math.max(10, incr); /* ever is larger */
				if ((ret = reallocFet(fet, increased)) != 0)
					return ret;
			}
			fet.getNames()[fet.getNum()] = new String(feature).replaceAll("\"|\\r|\\n", "").trim();
			if (value != null) {
				fet.getValues()[fet.getNum()] = new String(value).replaceAll("\"|\\r|\\n", "").trim();
			}
			fet.setNum(fet.getNum() + 1);
		}

		return 0;
	}

	/********************************************************************/
	@SuppressWarnings({ "java:S1854", "java:S3516" })
	public int reallocFet(WsqFet fet, int newlen) {
		/* If fet not allocated ... */
		if ((fet == null || fet.getAlloc() == 0)) {
			/* Allocate the fet. */
			fet = allocFet(newlen);
			/* Otherwise allocation was successful. */
			return 0;
		}

		/* Oherwise, reallocate fet. */
		fet.setNames(new String[newlen]);
		fet.setValues(new String[newlen]);
		fet.setAlloc(newlen);

		return 0;
	}

	/*******************************************************************/
	@SuppressWarnings({ "java:S135", "java:S2629", "java:S3626" })
	public int extractFet(StringBuilder value, char[] feature, WsqFet fet) {
		int item;
		for (item = 0; (item < fet.getNum()); item++) {
			if (fet.getNames()[item] != null && feature != null) {
				if (StringUtil.getInstance().stringCompare(fet.getNames()[item].trim(),
						new String(feature).trim()) != 0) {
					continue;
				} else
					break;
			}
		}
		if (item >= fet.getNum()) {
			if (feature != null)
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, MessageFormat.format("extractFet : feature {0} not found",
						new String(feature)));
			return (WsqErrorCode.EMPTY_STRING_FOUND.getErrorId());
		}
		if (fet.getValues()[item] != null) {
			value.append(fet.getValues()[item] + "");
		} else
			value.append("");

		return 0;
	}
}
