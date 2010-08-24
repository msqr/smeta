/* ===================================================================
 * Canon.java
 * 
 * Created Jan 23, 2007 9:54:16 AM
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

package magoffin.matt.meta.image.camera;

import java.io.InputStream;

import com.drew.metadata.Metadata;

import magoffin.matt.meta.image.EXIFJpegMetadataResource;

/**
 * A Canon-specific, generic model, implementation of {@link EXIFJpegMetadataResource}.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class Canon extends EXIFJpegMetadataResource {

	/**
	 * Construct from an InputStream.
	 * @param in the JPEG input stream
	 */
	public Canon(InputStream in) {
		super(in);
	}

	/**
	 * Construct from an existing EXIF {@link Metadata} instance.
	 * @param exif the EXIF metadata
	 */
	public Canon(Metadata exif) {
		super(exif);
	}

}
