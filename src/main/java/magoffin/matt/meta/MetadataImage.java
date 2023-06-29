/* ===================================================================
 * MetadataImage.java
 * 
 * Created Jan 15, 2007 10:44:31 AM
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

package magoffin.matt.meta;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Metadata type for an image.
 * 
 * <p>This metadata type allows for special handling of embedded 
 * images, i.e. images that are metadata for other media. This includes
 * such metadata as album art in ID3 tags or thumbnail images in EXIF
 * data, etc.</p>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public interface MetadataImage {
	
	/**
	 * Get the image MIME type.
	 * 
	 * @return mime type
	 */
	String getMimeType();

	/**
	 * Get the image as a {@link BufferedImage}.
	 * 
	 * <p>Implementations are not required to support this method, 
	 * and if not supported should throw a
	 * {@link UnsupportedOperationException}.</p>
	 * 
	 * @return the BufferedImage
	 */
	BufferedImage getAsBufferedImage();
	
	/**
	 * Write the image data to an output stream.
	 * 
	 * <p>This method will <em>not</em> flush or close the output stream.</p>
	 * 
	 * @param out the output stream
	 * @throws IOException if an IO error occurs
	 */
	void writeToStream(OutputStream out) throws IOException;
	
}
