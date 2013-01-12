/* ===================================================================
 * MP3MetadataContainer.java
 * 
 * Created Jan 19, 2007 8:14:17 AM
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * ===================================================================
 * $Id$
 * ===================================================================
 */

package org.farng.mp3;

import java.util.Iterator;

import org.farng.mp3.object.MP3Object;

/**
 * A container of MP3 metadata items.
 * 
 * <p>This container represents a single metadata item, which may contain
 * several {@link MP3Object} instances, each with an ID. For example a
 * picture container might contain an MP3Object for the picture's MIME 
 * type as well as a MP3Object for the picture data itself.</p>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public interface MP3MetadataContainer {

	/**
	 * Get an object value by its ID.
	 * 
	 * @param identifier the ID of the object to get
	 * @return the object, or <em>null</em> if not found
	 */
	public Object getObject(String identifier);
	
	/**
	 * Get an iterator over all MP3Object instances available.
	 * @return iterator
	 */
	public Iterator<MP3Object> getObjectListIterator();
}
