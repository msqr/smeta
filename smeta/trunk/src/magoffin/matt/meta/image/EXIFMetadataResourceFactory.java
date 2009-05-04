/* ===================================================================
 * EXIFMetadataResourceFactory.java
 * 
 * Created Jan 16, 2007 8:35:22 PM
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import magoffin.matt.meta.MetadataConfigurationException;
import magoffin.matt.meta.MetadataNotSupportedException;
import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.MetadataResourceFactory;

import org.apache.log4j.Logger;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;

/**
 * {@link MetadataResourceFactory} for EXIF metadata, eg. JPEG images.
 * 
 * <p>This class manages finding camera-specific {@link MetadataResource} 
 * implementations for EXIF-based images based on a classpath {@link Properties} 
 * resource. This class will search in the following locations:</p>
 * 
 * <ol>
 *   <li>smeta.properties</li>
 *   <li>META-INF/smeta.properties</li>
 * </ol>
 * 
 * <p>The properties are searched until the first suitable key is found. The 
 * properties resource is loaded via the same ClassLoader that loaded this class, i.e.
 * <code>getClass().getClassLoader().getResource()</code> is used. The properties 
 * file must have the format</p>
 * 
 * <pre>smeta.camera.MAKE.MODEL = CLASS</pre>
 * 
 * <p>where <code>MAKE</code> is an EXIF camera make (spaces removed),  
 * <code>MODEL</code> is an EXIF camera model (spaces removed), and 
 * <code>CLASS</code> is a fully-qualified class name that implements 
 * {@link MetadataResource}. The {@link MetadataResource} implementation 
 * must provide a constructor that takes a single {@link Metadata} object
 * as a parameter, which will be invoked by this factory. The class will 
 * be loaded via the same ClassLoader that loaded this class, i.e.
 * <code>getClass().getClassLoader().loadClass()</code> is used.</p>
 * 
 * <p>The <code>MODEL</code> may also be specified as a wildcard by 
 * using a <code>*</code> value. In this case, if a specific model match 
 * is not found, the wildcard value will be used.</p>
 * 
 * <p>An example of the property format looks like this:</p>
 * 
 * <pre>smeta.camera.canon.canon20d=meta.image.camera.Canon20D
 * smeta.camera.canon.canonpowershotg5=meta.image.camera.CanonG5
 * smeta.camera.canon.*=meta.image.camera.Canon</pre>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class EXIFMetadataResourceFactory implements MetadataResourceFactory {
	
	/** A custom class-path properties file. */
	public static final String CUSTOM_CAMERA_PROPERTIES = "smeta.properties";
	
	/** The default class-path properties file. */
	public static final String DEFAULT_CAMERA_PROPERTIES = "META-INF/smeta.properties";
	
	/** The prefix used for {@link MetadataResource} configuration properties. */
	public static final String CAMERA_PROPERTY_KEY_PREFIX = "smeta.camera.";
	
	/** Special key for the <kbd>makeModelMap</kbd> to match any camera model. */
	public static final String MATCH_ANY_MODEL = "*";

	private Map<String,Map<String,Class<? extends MetadataResource>>> makeModelMap 
		= new LinkedHashMap<String, Map<String,Class<? extends MetadataResource>>>();

	private final Logger log = Logger.getLogger(EXIFMetadataResourceFactory.class);
	
	
	/**
	 * Default constructor.
	 */
	public EXIFMetadataResourceFactory() {
		super();
		setupCameraMapping();
	}

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataResourceFactory#getMetadataResourceInstance(java.io.File)
	 */
	public MetadataResource getMetadataResourceInstance(File file)
			throws IOException, MetadataNotSupportedException {
		RandomAccessFile rFile = new RandomAccessFile(file, "r");
		try {
			if ( isValidJpegHeaderBytes(rFile) ) {
				InputStream in = new BufferedInputStream(
						new FileInputStream(file));
				try {
					return getJpegMetadataResource(in);
				} finally {
					try {
						in.close();
					} catch ( IOException e ) {
						log.warn("Unable to close input stream: " +e);
					}
				}
			}
		} finally {
			rFile.close();
		}
		throw new MetadataNotSupportedException(file, this);
	}

	/**
	 * Helper method that validates the JPEG (JFIF) file's magic number.
	 * 
	 * <p>Adapted from Metadata Extractor (http://drewnoakes.com/code/exif/).</p>
	 * 
	 * @return true if the magic number is JPEG (0xFFD8)
	 * @throws IOException for any problem in reading the file
	 */
	private boolean isValidJpegHeaderBytes(RandomAccessFile file) throws IOException {
		byte[] header = new byte[2];
		file.read(header, 0, 2);
		return (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8;
	}
	
	private void setupCameraMapping() {
		String[] searchPaths = new String[] {DEFAULT_CAMERA_PROPERTIES, 
				CUSTOM_CAMERA_PROPERTIES };
		for ( String aPath : searchPaths ) {
			try {
				setupCameraConfiguration(aPath);
			} catch ( MetadataConfigurationException e ) {
				if ( log.isDebugEnabled() ) {
					log.debug("Unable to use configuration path ["
							+aPath +"]: " +e.getMessage());
				}
			}
		}
	}
	
	private void setupCameraConfiguration(String propPath) {
		URL resource = getClass().getClassLoader().getResource(propPath);
		if ( resource == null ) {
			if ( log.isDebugEnabled() ) {
				log.debug("Camera properties [" +propPath +"] not available");
			}
			return;
		}
		Properties properties = new Properties();
		try {
			properties.load(resource.openStream());
		} catch ( IOException e ) {
			throw new MetadataConfigurationException(
					"Unable to load factory configuration properties [" 
					+propPath +"]", e);
		}
		
		// look at all keys for required prefix
		for ( Object key :  properties.keySet() ) {
			if ( key == null ) continue;
			String propKey = key.toString();
			if ( !propKey.startsWith(CAMERA_PROPERTY_KEY_PREFIX) ) continue;
			int makeModelDelimIdx = propKey.indexOf('.', 
					CAMERA_PROPERTY_KEY_PREFIX.length());
			if ( makeModelDelimIdx == -1 ) {
				log.warn("Ignoring bad camera property: " +propKey);
				continue;
			}
			String make = propKey.substring(CAMERA_PROPERTY_KEY_PREFIX.length(), 
					makeModelDelimIdx).toLowerCase();
			String model = propKey.substring(makeModelDelimIdx+1).toLowerCase();
			if ( log.isDebugEnabled() ) {
				log.debug("Got camera make [" +make +"] and model [" +model +"]");
			}
			
			if ( !makeModelMap.containsKey(make) ) {
				makeModelMap.put(make, 
						new LinkedHashMap<String, Class<? extends MetadataResource>>());
			}
			Map<String, Class<? extends MetadataResource>> makeMap 
				= makeModelMap.get(make);
			
			Class<? extends MetadataResource> modelClass = null;
			try {
				modelClass = getClass().getClassLoader().loadClass(
						properties.getProperty(propKey)).asSubclass(
								MetadataResource.class);
			} catch ( Exception e) {
				throw new MetadataConfigurationException(
						"Unable to instantiate MetadataResource class [" 
						+properties.getProperty(propKey) +"]", e);
			}
			
			if ( log.isDebugEnabled() ) {
				log.debug("Configured camera mapping for make ["
						+make +"]; model [" +model +"]: "
						+modelClass.getName());
			}
			makeMap.put(model, modelClass);
		}
	}
	
	/**
	 * Get a MetadataResource for a InputStream, supporting camera make/model
	 * configuration as described in the class documentation.
	 * 
	 * <p>If no make/model specific configuration can be found, a 
	 * {@link EXIFJpegMetadataResource} instance will be returned.</p>
	 * 
	 * @param in the input stream
	 * @return the MetadataResource
	 */
	private MetadataResource getJpegMetadataResource(InputStream in) {
		Metadata exif = null;
		try {
			exif = JpegMetadataReader.readMetadata(in);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if ( this.makeModelMap.size() < 1 ) {
			return new EXIFJpegMetadataResource(exif);
		}
		
		// see if can delegate to a camera/model specific implementation
		String make = extractCameraMakeKey(exif);
		String model = extractCameraModelKey(exif);
		if ( makeModelMap.containsKey(make) ) {
			Map<String,Class<? extends MetadataResource>> modelMap = makeModelMap.get(make);
			Class<? extends MetadataResource> makeExifClass = null;
			if ( modelMap.containsKey(model) ) {
				makeExifClass = modelMap.get(model);
				if ( log.isDebugEnabled() ) {
					log.debug("Got ["+model+"] specific EXIF: " +makeExifClass.getName());
				}
			} else if ( modelMap.containsKey(MATCH_ANY_MODEL) ) {
				makeExifClass = modelMap.get(MATCH_ANY_MODEL);
				if ( log.isDebugEnabled() ) {
					log.debug("Got ["+make+"] specific EXIF: " +makeExifClass.getName());
				}
			}
			if ( makeExifClass != null ) {
				try {
					Constructor<? extends MetadataResource> constr 
						= makeExifClass.getConstructor(Metadata.class);
					return constr.newInstance(exif);
				} catch ( Exception e ) {
					throw new RuntimeException(e);
				}
			}
		}
		return new EXIFJpegMetadataResource(exif);
	}

	/**
	 * Extract the camera make as a property key.
	 * 
	 * @param exif the Metadata to extract from
	 * @return camera make
	 */
	protected String extractCameraMakeKey(Metadata exif) {
		return getExifStringKey(exif, ExifDirectory.TAG_MAKE);
	}
	
	/**
	 * Extract the cameral model as a property key.
	 * 
	 * @param exif the Metadata to extract from
	 * @return camera model
	 */
	protected String extractCameraModelKey(Metadata exif) {
		return getExifStringKey(exif, ExifDirectory.TAG_MODEL);
	}

	private String getExifStringKey(Metadata exif, int tagType) {
		Directory dir = exif.getDirectory(ExifDirectory.class);
		if (dir == null || !dir.containsTag(tagType)) {
			return null;
		}
		String result = dir.getString(tagType);
		if ( result != null ) {
			result = result.replaceAll("\\s+", "").toLowerCase();
		}
		return result;
	}
	
	/**
	 * @return the makeModelMap
	 */
	protected Map<String, Map<String, Class<? extends MetadataResource>>> getMakeModelMap() {
		return makeModelMap;
	}
	
	/**
	 * @param makeModelMap the makeModelMap to set
	 */
	protected void setMakeModelMap(
			Map<String, Map<String, Class<? extends MetadataResource>>> makeModelMap) {
		this.makeModelMap = makeModelMap;
	}

}
