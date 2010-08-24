/* ===================================================================
 * ImageMetadataType.java
 * 
 * Created Jan 16, 2007 7:44:39 PM
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

package magoffin.matt.meta.image;

import java.util.Date;

/**
 * Metadata types for image media.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public enum ImageMetadataType {

	/** The aperture (APEX value) the photo was taken with. */
	APERTURE,
	
	/** A comment. */
	COMMENT,
	
	/** The aperture (F-Stop value) the photo was taken with. */
	F_STOP,
	
	/** The camera make the photo was taken with. */
	CAMERA_MAKE,
	
	/** The camera model the photo was taken with. */
	CAMERA_MODEL,
	
	/** The date the photo was taken, as a {@link Date} object. */
	DATE_TAKEN(Date.class),
	
	/** The flash setting the photo was taken with. */
	FLASH,
	
	/** The orientation of the camer the photo was taken with. */
	ORIENTATION,
	
	/** The shutter speed the photo was taken with. */
	SHUTTER_SPEED,
	
	/** A title for the image. */
	TITLE,
	
	/** The focal length the photo was taken with. */
	FOCAL_LENGTH,
	
	/** The focal length in 35mm terms the photo was taken with. */
	FOCAL_LENGTH_35MM_EQUIV,
	
	/** The GPS longitude value of the image's location. */
	GPS_LONGITUDE,
	
	/** The GPS latitude value of the image's location. */
	GPS_LATITUDE,
	
	/** The exposure bias the photo was taken with. */
	EXPOSURE_BIAS,
	
	/** The exposure time the photo was taken with. */
	EXPOSURE_TIME;

	private Class<?> objectType;
	
	/**
	 * Default constructor, for String types.
	 */
	private ImageMetadataType() {
		this(String.class);
	}
	
	/**
	 * Construct with a specific type.
	 * 
	 * @param objectType the object type
	 */
	private ImageMetadataType(Class<?> objectType) {
		this.objectType = objectType;
	}
	
	/**
	 * Get the type of object this metadata represents.
	 * @return object type
	 */
	public Class<?> getObjectType() {
		return this.objectType;
	}
	
}
