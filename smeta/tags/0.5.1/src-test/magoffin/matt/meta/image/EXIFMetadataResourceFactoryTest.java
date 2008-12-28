/* ===================================================================
 * EXIFMetadataResourceFactoryTest.java
 * 
 * Created Jan 16, 2007 9:59:27 PM
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

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;

import junit.framework.TestCase;
import magoffin.matt.meta.MetadataNotSupportedException;
import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.image.camera.Canon;
import magoffin.matt.meta.image.camera.Canon20D;
import magoffin.matt.meta.image.camera.CanonG5;

import org.apache.log4j.Logger;

/**
 * Test case for the {@link EXIFMetadataResourceFactory} class.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class EXIFMetadataResourceFactoryTest extends TestCase {

	private final Logger log = Logger.getLogger(getClass());

	/**
	 * Test non-EXIF file.
	 * @throws Exception if error occurs
	 */
	public void testNonEXIFResource() throws Exception {
		EXIFMetadataResourceFactory factory = new EXIFMetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource("magoffin/matt/meta/audio/id3v1.mp3");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		try {
			factory.getMetadataResourceInstance(file);
			fail("Should have thrown " +MetadataNotSupportedException.class.getName());
		} catch ( MetadataNotSupportedException e ) {
			log.debug("Got expected MetadataNotSupportedException: " +e);
			assertNotNull(e.getMetadataFile());
			assertNotNull(e.getFactory());
		}
	}
	
	/**
	 * Test able to get JPEG EXIF resource.
	 * @throws Exception if error occurs
	 */
	public void testJpegEXIFCanon() throws Exception {
		EXIFMetadataResourceFactory factory = new EXIFMetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource("magoffin/matt/meta/image/IMG_4846.jpg");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		MetadataResource mResource = factory.getMetadataResourceInstance(file);
		assertNotNull(mResource);
		log.debug("Got MetadataResource implementation: " +mResource.getClass().getName());
		assertSame(Canon.class, mResource.getClass());
		
		Iterable<String> keys = mResource.getParsedKeys();
		assertNotNull(keys);
		int size = 0;
		for ( String key : keys ) {
			size++;
			log.debug("Key [" +key +"] = " +mResource.getValue(key, Locale.US));
		}
	}

	/**
	 * Test able to get JPEG EXIF resource for a Canon G5 image.
	 * @throws Exception if error occurs
	 */
	public void testJpegEXIFCanonG5() throws Exception {
		EXIFMetadataResourceFactory factory = new EXIFMetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource("magoffin/matt/meta/image/IMG_1218.jpg");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		MetadataResource mResource = factory.getMetadataResourceInstance(file);
		assertNotNull(mResource);
		log.debug("Got MetadataResource implementation: " +mResource.getClass().getName());
		assertSame(CanonG5.class, mResource.getClass());
		
		Iterable<String> keys = mResource.getParsedKeys();
		assertNotNull(keys);
		int size = 0;
		for ( String key : keys ) {
			size++;
			log.debug("Key [" +key +"] = " +mResource.getValue(key, Locale.US));
		}
	}

	/**
	 * Test able to get JPEG EXIF resource for a Canon G5 image.
	 * @throws Exception if error occurs
	 */
	public void testJpegEXIFCanon20D() throws Exception {
		EXIFMetadataResourceFactory factory = new EXIFMetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource("magoffin/matt/meta/image/IMG_4523.jpg");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		MetadataResource mResource = factory.getMetadataResourceInstance(file);
		assertNotNull(mResource);
		log.debug("Got MetadataResource implementation: " +mResource.getClass().getName());
		assertSame(Canon20D.class, mResource.getClass());
		
		Iterable<String> keys = mResource.getParsedKeys();
		assertNotNull(keys);
		int size = 0;
		for ( String key : keys ) {
			size++;
			log.debug("Key [" +key +"] = " +mResource.getValue(key, Locale.US));
		}
	}

	/**
	 * Test able to get JPEG EXIF resource for an iPhone 3G image.
	 * @throws Exception if error occurs
	 */
	public void testJpegEXIFiPhone3G() throws Exception {
		EXIFMetadataResourceFactory factory = new EXIFMetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource("magoffin/matt/meta/image/IMG_0027.JPG");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		MetadataResource mResource = factory.getMetadataResourceInstance(file);
		assertNotNull(mResource);
		log.debug("Got MetadataResource implementation: " +mResource.getClass().getName());
		assertSame(EXIFJpegMetadataResource.class, mResource.getClass());
		
		Iterable<String> keys = mResource.getParsedKeys();
		assertNotNull(keys);
		int size = 0;
		for ( String key : keys ) {
			size++;
			log.debug("Key [" +key +"] = " +mResource.getValue(key, Locale.US));
		}
	}

}
