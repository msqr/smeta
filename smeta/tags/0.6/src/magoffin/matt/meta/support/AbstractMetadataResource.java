/* ===================================================================
 * AbstractMetadataResource.java
 * 
 * Created Jan 16, 2007 4:13:36 PM
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import magoffin.matt.meta.MetadataResource;

/**
 * Abstract base implementation of {@link MetadataResource} to help
 * make implementing this API easier.
 * 
 * <p>This class keeps a Map of List objects. Values can be added to the
 * List instances by calling {@link #setValue(String, Object)} or 
 * {@link #setValues(String, Collection)}. The {@link #getValue(String,Locale)}
 * method will return the first available value for the given key. 
 * The {@link #getValues(String,Locale)} will return an unmodifiable 
 * {@link List} of all values (or an empty List if no values are available
 * for that key).</p>
 * 
 * <p>The {@link Locale} parameters in {@link #getValue(String, Locale)} and
 * {@link #getValues(String, Locale)} are not used by this class. Extending
 * implementations can choose to support this as needed.</p>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public abstract class AbstractMetadataResource implements MetadataResource {
	
	private Map<String, List<Object>> metaMap = new LinkedHashMap<String, List<Object>>();
	private Map<String, List<String>> errors = new LinkedHashMap<String, List<String>>();
	
	/** A class-level logger. */
	protected final Logger log = Logger.getLogger(getClass());


	/**
	 * Set a value for a key.
	 * 
	 * <p>This replaces any values currently associated with the key.</p>
	 * 
	 * @param key the key to use
	 * @param value the value to set
	 */
	protected final void setValue(String key, Object value) {
		clearValues(key);
		addValue(key, value);
	}
	
	/**
	 * Set a collection of values for a key.
	 * 
	 * <p>This replaces any values currently associated with the key.</p>
	 * 
	 * @param key the key to use
	 * @param values the values to set
	 */
	protected final void setValues(String key, Collection<?> values) {
		clearValues(key);
		for ( Object obj : values ) {
			addValue(key, obj);
		}
	}
	
	/**
	 * Add a value to a key.
	 * 
	 * <p>Null values will not be added. Empty String values will 
	 * not be added, either.</p>
	 * 
	 * @param key the key to add the value to
	 * @param value the value to add
	 */
	protected final void addValue(String key, Object value) {
		if ( value == null || ((value instanceof String) 
				&& ((String)value).length() < 1) ) {
			return;
		}
		if ( !metaMap.containsKey(key) ) {
			metaMap.put(key, new LinkedList<Object>());
		}
		metaMap.get(key).add(value);
	}
	
	/**
	 * Clear all values for a key.
	 * 
	 * @param key the key to clear
	 */
	protected final void clearValues(String key) {
		if ( metaMap.containsKey(key) ) {
			metaMap.get(key).clear();
		}
	}
	
	/**
	 * Get the List associated with a key.
	 * 
	 * <p>If a non-null List is returned, this is a "live" list of values
	 * associated with this key, and changes made to the List will affect
	 * what is stored with this key.</p>
	 * 
	 * @param key the key
	 * @return the List associated with this key (may be <em>null</em>)
	 */
	protected final List<Object> getValueList(String key) {
		return metaMap.get(key);
	}
	
	/**
	 * Set an error message for a particular key.
	 * 
	 * <p>When an error is encountered while parsing a particular metadata
	 * value, an error message can be added for that key</p>
	 * 
	 * @param key the key to set the error message for
	 * @param msg the error message
	 */
	protected final void addError(String key, String msg) {
		if ( !errors.containsKey(key) ) {
			errors.put(key, new LinkedList<String>());
		}
		errors.get(key).add(msg);
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataResource#getValue(java.lang.String, java.util.Locale)
	 */
	public Object getValue(String key, Locale locale) {
		List<Object> values = getValueList(key);
		if ( values == null || values.size() < 1 ) {
			return null;
		}
		return values.get(0);
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataResource#getValues(java.lang.String, java.util.Locale)
	 */
	public Iterable<?> getValues(String key, Locale locale) {
		List<Object> values = getValueList(key);
		if ( values == null ) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(values);
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataResource#getParseErrors()
	 */
	public Map<String, List<String>> getParseErrors() {
		if ( errors.size() < 1 ) {
			return Collections.emptyMap();
		}
		// return a read-only copy
		Map<String, List<String>> copy = new LinkedHashMap<String, List<String>>();
		for ( Map.Entry<String, List<String>> me : errors.entrySet() ) {
			copy.put(me.getKey(), Collections.unmodifiableList(me.getValue()));
		}
		return Collections.unmodifiableMap(copy);
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataResource#getParsedKeys()
	 */
	public Iterable<String> getParsedKeys() {
		return Collections.unmodifiableSet(metaMap.keySet());
	}


}
