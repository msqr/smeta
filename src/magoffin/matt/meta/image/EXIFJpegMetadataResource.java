/* ===================================================================
 * EXIFJpegMetadataResource.java
 * 
 * Created Jan 16, 2007 8:36:47 PM
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

package magoffin.matt.meta.image;

import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.support.AbstractEnumMetadataResource;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.GpsDirectory;

/**
 * {@link MetadataResource} implementation for JPEG+EXIF resources.
 * 
 * <p>This implementation relies on Metadata Extractor, available from
 * http://drewnoakes.com/code/exif/</p>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class EXIFJpegMetadataResource 
extends AbstractEnumMetadataResource<ImageMetadataType>
implements MetadataResource {
	
	/** A date format in the form <code>yyyy:MM:dd hh:mm:ss</code>. */
	public static final String EXIF_BASIC_DATE_FORMAT = "yyyy:MM:dd HH:mm:ss";
	
	private Metadata exif;
	
	/**
	 * Construct from an InputStream.
	 * @param in the JPEG input stream
	 */
	public EXIFJpegMetadataResource(InputStream in) {
		try {
			exif = JpegMetadataReader.readMetadata(in);
		} catch ( JpegProcessingException e ) {
			throw new RuntimeException(e);
		}
		parseExif();
	}
	
	/**
	 * Construct from an existing EXIF {@link Metadata} instance.
	 * @param exif the EXIF metadata
	 */
	public EXIFJpegMetadataResource(Metadata exif) {
		this.exif = exif;
		parseExif();
	}
	
	/**
	 * Parse supported EXIF data from the {@link Metadata} instance
	 * associated with this object.
	 * 
	 * <p>Extending classes might want to override this behavior.</p>
	 */
	protected void parseExif() {
		setValue(ImageMetadataType.APERTURE,extractAperture());
		setValue(ImageMetadataType.CAMERA_MAKE,extractCameraMake());
		setValue(ImageMetadataType.CAMERA_MODEL,extractCameraModel());
		setValue(ImageMetadataType.DATE_TAKEN, extractCreationDate());
		setValue(ImageMetadataType.EXPOSURE_BIAS,extractExposureBias());
		setValue(ImageMetadataType.EXPOSURE_TIME,extractExposureTime());
		setValue(ImageMetadataType.F_STOP,extractFstop());
		setValue(ImageMetadataType.FLASH,extractFlash());
		setValue(ImageMetadataType.FOCAL_LENGTH,extractFocalLength());
		setValue(ImageMetadataType.FOCAL_LENGTH_35MM_EQUIV,extractFocalLength35mmEquiv());
		setValue(ImageMetadataType.ORIENTATION,extractOrientation());
		setValue(ImageMetadataType.SHUTTER_SPEED,extractShutterSpeed());
		setValue(ImageMetadataType.GPS_LATITUDE,extractGpsLatitude());
		setValue(ImageMetadataType.GPS_LONGITUDE,extractGpsLongitude());
	}
	
	/**
	 * Extract the aperture (APEX value).
	 * @return aperture
	 */
	protected String extractAperture() {
		Rational aperture = getExifRational(ExifDirectory.TAG_APERTURE);
		if (aperture != null) {
			return String.valueOf(aperture.floatValue());
		}
		return null;
	}
	
	/**
	 * Extract the aperture as an F-Stop value.
	 * @return f-stop
	 */
	protected String extractFstop() {
		Rational aperture = getExifRational(ExifDirectory.TAG_APERTURE);
		if ( aperture == null || aperture.floatValue() < 1 ) {
			return null;
		}
		return "F" +roundDecimal(Math.pow(1.4142,aperture.floatValue()),1);

	}

	/**
	 * Extract the camera make.
	 * @return camera make
	 */
	protected String extractCameraMake() {
		return getExifString(ExifDirectory.TAG_MAKE);
	}
	
	/**
	 * Extract the cameral model.
	 * @return camera model
	 */
	protected String extractCameraModel() {
		return getExifString(ExifDirectory.TAG_MODEL);
	}
	
	/**
	 * Extract the exposure bias, as an integer string.
	 * @return the exposure bias
	 */
	protected String extractExposureBias() {
		Rational exposureBias = getExifRational(ExifDirectory.TAG_EXPOSURE_BIAS);
		if (exposureBias != null) {
			return String.valueOf(exposureBias.intValue());
		}
		return null;
	}

	/**
	 * Extract boolean FALSE if flash did not fire, TRUE otherwise.
	 * @return boolean string
	 */
	protected String extractFlash() {
		int flash = getExifInt(ExifDirectory.TAG_FLASH);
		if ( flash == 0 ) {
			return null; // don't return value unless flash fired
		}
		return Boolean.TRUE.toString();
	}
	
	/**
	 * Extract the focal length.
	 * @return focal length
	 */
	protected String extractFocalLength() {
		Rational focalLength = getExifRational(ExifDirectory.TAG_FOCAL_LENGTH);
		if (focalLength != null) {
			return String.valueOf(focalLength.floatValue());
		}
		return null;
	}
	
	/**
	 * Extract the focal length in terms of 35mm film.
	 * @return focal length
	 */
	protected String extractFocalLength35mmEquiv() {
		if ( getFocalLength35mmEquivFactor() != 1.0f ) {
			Rational focalLength = getExifRational(ExifDirectory.TAG_FOCAL_LENGTH);
			if ( focalLength != null ) {
				float fl = focalLength.floatValue();
				int equiv = Math.round(fl*getFocalLength35mmEquivFactor());
				return String.valueOf(equiv);
			}
		}
		int length = getExifInt(ExifDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH);
		if ( length > 0 ) {
			return String.valueOf(length);
		}
		return null;
	}
	
	/**
	 * Extract the orientation (as an integer string).
	 * @return orientation
	 */
	protected String extractOrientation() {
		int o = getExifInt(ExifDirectory.TAG_ORIENTATION);
		if ( o >= 0 ) {
			return String.valueOf(o);
		}
		return null;
	}
	
	/**
	 * Extract a creation date from the EXIF.
	 * 
	 * <p>This method attempts to find the creation date for the image
	 * by taking the first value available from:</p>
	 * 
	 * <ol>
	 *   <li>ExifDirectory.TAG_DATETIME_ORIGINAL</li>
	 *   <li>ExifDirectory.TAG_DATETIME_DIGITIZED</li>
	 *   <li>ExifDirectory.TAG_DATETIME</li>
	 * </ol>
	 * 
	 * <p>If a date is found, it is parsed using the date format
	 * {@link #EXIF_BASIC_DATE_FORMAT}. If a parse 
	 * exception occurs, or no date is found, <em>null</em> is returned.</p>
	 * 
	 * @return a Date
	 */
	protected Date extractCreationDate() {
		String dStr = this.getExifString(ExifDirectory.TAG_DATETIME_ORIGINAL);
		if (dStr == null) {
			// try digi date
			dStr = this.getExifString(ExifDirectory.TAG_DATETIME_DIGITIZED);
		}
		if (dStr == null) {
			// try mod date/time as last resort
			dStr = this.getExifString(ExifDirectory.TAG_DATETIME);
		}
		if (dStr == null) {
			return null;
		}
		try {
			return new SimpleDateFormat(EXIF_BASIC_DATE_FORMAT).parse(dStr);
		} catch (ParseException e) {
			log.warn("Could not parse date from '" + dStr + "': "
					+ e.getMessage());
			return null;
		}
	}
	
	/**
	 * Extract the exposure time as a String value.
	 * 
	 * <p>This method attempts to format the exposure Rational in a 
	 * sensible way.</p>
	 * 
	 * @return the exposure string
	 */
	protected String extractExposureTime() {
		Rational exposure = getExifRational(ExifDirectory.TAG_EXPOSURE_TIME);
		if (exposure != null) {
			if (exposure.getNumerator() > exposure.getDenominator()) {
				// longer than 1 second, so perform division
				double speed = exposure.doubleValue();
				return roundDecimal(speed, 1);
			}
			// less than 1 second, so leave as fraction
			return exposure.toSimpleString(false);
		}
		return null;
	}
	
	/**
	 * Extract the shutter speed as a String value.
	 * 
	 * <p>This method attempts to format the shutter speed Rational in a 
	 * sensible way.</p>
	 * 
	 * @return the shutter speed string
	 */
	protected String extractShutterSpeed() {
		Rational shutter = getExifRational(ExifDirectory.TAG_SHUTTER_SPEED);
		if (shutter != null) {
			if (shutter.getNumerator() < shutter.getDenominator()) {
				// longer than 1 second, so perform division to get simple
				// result
				double speed = 1.0 / Math.pow(2.0, shutter.doubleValue());
				return roundDecimal(speed,1);
			}
			// less than 1 second, so leave as fraction
			long fracSpeed = Math.round(Math.pow(2.0, shutter.doubleValue()));
			return "1/" + fracSpeed;
		}
		return null;
	}
	
	/**
	 * Extract the GPS longitude as a String value.
	 * 
	 * <p>This method combines the longitude reference with the longitude value.</p>
	 * 
	 * @return the GPS longitude string
	 */
	protected String extractGpsLongitude() {
		Directory dir = this.exif.getDirectory(GpsDirectory.class);
		if ( dir == null || !dir.containsTag(GpsDirectory.TAG_GPS_LONGITUDE)
			|| !dir.containsTag(GpsDirectory.TAG_GPS_LONGITUDE_REF) ) {
			return null;
		}
		try {
			String val = dir.getDescription(GpsDirectory.TAG_GPS_LONGITUDE);
			String ref = dir.getDescription(GpsDirectory.TAG_GPS_LONGITUDE_REF);
			if ( ref != null && val != null ) {
				return ref +' ' +val;
			}
		} catch (MetadataException e) {
			log.warn("Metadata exception getting Exif GPS longitude: " 
					+ e.getMessage());
		}
		return null;
	}
	
	/**
	 * Extract the GPS latitude as a String value.
	 * 
	 * <p>This method combines the latitude reference with the latitude value.</p>
	 * 
	 * @return the GPS latitude string
	 */
	protected String extractGpsLatitude() {
		Directory dir = this.exif.getDirectory(GpsDirectory.class);
		if ( dir == null || !dir.containsTag(GpsDirectory.TAG_GPS_LATITUDE)
			|| !dir.containsTag(GpsDirectory.TAG_GPS_LATITUDE_REF) ) {
			return null;
		}
		try {
			String val = dir.getDescription(GpsDirectory.TAG_GPS_LATITUDE);
			String ref = dir.getDescription(GpsDirectory.TAG_GPS_LATITUDE_REF);
			if ( ref != null && val != null ) {
				return ref +' ' +val;
			}
		} catch (MetadataException e) {
			log.warn("Metadata exception getting Exif GPS latitude: " 
					+ e.getMessage());
		}
		return null;
	}
	
	/**
	 * Round a decimal to a number of places.
	 * 
	 * @param decimal the number to round
	 * @param precision the maximum number of fraction digits
	 * @return the rounded number, or <kbd>decimal</kbd> if an error occurs
	 */
	protected String roundDecimal(double decimal, int precision) {
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(precision);
		try {
			return format.format(decimal);
		} catch ( Exception e ) {
			log.warn("Unable to round decimal " +decimal +": " +e.toString());
			return String.valueOf(decimal);
		}
	}

	/**
	 * Get an EXIF integer value.
	 * @param tagType the EXIF tag to get an integer value for
	 * @return the integer value, or <code>-1</code> if not found
	 */
	protected int getExifInt(int tagType) {
		return getMetaInt(ExifDirectory.class, tagType);
	}

	/**
	 * Get a EXIF Rational value.
	 * @param tagType the EXIF tag to get a Rational value for
	 * @return the Rational, or <em>null</em> if not found or an exception occurs
	 */
	protected Rational getExifRational(int tagType) {
		return getMetaRational(ExifDirectory.class, tagType);
	}

	/**
	 * Get a EXIF String value.
	 * @param tagType the EXIF tag to get a String value for
	 * @return the String, or <em>null</em> if not found or an exception occurs
	 */
	protected String getExifString(int tagType) {
		return getMetaString(ExifDirectory.class, tagType);
	}
	
	/**
	 * Get an EXIF integer value from an arbitrary EXIF directory.
	 * 
	 * @param dirClass the class of the directory to extract from
	 * @param tagType the EXIF tag to get an integer value for
	 * @return the integer value, or <code>-1</code> if not found
	 */
	protected int getMetaInt(Class<? extends Directory> dirClass, int tagType) {
		Directory dir = this.exif.getDirectory(dirClass);
		if (dir == null || !dir.containsTag(tagType)) {
			return -1;
		}
		try {
			return dir.getInt(tagType);
		} catch (MetadataException e) {
			log.warn("Metadata exception getting Exif int type " + tagType
					+" from Directory type " +dirClass.getName()
					+ ": " + e.getMessage());
		}
		return -1;
	}

	/**
	 * Get a EXIF Rational value from an arbitrary EXIF directory.
	 * 
	 * @param dirClass the class of the directory to extract from
	 * @param tagType the EXIF tag to get a Rational value for
	 * @return the Rational, or <em>null</em> if not found or an exception occurs
	 */
	protected Rational getMetaRational(Class<? extends Directory> dirClass, int tagType) {
		Directory dir = this.exif.getDirectory(dirClass);
		if (dir == null || !dir.containsTag(tagType)) {
			return null;
		}
		try {
			return dir.getRational(tagType);
		} catch (MetadataException e) {
			log.warn("Metadata exception getting Exif rational type " +tagType
					+" from Directory type " +dirClass.getName()
					+ ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * Get a metadata String value from an arbitrary EXIF directory.
	 * 
	 * @param dirClass the class of the directory to extract from
	 * @param tagType the EXIF tag to get a String value for
	 * @return the String, or <em>null</em> if not found
	 */
	protected String getMetaString(Class<? extends Directory> dirClass, int tagType) {
		Directory dir = this.exif.getDirectory(dirClass);
		if (dir == null || !dir.containsTag(tagType)) {
			return null;
		}
		return dir.getString(tagType);
	}
	
	/**
	 * Get a multiplication factor to convert the EXIF focal length into
	 * a 35mm equivalent length, for cameras that work this way.
	 * 
	 * <p>This implementation returns <code>1</code>, i.e. no multiplication 
	 * factor. Extending classes can override this to provide a camera-specific
	 * value.</p>
	 * 
	 * @return the focalLength35mmEquivFactor
	 */
	protected float getFocalLength35mmEquivFactor() {
		return 1.0f;
	}
	
}
