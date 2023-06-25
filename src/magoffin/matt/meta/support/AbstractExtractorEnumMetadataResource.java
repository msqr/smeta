/* ===================================================================
 * AbstractExtractorEnumMetadataResource.java
 * 
 * Created 25/06/2023 6:20:48 pm
 * 
 * Copyright (c) 2023 Matt Magoffin.
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
 */

package magoffin.matt.meta.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;

/**
 * Abstract enum-based metadata resource based on Metadata Extractor.
 *
 * @author matt
 * @version 1.0
 * @param <T>
 *        the enum type
 */
public abstract class AbstractExtractorEnumMetadataResource<T extends Enum<T>>
		extends AbstractEnumMetadataResource<T> {

	/** A date format in the form <code>yyyy:MM:dd hh:mm:ss</code>. */
	public static final String BASIC_DATE_FORMAT = "yyyy:MM:dd HH:mm:ss";

	/** A date format in the form <code>yyyy:MM:dd'T'hh:mm:ssXX</code>. */
	public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXX";

	/** A date format in the form <code>EEE, dd MM yyyy HH:mm:ss XXX</code>. */
	public static final String RFC5322_DATE_FORMAT = "EEE, dd MM yyyy HH:mm:ss XXX";

	/** A date format in the form <code>EEE dd MM HH:mm:ss XXX yyyy</code>. */
	public static final String OBS_DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

	/** An array of date formats. */
	private static final String[] DATE_FORMATS = new String[] { ISO_DATE_FORMAT, BASIC_DATE_FORMAT,
			RFC5322_DATE_FORMAT, OBS_DATE_FORMAT, };

	/** The metadata instance. */
	protected final Metadata meta;

	/**
	 * Constructor.
	 * 
	 * @param meta
	 *        the metadata
	 */
	protected AbstractExtractorEnumMetadataResource(Metadata meta) {
		super();
		this.meta = meta;
	}

	/**
	 * Get a String value from an arbitrary metadata directory.
	 * 
	 * @param dirClass
	 *        the class of the directory to extract from
	 * @param tagType
	 *        the metadata tag to get a String value for
	 * @return the String, or <em>null</em> if not found
	 */
	protected String getMetaString(Class<? extends Directory> dirClass, int tagType) {
		for ( Directory dir : meta.getDirectoriesOfType(dirClass) ) {
			if ( dir != null && dir.containsTag(tagType) ) {
				return dir.getString(tagType);
			}
		}
		return null;
	}

	/**
	 * Get an integer value from an arbitrary metadata directory.
	 * 
	 * @param dirClass
	 *        the class of the directory to extract from
	 * @param tagType
	 *        the metadata tag to get an integer value for
	 * @return the integer value, or <em>null</em> if not found
	 */
	protected Integer getMetaInteger(Class<? extends Directory> dirClass, int tagType) {
		for ( Directory dir : meta.getDirectoriesOfType(dirClass) ) {
			if ( dir != null && dir.containsTag(tagType) ) {
				try {
					return dir.getInt(tagType);
				} catch ( MetadataException e ) {
					log.warn("Metadata exception getting int type " + tagType + " from Directory type "
							+ dirClass.getName() + ": " + e.getMessage());
				}
			}
		}
		return null;
	}

	/**
	 * Get a long value from an arbitrary metadata directory.
	 * 
	 * @param dirClass
	 *        the class of the directory to extract from
	 * @param tagType
	 *        the metadata tag to get an long value for
	 * @return the integer value, or <em>null</em> if not found
	 */
	protected Long getMetaLong(Class<? extends Directory> dirClass, int tagType) {
		for ( Directory dir : meta.getDirectoriesOfType(dirClass) ) {
			if ( dir != null && dir.containsTag(tagType) ) {
				try {
					return dir.getLong(tagType);
				} catch ( MetadataException e ) {
					log.warn("Metadata exception getting long type " + tagType + " from Directory type "
							+ dirClass.getName() + ": " + e.getMessage());
				}
			}
		}
		return null;
	}

	/**
	 * Get a float value from an arbitrary metadata directory.
	 * 
	 * @param dirClass
	 *        the class of the directory to extract from
	 * @param tagType
	 *        the metadata tag to get an float value for
	 * @return the integer value, or <em>null</em> if not found
	 */
	protected Float getMetaFloat(Class<? extends Directory> dirClass, int tagType) {
		for ( Directory dir : meta.getDirectoriesOfType(dirClass) ) {
			if ( dir != null && dir.containsTag(tagType) ) {
				try {
					return dir.getFloat(tagType);
				} catch ( MetadataException e ) {
					log.warn("Metadata exception getting long type " + tagType + " from Directory type "
							+ dirClass.getName() + ": " + e.getMessage());
				}
			}
		}
		return null;
	}

	/**
	 * Extract a creation date from the metadata.
	 * 
	 * <p>
	 * If a date is found, it is parsed using the date format constants defined
	 * in this class. If a parse exception occurs, or no date is found,
	 * <em>null</em> is returned.
	 * </p>
	 * 
	 * @param dirClass
	 *        the directory class
	 * @param tagType
	 *        the tag type
	 * @return a Date, or <em>null</em> if one cannot be parsed
	 */
	protected Date getMetaDate(Class<? extends Directory> dirClass, int tagType) {
		String dStr = getMetaString(dirClass, tagType);
		if ( dStr == null ) {
			return null;
		}
		for ( String format : DATE_FORMATS ) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
				return sdf.parse(dStr);
			} catch ( ParseException e ) {
				// ignore
			}
		}
		log.warn("Could not parse date from '" + dStr + "'");
		return null;
	}

}
