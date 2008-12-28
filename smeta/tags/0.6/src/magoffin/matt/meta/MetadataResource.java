/* ===================================================================
 * MetadataResource.java
 * 
 * Created Jan 15, 2007 9:54:54 AM
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
 * Generic metadata extraction API.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public interface MetadataResource {

	/**
	 * Get a single metadata value based on an arbitrary key.
	 * 
	 * <p>Keys are implementation specific. If more than one metadata
	 * of the specified key is available, this will return the first 
	 * available one.</p>
	 * 
	 * <p>An optional Locale parameter may be provided. Some metadata
	 * values might have Locale-specific variations. The Locale 
	 * parameter may be <em>null</em> in which case the platform's 
	 * default Locale will be used.</p>
	 * 
	 * @param key the key
	 * @param locale a Locale
	 * @return the value, or <em>null</em> if not available
	 */
	Object getValue(String key, Locale locale);
	
	/**
	 * Get all available metadata values based on an arbitrary key.
	 * 
	 * <p>Keys are implementation specific.</p>
	 * 
	 * <p>An optional Locale parameter may be provided. Some metadata
	 * values might have Locale-specific variations. The Locale 
	 * parameter may be <em>null</em> in which case the platform's 
	 * default Locale will be used.</p>
	 * 
	 * @param key the key
	 * @param locale a Locale
	 * @return iterable for all values, never <em>null</em>
	 */
	Iterable<?> getValues(String key, Locale locale);
	
	/**
	 * Get an Iterable of all successfully parsed metadata keys.
	 * 
	 * <p>No sorting should be implied by the returned Iterator, but 
	 * implementations are free to order the results in any way they 
	 * see fit.</p>
	 * 
	 * @return Iterator, never null
	 */
	Iterable<String> getParsedKeys();
	
	/**
	 * Get a Map of errors that occured while parsing the metadata
	 * resource.
	 * 
	 * <p>The Map has keys based on the metadata names, and the List
	 * associated with each key holds the error message(s) that occurred
	 * while processing the metadata values for that key.</p>
	 * 
	 * <p>If no errors occurred while processing, this will return
	 * an empty Map (never null).</p>
	 * 
	 * @return error map
	 */
	Map<String, List<String>> getParseErrors();

}
