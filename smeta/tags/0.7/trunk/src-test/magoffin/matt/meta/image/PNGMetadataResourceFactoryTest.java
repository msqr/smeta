/* ===================================================================
 * PNGMetadataResourceFactoryTest.java
 * 
 * Created Feb 5, 2007 4:39:38 PM
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

import org.apache.log4j.Logger;

/**
 * Test case for the {@link PNGMetadataResourceFactory} class.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class PNGMetadataResourceFactoryTest extends TestCase {

	private final Logger log = Logger.getLogger(getClass());

	/**
	 * Test non-PNG file.
	 * @throws Exception if error occurs
	 */
	public void testNonPNGResource() throws Exception {
		PNGMetadataResourceFactory factory = new PNGMetadataResourceFactory();
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
	 * Test able to get PNG resource.
	 * @throws Exception if error occurs
	 */
	public void testPng() throws Exception {
		PNGMetadataResourceFactory factory = new PNGMetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource("magoffin/matt/meta/image/ostrander-3d-route.png");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		MetadataResource mResource = factory.getMetadataResourceInstance(file);
		assertNotNull(mResource);
		log.debug("Got MetadataResource implementation: " +mResource.getClass().getName());
		assertSame(PNGMetadataResource.class, mResource.getClass());
		
		Iterable<String> keys = mResource.getParsedKeys();
		assertNotNull(keys);
		int size = 0;
		for ( String key : keys ) {
			size++;
			log.debug("Key [" +key +"] = " +mResource.getValue(key, Locale.US));
		}
	}


}
