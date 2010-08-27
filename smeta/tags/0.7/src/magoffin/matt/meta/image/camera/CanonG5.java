/* ===================================================================
 * CanonG5.java
 * 
 * Created Jan 23, 2007 9:11:47 AM
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
 * A Canon G5-specific implementation of {@link EXIFJpegMetadataResource}.
 * 
 * <p>INFO</p>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class CanonG5 extends Canon {

	/** The field of view crop factor from the 20D focal length to a 35mm equivilent. */
	public static final float FOCAL_EQUIV_35MM_X_FACTOR = 4.861111f;
	
	/**
	 * Construct from an InputStream.
	 * @param in the JPEG input stream
	 */
	public CanonG5(InputStream in) {
		super(in);
	}

	/**
	 * Construct from an existing EXIF {@link Metadata} instance.
	 * @param exif the EXIF metadata
	 */
	public CanonG5(Metadata exif) {
		super(exif);
	}

	@Override
	protected float getFocalLength35mmEquivFactor() {
		return FOCAL_EQUIV_35MM_X_FACTOR;
	}

}
