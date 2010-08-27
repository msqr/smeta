/* ===================================================================
 * JMFMetadataResourceFactoryTest.java
 * 
 * Created Jan 29, 2007 12:29:31 PM
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

package magoffin.matt.meta.video;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;

import junit.framework.TestCase;
import magoffin.matt.meta.MetadataImage;
import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.MetadataResourceFactory;

import org.apache.log4j.Logger;

/**
 * Test case for the {@link JMFMetadataResourceFactory} class.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class QuickTimeMetadataResourceFactoryTest extends TestCase {

	private final Logger log = Logger.getLogger(getClass());

	/**
	 * Test able to get QuickTimeMetadataResource instance with a poster image.
	 * @throws Exception if error occurs
	 */
	public void testVideoWithPoster() throws Exception {
		QuickTimeMetadataResourceFactory factory = new QuickTimeMetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource(
				"magoffin/matt/meta/video/MVI_1586.AVI");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		MetadataResource mResource = handleFile(factory, file);
		assertEquals(QuickTimeMetadataResource.class, mResource.getClass());
		
		VideoMetadataResource aResource = (VideoMetadataResource)mResource;

		// verify got image
		Object o = aResource.getValue(VideoMetadataType.POSTER, Locale.US);
		assertNotNull(o);
		assertTrue(o instanceof MetadataImage);
		MetadataImage poster = (MetadataImage)o;
		assertEquals("image/png", poster.getMimeType());
		
		File coverOutput = File.createTempFile("QuickTimeMetadataResource-Poster-", 
				"." +poster.getMimeType().substring(
						poster.getMimeType().indexOf('/')+1));
		log.debug("Creating movie poster file [" +coverOutput.getAbsolutePath() +"]");
		OutputStream out = new BufferedOutputStream(new FileOutputStream(coverOutput));
		poster.writeToStream(out);
		out.flush();
		out.close();
		
		assertTrue(coverOutput.length() > 0);
		
		// get getting BufferedImage
		BufferedImage image = poster.getAsBufferedImage();
		assertNotNull(image);
	}

	private MetadataResource handleFile(MetadataResourceFactory factory, File file) 
	throws IOException {
		MetadataResource mResource = factory.getMetadataResourceInstance(file);
		assertNotNull(mResource);
		log.debug("Got MetadataResource implementation: " +mResource.getClass().getName());
		
		Iterable<String> keys = mResource.getParsedKeys();
		assertNotNull(keys);
		int size = 0;
		for ( String key : keys ) {
			size++;
			log.debug("Key [" +key +"] = " +mResource.getValue(key, Locale.US));
		}
		assertTrue("Should have read some tags", size > 0);
		return mResource;
	}
	

}
