/* ===================================================================
 * BasicMetadataImage.java
 * 
 * Created Jan 20, 2007 7:41:23 PM
 * 
 * Copyright (c) 2007 Matt Magoffin (spamsqr@msqr.us)
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA
 * ===================================================================
 * $Id$
 * ===================================================================
 */

package magoffin.matt.meta.support;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import magoffin.matt.meta.MetadataConfigurationException;
import magoffin.matt.meta.MetadataImage;

/**
 * Basic implementation of {@link MetadataImage}.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class BasicMetadataImage implements MetadataImage {
	
	private String mimeType;
	private byte[] imageData;
	private BufferedImage bufferedImage;
	
	/**
	 * Construct from a MIME type and byte array of image data.
	 * 
	 * @param mimeType the MIME type
	 * @param imageData the image data
	 */
	public BasicMetadataImage(String mimeType, byte[] imageData) {
		this.mimeType = mimeType;
		this.imageData = imageData;
	}
	
	/**
	 * Construct from an {@link Image}.
	 * @param image the image
	 */
	public BasicMetadataImage(Image image) {
		this.bufferedImage = getBufferedImage(image);
	}

	/**
	 * Construct from an {@link BufferedImage}.
	 * @param image the image
	 */
	public BasicMetadataImage(BufferedImage image) {
		this.bufferedImage = image;
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataImage#getAsBufferedImage()
	 */
	public BufferedImage getAsBufferedImage() {
		if ( this.bufferedImage != null ) {
			return bufferedImage;
		}
		ImageReader reader = getImageReader();
		if ( reader == null ) {
			throw new UnsupportedOperationException("MIME type ["
					+mimeType +"] not supported");
		}
		ImageReadParam param = reader.getDefaultReadParam();
		try {
			return reader.read(0, param);
		} catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	private BufferedImage getBufferedImage(Image image) {
		
		if (image instanceof BufferedImage) {
			return (BufferedImage)image;
		}
		
		// ASSUME ALL PIXELS IN IMAGE ARE LOADED

		// Determine if the image has transparent pixels; for this method's
		// implementation, see e665 Determining If an Image Has Transparent Pixels
		boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the screen
		BufferedImage bimage = null;

		// Create a buffered image using the default color model
		int type = BufferedImage.TYPE_INT_RGB;
		
		if (hasAlpha) {
			type = BufferedImage.TYPE_INT_ARGB;
		}
		bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);


		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}
	
	private boolean hasAlpha(Image image) {
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage)image;
			return bimage.getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			// ignore
		}

		// Get the image's color model
		ColorModel cm = pg.getColorModel();
		return (cm != null ? cm.hasAlpha() : false);
	}

	/**
	 * Get an ImageReader for the given InputStream for the given MIME type.
	 * 
	 * <p>This method will create an ImageInputStream from the <code>imageData</code>
	 * byte array in this instance.</p>
	 * 
	 * @return an ImageReader set to the InputStream <em>in</em>, or <em>null</em>
	 * if MIME type not supported
	 */
	protected ImageReader getImageReader() {
		Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(mimeType);
		if ( !readers.hasNext() ) {
			return null;
		}
		ImageReader reader = readers.next();
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(
				new ByteArrayInputStream(imageData));
			reader.setInput(iis);
			return reader;
		} catch ( IOException e ) {
			throw new RuntimeException("IOException reading image input stream for [" 
					+mimeType +"]",e);
		}
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataImage#getMimeType()
	 */
	public String getMimeType() {
		return mimeType == null ? "image/png" : mimeType;
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataImage#writeToStream(java.io.OutputStream)
	 */
	public void writeToStream(OutputStream out) throws IOException {
		if ( bufferedImage == null ) {
			out.write(imageData);
			return;
		}
		// write as PNG image
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/png");
		if ( !writers.hasNext() ) {
			throw new MetadataConfigurationException("No ImageWriter for image/png MIME available");
		}
		ImageWriter writer = writers.next();
		ImageOutputStream ios = ImageIO.createImageOutputStream(out);
		writer.setOutput(ios);
		writer.write(bufferedImage);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetadataImage{" 
			+(mimeType == null ? "[image/png]" : mimeType) +"; " 
			+(imageData == null 
					? (bufferedImage == null 
							? "0" 
							: bufferedImage.getWidth() +"x" +bufferedImage.getHeight())
					: String.valueOf(imageData.length)) 
			+"}";
	}

}
