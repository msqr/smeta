/* ===================================================================
 * EnvironmentResourcesTest.java
 * 
 * Created Jan 22, 2007 10:37:05 AM
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

package magoffin.matt.meta;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * Test case for testing local resources, which are not checked into 
 * source control.
 * 
 * <p>This test case can be used to test copyrighted resources in a 
 * local environment, without checking those resources into the source
 * control system.</p>
 * 
 * <p>To use, create a file called <code>smeta-test-environment.properties</code>
 * on your classpath. In the same directory as that file, place the files you
 * want to test, named like this:</p>
 * 
 * <pre>X-[MetadataResource class name]-[expected metadata key count].Y</pre>
 * 
 * <p>Where <code>X</code> can be anything (for example "Test01"), <code>Y</code>
 * is a supported file extension, and <code>[MetadataResource class name]</code>
 * is the expected implementation class name (without any packages) of 
 * {@link MetadataResource} to be used to parse the file, and 
 * <code>[expected metadata key count]</code> is the number of metadata keys 
 * you expect to be parsed. For example, you might have files named like this:</p>
 * 
 * <ul>
 *   <li>Test01-ID3v2_2MetadataResource-11.mp3</li>
 *   <li>Test02-ID3v2_3MetadataResource-9.mp3</li>
 *   <li>Test03-EXIFJpegMetadataResource-12.jpg</li>
 * </ul>
 * 
 * <p>The test case will run for each file, stopping at any error, and 
 * log all the keys and values extracted.</p>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class EnvironmentResourcesTest extends TestCase {

	/** Pattern to match test media resources. */
	private static final Pattern FILENAME_MATCH_PATTERN 
		= Pattern.compile("\\w+-(\\w+)-(\\d+)\\.(\\w+)$", 
			Pattern.CASE_INSENSITIVE);
	
	private final Logger log = Logger.getLogger(EnvironmentResourcesTest.class);


	private static class TestResourceFilenameFilter implements FilenameFilter {
		public boolean accept(File file, String name) {
			return FILENAME_MATCH_PATTERN.matcher(name).find();
		}
	}
	
	/**
	 * Run this unit test.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(EnvironmentResourcesTest.class);
	}

	/**
	 * Run the media tests.
	 * @throws Exception if an error occurs
	 */
	public void testResources() throws Exception {
		URL u = getClass().getClassLoader().getResource("smeta-test-environment.properties");
		assertNotNull("The file [smeta-test-environment.properties] was not found on the "
				+"classpath, please create this file.", u);
		File envFlagFile = new File(URLDecoder.decode(u.getFile(), "UTF-8"));
		File baseDir = envFlagFile.getParentFile();
		File[] testFiles = baseDir.listFiles(new TestResourceFilenameFilter());
		log.debug("Found " +testFiles.length +" media files to test");
		MetadataResourceFactoryManager manager 
			= MetadataResourceFactoryManager.getDefaultManagerInstance();
		for ( File testFile : testFiles ) {
			log.debug("Teting media file [" +testFile.getAbsolutePath() +"]");
			handleFile(manager, testFile);
		}
	}
	
	private MetadataResource handleFile(MetadataResourceFactoryManager manager, File file) 
	throws Exception {
		MetadataResourceFactory factory = manager.getMetadataResourceFactory(file);
		assertNotNull("No factory configured for file [" +file.getAbsolutePath() +"]", factory);
		MetadataResource mResource = factory.getMetadataResourceInstance(file);
		assertNotNull(mResource);
		log.debug("Got MetadataResource implementation: " +mResource.getClass().getName());
		
		Iterable<String> keys = mResource.getParsedKeys();
		assertNotNull(keys);
		int actualNumKeys = 0;
		for ( String key : keys ) {
			actualNumKeys++;
			log.debug("Key [" +key +"] = " +mResource.getValue(key, Locale.US));
		}

		
		// verify implementation class name, num meta found
		Matcher matcher = FILENAME_MATCH_PATTERN.matcher(file.getName());
		assertTrue(matcher.find());
		String implName = matcher.group(1);
		int numKeys = Integer.parseInt(matcher.group(2));
		log.debug("Testing for MetadataResource implementation [" +implName 
				+"] and number of keys [" +numKeys +"]");
		
		String actualImplName = mResource.getClass().getName();
		int packageIdx = actualImplName.lastIndexOf('.');
		if ( packageIdx != -1 ) {
			actualImplName = actualImplName.substring(packageIdx+1);
		}
		assertEquals(implName, actualImplName);
		assertEquals(numKeys, actualNumKeys);
		return mResource;
	}
	

}
