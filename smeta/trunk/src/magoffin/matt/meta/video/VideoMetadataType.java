/* ===================================================================
 * VideoMetadataType.java
 * 
 * Created Jan 29, 2007 10:41:40 AM
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

package magoffin.matt.meta.video;

import magoffin.matt.meta.MetadataImage;


/**
 * Metadata types for video media.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public enum VideoMetadataType {

	/** The audio format, such as codec, frequency, bits, etc. */
	AUDIO_FORMAT,
	
	/** The total length of the video, in milliseconds. */
	DURATION(Long.class),
	
	/** The total lenght of the video, in HH:MM:SS.F format. */
	DURATION_TIME,
	
	/** The frame rate of the video in frames/second. */
	FPS(Float.class),
	
	/** The height of the video, in pixels. */
	HEIGHT(Integer.class),
	
	/** A poster image from the video, as a {@link MetadataImage}. */
	POSTER(MetadataImage.class),
	
	/** The video format, such as codec, quality, etc. */
	VIDEO_FORMAT,
	
	/** The width of the video, in pixels. */
	WIDTH(Integer.class);

	private Class<?> objectType;
	
	/**
	 * Default constructor, for String types.
	 */
	private VideoMetadataType() {
		this(String.class);
	}
	
	/**
	 * Construct with a specific type.
	 * 
	 * @param objectType the object type
	 */
	private VideoMetadataType(Class<?> objectType) {
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
