/* ===================================================================
 * MockMetadataResourceFactory.java
 * 
 * Created Jan 16, 2007 8:11:27 AM
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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Mock implementation of {@link MetadataResourceFactory}.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class MockMetadataResourceFactory implements MetadataResourceFactory {
	
	private Set<String> supportedKeys = new LinkedHashSet<String>();

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataResourceFactory#getMetadataResourceInstance(java.io.File)
	 */
	public MetadataResource getMetadataResourceInstance(File file) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return the supportedKeys
	 */
	public Set<String> getSupportedKeys() {
		return supportedKeys;
	}
	
	/**
	 * @param supportedKeys the supportedKeys to set
	 */
	public void setSupportedKeys(Set<String> supportedKeys) {
		this.supportedKeys = supportedKeys;
	}

}
