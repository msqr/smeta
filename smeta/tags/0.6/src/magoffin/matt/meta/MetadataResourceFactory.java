/* ===================================================================
 * MetadataResourceFactory.java
 * 
 * Created Jan 15, 2007 9:58:50 PM
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

import java.io.File;
import java.io.IOException;

/**
 * Factory API for creating {@link MetadataResource} instances.
 * 
 * <p>This factory API is designed so that different, related metadata standards
 * can be instantiated by a common factory. For example a single ID3 factory
 * can be implemented to return different MetadataResource implementations for
 * ID3v1, ID3v1.1, ID3v2, ID3v2.2, etc.</p>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public interface MetadataResourceFactory {

	/**
	 * Get a {@link MetadataResource} for a given file.
	 * 
	 * @param file the file
	 * @return the MetadataResource
	 * @throws IOException if an IO error occurs
	 * @throws MetadataNotSupportedException if the file is not supported for any reason
	 */
	MetadataResource getMetadataResourceInstance(File file) 
	throws IOException, MetadataNotSupportedException;
	
}
