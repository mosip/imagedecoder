package io.mosip.imagedecoder.util.openjpeg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.text.MessageFormat;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;

import io.mosip.imagedecoder.constant.DecoderErrorCodes;
import io.mosip.imagedecoder.exceptions.DecoderException;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;

public class ImageUtil {
	private static Logger logger = ImageDecoderLogger.getLogger(ImageUtil.class);
	// Static variable reference of singleInstance of type Singleton
	private static ImageUtil singleInstance = null;

	private ImageUtil() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized ImageUtil getInstance() {
		if (singleInstance == null)
			singleInstance = new ImageUtil();

		return singleInstance;
	}

	/**
	 * Calculate the aspect ratio from a set of width/height params
	 * 
	 * @param width
	 * @param height
	 * @return aspectRatio
	 */
	public double calculateAspectRatio(int width, int height) {
		if (width == 0 || height == 0)
			throw new IllegalArgumentException("Cannot calculate aspect ratio from zero");
		if (width < 0 || height < 0)
			throw new IllegalArgumentException("Cannot calculate aspect ratio from negative numbers");
		if (width > height)
			return (double) width / height;
		else
			return (double) height / width;
	}

	/**
	 * Calculate the compression ratio from a set of width/height/depth params
	 * 
	 * @param width
	 * @param height
	 * @param depth
	 * @param compressionImageLength
	 * @return compressionRatio
	 */
	public int calculateCompressionRatio(int width, int height, int depth, int compressionImageLength) {
		if (width == 0 || height == 0 || depth == 0 || compressionImageLength == 0)
			throw new IllegalArgumentException("Cannot calculate compression Ratio from zero");
		if (width < 0 || height < 0 || depth < 0 || compressionImageLength < 0)
			throw new IllegalArgumentException("Cannot calculate compression Ratio from negative numbers");

		double compressionRatio = ((width * height * depth) / compressionImageLength);
		if (compressionRatio > 100)
			compressionRatio = 100 - compressionRatio;
		return (int) Math.abs(compressionRatio);
	}

