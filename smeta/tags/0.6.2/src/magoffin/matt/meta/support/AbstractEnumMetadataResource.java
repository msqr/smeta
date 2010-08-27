/* ===================================================================
 * AbstractEnumMetadataResource.java
 * 
 * Created Jan 16, 2007 8:10:55 PM
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
import java.util.Locale;

import magoffin.matt.meta.MetadataResource;

/**
 * Abstract base {@link MetadataResource} implementation with special
 * support for an enumeration type.
 * 
 * @param <T> the enum type
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public abstract class AbstractEnumMetadataResource<T extends Enum<T>> 
extends AbstractMetadataResource {

	/**
	 * Get a single metadata value based on a type constant.
	 * 
	 * <p>{@link Enum#name()} will be used to call 
	 * {@link #getValue(String, Locale)}. If more than one 
	 * metadata of the specified type is available, this will 
	 * return the first available one.</p>
	 * 
	 * @param type the type
	 * @param locale a Locale
	 * @return the value, or <em>null</em> if not available
	 */
	public Object getValue(T type, Locale locale) {
		return getValue(type.name(), locale);
	}
	
	/**
	 * Get all available metadata values based on a type constant.
	 * 
	 * <p>{@link Enum#name()} will be used to call 
	 * {@link #getValues(String, Locale)}. If more than one 
	 * metadata of the specified type is available, this will 
	 * return the first available one.</p>
	 * 
	 * @param type the type
	 * @param locale a Locale
	 * @return iterable for all values, never <em>null</em>
	 */
	public Iterable<?> getValues(T type, Locale locale) {
		return getValues(type.name(), locale);
	}
	
	/**
	 * Set a value for a type constant.
	 * 
	 * <p>This replaces any values currently associated with the key.
	 * {@link Enum#name()} will be used to call 
	 * {@link #setValue(String, Object)}.</p>
	 * 
	 * @param type the type to use
	 * @param value the value to set
	 */
	protected final void setValue(T type, Object value) {
		setValue(type.name(), value);
	}
	
	/**
	 * Set a value for a type constant.
	 * 
	 * <p>This replaces any values currently associated with the key.
	 * {@link Enum#name()} will be used to call 
	 * {@link #setValues(String, Collection)}.</p>
	 * 
	 * @param type the type to use
	 * @param values the values to set
	 */
	protected final void setValues(T type, Collection<?> values) {
		setValues(type.name(), values);
	}
	
	/**
	 * Add a value for a type constant.
	 * 
	 * <p>This replaces any values currently associated with the key.
	 * {@link Enum#name()} will be used to call 
	 * {@link #addValue(String, Object)}.</p>
	 * 
	 * @param type the type to use
	 * @param value the value to add
	 */
	protected final void addValue(T type, Object value) {
		addValue(type.name(), value);
	}
	
	/**
	 * Set an error message for a particular type.
	 * 
	 * <p>{@link Enum#name()} will be used to call 
	 * {@link #addError(String, String)}.</p>
	 * 
	 * @param type the type to set the error message for
	 * @param msg the error message
	 */
	protected final void addError(T type, String msg) {
		addError(type.name(), msg);
	}

}
