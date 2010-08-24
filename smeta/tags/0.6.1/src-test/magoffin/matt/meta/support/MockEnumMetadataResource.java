/* ===================================================================
 * MockEnumMetadataResource.java
 * 
 * Created Jan 17, 2007 4:22:53 PM
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

package magoffin.matt.meta.support;

/**
 * Mock implementation of {@link AbstractEnumMetadataResource}.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class MockEnumMetadataResource 
extends AbstractEnumMetadataResource<MockMetadataType> {
	
	/**
	 * Add test metadata value.
	 * @param key the key
	 * @param value the value
	 */
	public void setMetadataValue(MockMetadataType key, Object value) {
		setValue(key, value);
	}
	
	/**
	 * Add test errror messages.
	 * 
	 * @param key the key
	 * @param msg the message
	 */
	public void addMetadataError(MockMetadataType key, String msg) {
		addError(key, msg);
	}

}
