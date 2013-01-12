/* ===================================================================
 * MockMetadataResource.java
 * 
 * Created Jan 16, 2007 8:14:00 AM
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

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Mock implementation of {@link MetadataResource}.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class MockMetadataResource implements MetadataResource {

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataResource#getValue(java.lang.String, java.util.Locale)
	 */
	public Object getValue(String key, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataResource#getValues(java.lang.String, java.util.Locale)
	 */
	public Iterable<?> getValues(String key, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataResource#getParseErrors()
	 */
	public Map<String, List<String>> getParseErrors() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataResource#getParsedKeys()
	 */
	public Iterable<String> getParsedKeys() {
		// TODO Auto-generated method stub
		return null;
	}

}
