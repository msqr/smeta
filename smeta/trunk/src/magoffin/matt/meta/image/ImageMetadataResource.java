/* ===================================================================
 * ImageMetadataResource.java
 * 
 * Created Jan 16, 2007 7:46:01 PM
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

import java.util.Locale;

import magoffin.matt.meta.MetadataResource;

/**
 * API for image resources.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public interface ImageMetadataResource extends MetadataResource {

	/**
	 * Get a single metadata value based on a type constant.
	 * 
	 * <p>If more than one metadata of the specified type is 
	 * available, this will return the first available one.</p>
	 * 
	 * @param type the type
	 * @param locale a Locale
	 * @return the value, or <em>null</em> if not available
	 */
	Object getValue(ImageMetadataType type, Locale locale);
	
	/**
	 * Get all available metadata values based on a type constant.
	 * 
	 * @param type the type
	 * @param locale a Locale
	 * @return iterable for all values, never <em>null</em>
	 */
	Iterable<?> getValues(ImageMetadataType type, Locale locale);
	
}