	@SuppressWarnings({ "java:S107", "java:S135", "java:S3626", "java:S3776", "java:S6541", "unused" })
	public BufferedImage fromJ2kImage(int width, int height, OpenJpegImage image) {
		BufferedImage dib = null;

		try {
			// compute image width and height
			int wr = image.getComps()[0].getWidth();
			int wrr = MathUtil.getInstance().intCeilDivPow2(image.getComps()[0].getWidth(),
					image.getComps()[0].getFactor());

			int hr = image.getComps()[0].getHeight();
			int hrr = MathUtil.getInstance().intCeilDivPow2(image.getComps()[0].getHeight(),
					image.getComps()[0].getFactor());

			// check the number of components
			int noOfComps = image.getNoOfComps();

			boolean bIsValid = true;
			for (int c = 0; c < noOfComps - 1; c++) {
				if ((image.getComps()[c].getDX() == image.getComps()[c + 1].getDX())
						&& (image.getComps()[c].getDY() == image.getComps()[c + 1].getDY())
						&& (image.getComps()[c].getPrec() == image.getComps()[c + 1].getPrec())) {
					continue;
				} else {
					bIsValid = false;
					break;
				}
			}
			bIsValid &= ((noOfComps == 1) || (noOfComps == 3) || (noOfComps == 4));
			if (!bIsValid) {
				if (noOfComps != 0) {
					logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,
							MessageFormat.format(
									"Warning: image contains {0} greyscale components. Only the first will be loaded.",
									noOfComps));
					noOfComps = 1;
				} else {
					// unknown type
					throw new DecoderException(DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorCode(),
							DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorMessage());
				}
			}

			// create a new DIB
			if (image.getComps()[0].getPrec() <= 8) {
				switch (noOfComps) {
				case 1:
					dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_BYTE_GRAY);
					break;
				case 3:
					dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_INT_RGB);
					break;
				case 4:
					dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_INT_ARGB);
					break;
				default:
					throw new DecoderException(DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorCode(),
							DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorMessage());
				}
			} else if (image.getComps()[0].getPrec() <= 16) {
				switch (noOfComps) {
				case 1:
					dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_USHORT_GRAY);
					break;
				case 3:
					dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_INT_RGB);
					break;
				case 4:
					dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_INT_ARGB);
					break;
				default:
					throw new DecoderException(DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorCode(),
							DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorMessage());
				}
			} else {
				throw new DecoderException(DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorCode(),
						DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorMessage());
			}
			if (dib == null) {
				throw new DecoderException(DecoderErrorCodes.BUFFEREDIMAGE_ALLOCATION_FAILED_ERROR.getErrorCode(),
						DecoderErrorCodes.BUFFEREDIMAGE_ALLOCATION_FAILED_ERROR.getErrorMessage());
			}
			if (image.getComps()[0].getPrec() <= 8) {
				if (noOfComps == 1) {
					// 8-bit greyscale
					// ----------------------------------------------------------
					// load pixel data
					byte[] array = ((DataBufferByte) dib.getRaster().getDataBuffer()).getData();
					System.arraycopy(integersToBytes(image.getComps()[0].getData()), 0, array, 0, array.length);
				} else if (noOfComps == 3) {

					// 24-bit RGB
					// ----------------------------------------------------------

					// load pixel data

					int pixelCount = 0;

					for (int y = 0; y < hrr; y++) {
						int bitsIndex = hrr - 1 - y;

						for (int x = 0; x < wrr; x++) {
							int pixelPosition = (pixelCount / wrr * wr + pixelCount % wrr);

							int r = image.getComps()[0].getData()[pixelPosition];
							r += (image.getComps()[0].getSgnd() != 0 ? 1 << (image.getComps()[0].getPrec() - 1) : 0);

							int g = image.getComps()[1].getData()[pixelPosition];
							g += (image.getComps()[1].getSgnd() != 0 ? 1 << (image.getComps()[1].getPrec() - 1) : 0);

							int b = image.getComps()[2].getData()[pixelPosition];
							b += (image.getComps()[2].getSgnd() != 0 ? 1 << (image.getComps()[2].getPrec() - 1) : 0);

							dib.setRGB(x, y, new Color(r, g, b).getRGB());
							pixelCount++;
						}
					}
				} else if (noOfComps == 4) {

					// 32-bit RGBA
					// ----------------------------------------------------------

					// load pixel data

					int pixelCount = 0;

					for (int y = 0; y < hrr; y++) {
						int bitsIndex = hrr - 1 - y;

						for (int x = 0; x < wrr; x++) {
							int pixelPosition = (pixelCount / wrr * wr + pixelCount % wrr);

							int r = image.getComps()[0].getData()[pixelPosition];
							r += (image.getComps()[0].getSgnd() != 0 ? 1 << (image.getComps()[0].getPrec() - 1) : 0);

							int g = image.getComps()[1].getData()[pixelPosition];
							g += (image.getComps()[1].getSgnd() != 0 ? 1 << (image.getComps()[1].getPrec() - 1) : 0);

							int b = image.getComps()[2].getData()[pixelPosition];
							b += (image.getComps()[2].getSgnd() != 0 ? 1 << (image.getComps()[2].getPrec() - 1) : 0);

							int a = image.getComps()[3].getData()[pixelPosition];
							a += (image.getComps()[3].getSgnd() != 0 ? 1 << (image.getComps()[3].getPrec() - 1) : 0);

							dib.setRGB(x, y, new Color(r, g, b, a).getRGB());
							pixelCount++;
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "fromJ2kImage", ex);
		}
		return dib;
	}

	public BufferedImage fromByteGray(int width, int height, byte[] gray) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		byte[] array = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(gray, 0, array, 0, array.length);
		return image;
	}

	public byte[] integersToBytes(int[] data) {
		byte[] array = new byte[data.length];
		for (int index = 0; index < data.length; index++)
			array[index] = (byte) data[index];
		return array;
	}
}