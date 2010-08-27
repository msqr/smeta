/* ===================================================================
 * MP3Object.java
 * 
 * Created Jan 19, 2007 8:16:08 AM
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

package org.farng.mp3.object;

/**
 * API for a MP3 metadata object type.
 * 
 * <p>MP3 metadata object types includes things such as a
 * "string" type, "number" type, "lyric line" type, "image"
 * type, etc. This API defines a standard way of accessing
 * the content of each type.</p>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public interface MP3Object {
	
	/**
	 * Get the identifier for this object.
	 * @return the object ID
	 */
	public String getIdentifier();
	
	/**
	 * Get the value of this object.
	 * @return the object
	 */
	public Object getValue();

	/**
	 * Get the size of this object, in bytes.
	 * @return the size
	 */
    public int getSize();

    /**
     * Get a string representation of this object.
     * @return string
     */
    public String toString();

}
