/* ===================================================================
 * Display.java
 * 
 * Created Oct 29, 2010 1:27:17 PM
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

package magoffin.matt.meta.util;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import magoffin.matt.meta.MetadataNotSupportedException;
import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.MetadataResourceFactory;
import magoffin.matt.meta.MetadataResourceFactoryManager;

/**
 * Simple application that uses sMeta to display metadata extracted from 
 * files passed on the command line.
 *
 * @author matt
 * @version $Revision$ $Date$
 */
public class Display {

	/**
	 * Display metadata for files pass on command line.
	 * 
	 * <p>Pass files on the command line to read metadata from.</p>
	 * 
	 * @param args list of files
	 * @throws IOException if an IO error occurs
	 */
	public static void main(String[] args) throws IOException {
		if ( args == null || args.length < 1 ) {
			System.err.println("Pass files to read metadata from.");
		}
		
		// 1: get a MetadataResourceFactoryManager instance
		MetadataResourceFactoryManager manager 
			= MetadataResourceFactoryManager.getDefaultManagerInstance();
		
		for ( String filePath : args ) {
			File oneFile = new File(filePath);
			
			// 2: get a MetadataResourceFactory for this file type
			MetadataResourceFactory factory 
				= manager.getMetadataResourceFactory(oneFile);
			
			try {
				// 3: get a MetadataResource for this file
				MetadataResource metaResource 
					= factory.getMetadataResourceInstance(oneFile);
				
				// 4: print out all available metadata
				Iterable<String> keys = metaResource.getParsedKeys();
				System.out.println("\nMetadata for file [" 
					+oneFile.getAbsolutePath() +"]:");
				for ( String key : keys ) {
					System.out.println(key +" = " 
						+metaResource.getValue(key, Locale.getDefault()));
				}
			} catch ( MetadataNotSupportedException e ) {
				System.err.println("File [" +oneFile.getAbsolutePath() 
						+"] is not supported by the [" 
						+factory.getClass().getName() +"] factory");
			}
		}
	}

}
