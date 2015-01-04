/* ===================================================================
 * ID3MetadataResourceFactoryTest.java
 * 
 * Created Jan 16, 2007 11:47:43 AM
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

package magoffin.matt.meta.audio;

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
import magoffin.matt.meta.MetadataNotSupportedException;
import magoffin.matt.meta.MetadataResource;
import org.apache.log4j.Logger;

/**
 * Tes case for the {@link ID3MetadataResourceFactory} class.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class ID3MetadataResourceFactoryTest extends TestCase {
	
	private final Logger log = Logger.getLogger(getClass());

	/**
	 * Test non-ID3 file.
	 * @throws Exception if error occurs
	 */
	public void testNonID3Resource() throws Exception {
		ID3MetadataResourceFactory factory = new ID3MetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource("magoffin/matt/meta/image/IMG_1218.jpg");
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
	 * Test able to get ID3v1 resource.
	 * @throws Exception if error occurs
	 */
	public void testID3v1() throws Exception {
		ID3MetadataResourceFactory factory = new ID3MetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource("magoffin/matt/meta/audio/id3v1.mp3");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		MetadataResource mResource = handleFile(factory, file);
		assertEquals(ID3v1MetadataResource.class, mResource.getClass());
	}

	/**
	 * Test able to get ID3v1.1 resource.
	 * @throws Exception if error occurs
	 */
	public void testID3v1_1() throws Exception {
		ID3MetadataResourceFactory factory = new ID3MetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource("magoffin/matt/meta/audio/id3v1_1.mp3");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		MetadataResource mResource = handleFile(factory, file);
		assertEquals(ID3v1_1MetadataResource.class, mResource.getClass());
	}

	/**
	 * Test able to get ID3v2.2 resource.
	 * @throws Exception if error occurs
	 */
	public void testID3v2_2() throws Exception {
		ID3MetadataResourceFactory factory = new ID3MetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource(
				"magoffin/matt/meta/audio/id3v2_2.mp3");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		MetadataResource mResource = handleFile(factory, file);
		assertEquals(ID3v2_2MetadataResource.class, mResource.getClass());

		// verify got image
		AudioMetadataResource aResource = (AudioMetadataResource) mResource;
		Object o = aResource.getValue(AudioMetadataType.ALBUM_COVER, Locale.US);
		assertNotNull(o);
		assertTrue(o instanceof MetadataImage);
		MetadataImage albumCover = (MetadataImage) o;
		assertEquals("image/jpeg", albumCover.getMimeType());
	}

	/**
	 * Test able to get ID3v2.3 resource.
	 * @throws Exception if error occurs
	 */
	public void testID3v2_3() throws Exception {
		ID3MetadataResourceFactory factory = new ID3MetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource(
				"magoffin/matt/meta/audio/id3v2_3.mp3");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		MetadataResource mResource = handleFile(factory, file);
		assertEquals(ID3v2_3MetadataResource.class, mResource.getClass());

		// verify got image
		AudioMetadataResource aResource = (AudioMetadataResource) mResource;
		Object o = aResource.getValue(AudioMetadataType.ALBUM_COVER, Locale.US);
		assertNotNull(o);
		assertTrue(o instanceof MetadataImage);
		MetadataImage albumCover = (MetadataImage) o;
		assertEquals("image/jpeg", albumCover.getMimeType());
	}

	/**
	 * Test able to get ID3v2.4 resource.
	 * @throws Exception if error occurs
	 */
	public void testID3v2_4() throws Exception {
		ID3MetadataResourceFactory factory = new ID3MetadataResourceFactory();
		URL u = getClass().getClassLoader().getResource(
				"magoffin/matt/meta/audio/id3v2_4.mp3");
		File file = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		MetadataResource mResource = handleFile(factory, file);
		assertEquals(ID3v2_4MetadataResource.class, mResource.getClass());
		
		AudioMetadataResource aResource = (AudioMetadataResource)mResource;

		// verify got image
		Object o = aResource.getValue(AudioMetadataType.ALBUM_COVER, Locale.US);
		assertNotNull(o);
		assertTrue(o instanceof MetadataImage);
		MetadataImage albumCover = (MetadataImage)o;
		assertEquals("image/png", albumCover.getMimeType());
		
		File coverOutput = File.createTempFile("ID3MetadataResource-AlbumCover-", 
				"." +albumCover.getMimeType().substring(
						albumCover.getMimeType().indexOf('/')+1));
		log.debug("Creating album cover file [" +coverOutput.getAbsolutePath() +"]");
		OutputStream out = new BufferedOutputStream(new FileOutputStream(coverOutput));
		albumCover.writeToStream(out);
		out.flush();
		out.close();
		
		assertTrue(coverOutput.length() > 0);
		
		// get getting BufferedImage
		BufferedImage image = albumCover.getAsBufferedImage();
		assertNotNull(image);
	}

	private MetadataResource handleFile(ID3MetadataResourceFactory factory, File file) throws IOException {
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
