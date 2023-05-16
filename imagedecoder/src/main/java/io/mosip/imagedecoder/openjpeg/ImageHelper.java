package io.mosip.imagedecoder.openjpeg;

import io.mosip.imagedecoder.model.openjpeg.Jp2ColorSpace;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponent;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponentParameters;

public class ImageHelper {
	// Static variable reference of singleInstance of type Singleton
    private static ImageHelper singleInstance = null;    
    private ImageHelper()
	{ 
		super ();
	} 
  
	//synchronized method to control simultaneous access 
	public static synchronized ImageHelper getInstance()
	{ 
		if (singleInstance == null)
			singleInstance = new ImageHelper();
  
        return singleInstance;
	}
	
	public OpenJpegImage imageCreateBasic() {
		OpenJpegImage image = new OpenJpegImage();
		return image;
	}

	public OpenJpegImage imageCreate(int numcmpts, OpenJpegImageComponentParameters[] cmptparms, Jp2ColorSpace clrspc) {
		int compno;
		OpenJpegImage image = imageCreateBasic();
		if(image != null) {
			image.setColorSpace(clrspc);
			image.setNoOfComps(numcmpts);
			/* allocate memory for the per-component information */
			image.setComps(new OpenJpegImageComponent[image.getNoOfComps()]);
			/* create the individual image components */
			for(compno = 0; compno < numcmpts; compno++) {
				image.getComps()[compno] = new OpenJpegImageComponent();
				OpenJpegImageComponent comp = image.getComps()[compno];
				comp.setDX(cmptparms[compno].getDx());
				comp.setDY(cmptparms[compno].getDy());
				comp.setWidth(cmptparms[compno].getWidth());
				comp.setHeight(cmptparms[compno].getHeight());
				comp.setX0(cmptparms[compno].getX0());
				comp.setY0(cmptparms[compno].getY0());
				comp.setPrec(cmptparms[compno].getPrec());
				comp.setBpp(cmptparms[compno].getBpp());
				comp.setSgnd(cmptparms[compno].getSgnd());
				comp.setData (new int[comp.getWidth() * comp.getHeight()]); 
			}
		}

		return image;
	}

	public void imageDestroy(OpenJpegImage image) {
		int i;
		if(image != null) {
			if(image.getComps() != null && image.getComps().length > 0) {
				/* image components */
				for(i = 0; i < image.getNoOfComps(); i++) {
					OpenJpegImageComponent imageComp = image.getComps()[i];
					if(imageComp.getData() != null && imageComp.getData().length > 0) {
						imageComp.setData(null);
					}
				}
				image.setComps(null);
			}
			image = null;
		}
	}
}
