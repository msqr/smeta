/* ===================================================================
 * MP3MetadataItem.java
 * 
 * Created Jan 19, 2007 8:07:03 AM
 * 
 * Copyright (c) 2007 Matt Magoffin (spamsqr@msqr.us)
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

/**
 * API for MP3 metadta items (ID3 tags, Lyrics3 objects, etc).
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public interface MP3MetadataItem {

    /**
     * ID string that usually corresponds to the class name, but can be 
     * displayed to the user. It is not intended to identify each individual 
     * instance.
     *
     * @return ID string
     */
    public abstract String getIdentifier();

    /**
     * Get the size of the item, in bytes.
     * @return size
     */
    public abstract int getSize();
    
    /**
     * Get the {@link MP3MetadataContainer} for this item.
     * @return the metadata container
     */
    public MP3MetadataContainer getBody();
    
}
