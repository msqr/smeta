/* ===================================================================
 * LocalizablePlaceholder.java
 * 
 * Created Aug 24, 2010 4:25:05 PM
 * 
 * Copyright (c) 2010 Matt Magoffin.
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

import java.util.Locale;

/**
 * Placeholder value for a localizable value.
 * 
 * <p>This can be used to store parsed metadata values that need to be
 * localized later on.</p>
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
public class LocalizablePlaceholder {
	
	private ValueGenerator generator;
	
	/**
	 * Callback API for generating a value for a specific locale.
	 */
	public static interface ValueGenerator {
		
		/**
		 * Generate the value for a specific locale.
		 * 
		 * @param locale the locale to generate the value for
		 * @return the value
		 */
		public Object getValueForLocale(Locale locale);
		
	}
	
	/**
	 * Constructor.
	 * 
	 * @param generator the value generator
	 */
	public LocalizablePlaceholder(ValueGenerator generator) {
		super();
		this.generator = generator;
	}
	
	/**
	 * Get the value for a specific locale.
	 * 
	 * @param locale the locale to get the value for
	 * @return the value
	 */
	public Object getValue(Locale locale) {
		return generator.getValueForLocale(locale);
	}

}
