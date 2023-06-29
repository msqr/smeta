/* ===================================================================
 * Fujifilm.java
 * 
 * Created Jan 12, 2013 5:00:18 PM
 * 
 * Copyright (c) 2013 Matt Magoffin.
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

package magoffin.matt.meta.image.camera.fujifilm;

import java.io.InputStream;
import magoffin.matt.meta.image.EXIFJpegMetadataResource;
import com.drew.metadata.Metadata;

/**
 * A Fujifilm-specific, generic model, implementation of {@link EXIFJpegMetadataResource}.
 *
 * @author matt
 * @version $Revision$ $Date$
 */
public class Fujifilm extends EXIFJpegMetadataResource {

	/**
	 * Construct from an InputStream.
	 * @param in the JPEG input stream
	 */
	public Fujifilm(InputStream in) {
		super(in);
	}

	/**
	 * Construct from an existing EXIF {@link Metadata} instance.
	 * @param exif the EXIF metadata
	 */
	public Fujifilm(Metadata exif) {
		super(exif);
	}

}
