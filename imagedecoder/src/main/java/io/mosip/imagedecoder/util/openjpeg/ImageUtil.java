package io.mosip.imagedecoder.util.openjpeg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.imagedecoder.constant.DecoderErrorCodes;
import io.mosip.imagedecoder.exceptions.DecoderException;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;

public class ImageUtil {
	private static Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);
	/** 
	 * Calculate the aspect ratio from a set of width/height params
	 * @param width
	 * @param height
	 * @return aspectRatio
	 */
	public static double calculateAspectRatio(int width, int height) {
		if (width == 0 || height == 0)
			throw new IllegalArgumentException("Cannot calculate aspect ratio from zero");
		if (width < 0 || height < 0)
			throw new IllegalArgumentException("Cannot calculate aspect ratio from negative numbers");
		if (width > height)
			return width / height;
		else
			return height / width;
	}

	/** 
	 * Calculate the compression ratio from a set of width/height/depth params
	 * @param width
	 * @param height
	 * @param depth
	 * @param compressionImageLength
	 * @return compressionRatio
	 */
	public static int calculateCompressionRatio(int width, int height, int depth, int compressionImageLength) {
		if (width == 0 || height == 0 || depth == 0 || compressionImageLength == 0)
			throw new IllegalArgumentException("Cannot calculate compression Ratio from zero");
		if (width < 0 || height < 0 || depth < 0 || compressionImageLength < 0)
			throw new IllegalArgumentException("Cannot calculate compression Ratio from negative numbers");
		
		double compressionRatio = ((width * height * depth)/compressionImageLength);
		if (compressionRatio > 100)
			compressionRatio = 100 - compressionRatio;
		return (int)Math.abs(compressionRatio);
	}
	
    public static BufferedImage fromJ2kImage(int width, int height, OpenJpegImage image) {
    	BufferedImage dib = null;

		try {
			// compute image width and height
			int wr = image.getComps()[0].getWidth();
			int wrr = MathUtil.intCeilDivPow2(image.getComps()[0].getWidth(), image.getComps()[0].getFactor());
			
			int hr = image.getComps()[0].getHeight();
			int hrr = MathUtil.intCeilDivPow2(image.getComps()[0].getHeight(), image.getComps()[0].getFactor());

			// check the number of components
			int noOfComps = image.getNoOfComps();

			boolean bIsValid = true;
			for(int c = 0; c < noOfComps - 1; c++) {
				if(	(image.getComps()[c].getDX() == image.getComps()[c+1].getDX()) && 
					(image.getComps()[c].getDY() == image.getComps()[c+1].getDY()) &&
					(image.getComps()[c].getPrec() == image.getComps()[c+1].getPrec()) ) {
					continue;
				} else {
					bIsValid = false;
					break;
				}
			}
			bIsValid &= ((noOfComps == 1) || (noOfComps == 3) || (noOfComps == 4));
			if(!bIsValid) {
				if(noOfComps != 0) {
					LOGGER.error(String.format("Warning: image contains %d greyscale components. Only the first will be loaded.", noOfComps));
					noOfComps = 1;
				} else {
					// unknown type
					throw new DecoderException (DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorCode(), DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorMessage());
				}
			}
			
			// create a new DIB
			if(image.getComps()[0].getPrec() <= 8) {
				switch(noOfComps) {
					case 1:
						dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_BYTE_GRAY);
						break;
					case 3:
						dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_INT_RGB);
						break;
					case 4:
						dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_INT_ARGB);
						break;
				}
			} else if(image.getComps()[0].getPrec() <= 16) {
				switch(noOfComps) {
					case 1:
						dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_USHORT_GRAY);
						break;
					case 3:
						dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_INT_RGB);
						break;
					case 4:
						dib = new BufferedImage(wrr, hrr, BufferedImage.TYPE_INT_ARGB);
						break;
				}
			} else {
				throw new DecoderException (DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorCode(), DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorMessage());
			}
			if(dib == null) {
				throw new DecoderException (DecoderErrorCodes.BUFFEREDIMAGE_ALLOCATION_FAILED_ERROR.getErrorCode(), DecoderErrorCodes.BUFFEREDIMAGE_ALLOCATION_FAILED_ERROR.getErrorMessage());
			}
			if(image.getComps()[0].getPrec() <= 8) {
				if(noOfComps == 1) {
					// 8-bit greyscale
					// ----------------------------------------------------------
					// load pixel data
			        byte[] array = ((DataBufferByte) dib.getRaster().getDataBuffer()).getData();
			        System.arraycopy(ImageUtil.integersToBytes (image.getComps()[0].getData()), 0, array, 0, array.length);

					/*
					int pixel_count = 0;
					for(int y = 0; y < hrr; y++) {		
						int bitsIndex = hrr - 1 - y;

						for(int x = 0; x < wrr; x++) {
							int pixel_pos = (pixel_count / wrr * wr + pixel_count % wrr);

							int index = image.getComps()[0].getData()[pixel_pos];
							index += (image.getComps()[0].getSgnd() != 0 ? 1 << (image.getComps()[0].getPrec() - 1) : 0);
							
							dib.setRGB(x, y, (byte)index);
							pixel_count++;
						}
					}
					*/
				}
				else if(noOfComps == 3) {

					// 24-bit RGB
					// ----------------------------------------------------------	
					
					// load pixel data

					int pixel_count = 0;

					for(int y = 0; y < hrr; y++) {		
						int bitsIndex = hrr - 1 - y;

						for(int x = 0; x < wrr; x++) {
							int pixel_pos = (int) (pixel_count / wrr * wr + pixel_count % wrr);

							int r = image.getComps()[0].getData()[pixel_pos];
							r += (image.getComps()[0].getSgnd() != 0 ? 1 << (image.getComps()[0].getPrec() - 1) : 0);
							
							int g = image.getComps()[1].getData()[pixel_pos];
							g += (image.getComps()[1].getSgnd() != 0 ? 1 << (image.getComps()[1].getPrec() - 1) : 0);
							
							int b = image.getComps()[2].getData()[pixel_pos];
							b += (image.getComps()[2].getSgnd() != 0 ? 1 << (image.getComps()[2].getPrec() - 1) : 0);

							dib.setRGB(x, y, new Color(r, g, b).getRGB());
							pixel_count++;
						}
					}
				}
				else if(noOfComps == 4) {

					// 32-bit RGBA
					// ----------------------------------------------------------	
					
					// load pixel data

					int pixel_count = 0;

					for(int y = 0; y < hrr; y++) {		
						int bitsIndex = hrr - 1 - y;

						for(int x = 0; x < wrr; x++) {
							int pixel_pos = (int) (pixel_count / wrr * wr + pixel_count % wrr);

							int r = image.getComps()[0].getData()[pixel_pos];
							r += (image.getComps()[0].getSgnd() != 0 ? 1 << (image.getComps()[0].getPrec() - 1) : 0);
							
							int g = image.getComps()[1].getData()[pixel_pos];
							g += (image.getComps()[1].getSgnd() != 0  ? 1 << (image.getComps()[1].getPrec() - 1) : 0);
							
							int b = image.getComps()[2].getData()[pixel_pos];
							b += (image.getComps()[2].getSgnd()  != 0 ? 1 << (image.getComps()[2].getPrec() - 1) : 0);

							int a = image.getComps()[3].getData()[pixel_pos];
							a += (image.getComps()[3].getSgnd()  != 0 ? 1 << (image.getComps()[3].getPrec() - 1) : 0);

							dib.setRGB(x, y, new Color(r, g, b, a).getRGB());
							pixel_count++;
						}
					}
				}
			}
			/* need to handle a
			else if(image.getComps()[0].getPrec() <= 16) {
				if(noOfComps == 1) {
					// 16-bit greyscale
					// ----------------------------------------------------------

					// load pixel data

					long pixel_count = 0;

					for(int y = 0; y < hrr; y++) {		
						int bitsIndex = hrr - 1 - y;

						for(int x = 0; x < wrr; x++) {
							int pixel_pos = (int) (pixel_count / wrr * wr + pixel_count % wrr);

							int index = image.getComps()[0].getData()[pixel_pos];
							index += (image.getComps()[0].getSgnd() != 0 ? 1 << (image.getComps()[0].getPrec() - 1) : 0);

							dib.setRGB(x, y, (short)index);
							pixel_count++;
						}
					}
				}
				else if(noOfComps == 3) {

					// 48-bit RGB
					// ----------------------------------------------------------	
					
					// load pixel data

					int pixel_count = 0;

					for(int y = 0; y < hrr; y++) {		
						int bitsIndex = hrr - 1 - y;

						for(int x = 0; x < wrr; x++) {
							int pixel_pos = pixel_count / wrr * wr + pixel_count % wrr;

							int r = image.getComps()[0].getData()[pixel_pos];
							r += (image.getComps()[0].getSgnd() != 0 ? 1 << (image.getComps()[0].getPrec() - 1) : 0);
							
							int g = image.getComps()[1].getData()[pixel_pos];
							g += (image.getComps()[1].getSgnd() != 0  ? 1 << (image.getComps()[1].getPrec() - 1) : 0);
							
							int b = image.getComps()[2].getData()[pixel_pos];
							b += (image.getComps()[2].getSgnd() != 0  ? 1 << (image.getComps()[2].getPrec() - 1) : 0);

							dib.setRGB(x, y, new Color(r, g, b).getRGB());

							pixel_count++;
						}
					}
				}
				else if(noOfComps == 4) {

					// 64-bit RGBA
					// ----------------------------------------------------------	
					
					// load pixel data

					int pixel_count = 0;

					for(int y = 0; y < hrr; y++) {		
						int bitsIndex = hrr - 1 - y;

						for(int x = 0; x < wrr; x++) {
							int pixel_pos = pixel_count / wrr * wr + pixel_count % wrr;

							int r = image.getComps()[0].getData()[pixel_pos];
							r += (image.getComps()[0].getSgnd() != 0 ? 1 << (image.getComps()[0].getPrec() - 1) : 0);
							
							int g = image.getComps()[1].getData()[pixel_pos];
							g += (image.getComps()[1].getSgnd() != 0  ? 1 << (image.getComps()[1].getPrec() - 1) : 0);
							
							int b = image.getComps()[2].getData()[pixel_pos];
							b += (image.getComps()[2].getSgnd()  != 0 ? 1 << (image.getComps()[2].getPrec() - 1) : 0);

							int a = image.getComps()[3].getData()[pixel_pos];
							a += (image.getComps()[3].getSgnd()  != 0 ? 1 << (image.getComps()[3].getPrec() - 1) : 0);

							dib.setRGB(x, y, new Color(r, g, b, a).getRGB());

							pixel_count++;
						}
					}
				}
			}*/
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return dib;
    }
    
    public static BufferedImage fromByteGray(int width, int height, byte[] gray) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte[] array = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(gray, 0, array, 0, array.length);
        return image;
    }

    public static byte[] integersToBytes(int[] data) {
    	byte[] array = new byte[data.length];
    	for (int index=0; index < data.length; index++)
    		array[index] = (byte) data[index];
        return array;
    }
}
