/* ===================================================================
 * ID3v2_2MetadataResource.java
 * 
 * Created Jan 19, 2007 8:43:47 AM
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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

import magoffin.matt.meta.MetadataImage;
import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.support.AbstractEnumMetadataResource;
import magoffin.matt.meta.support.BasicMetadataImage;

import org.farng.mp3.MP3AudioMetadata;
import org.farng.mp3.MP3FileHelper;
import org.farng.mp3.MP3MetadataContainer;
import org.farng.mp3.MP3MetadataItem;
import org.farng.mp3.MP3Tag;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v2_2;

/**
 * {@link MetadataResource} implementation for ID3v2 resources.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class ID3v2_2MetadataResource
extends AbstractEnumMetadataResource<AudioMetadataType>
implements AudioMetadataResource {

	private static final byte LAYER_III = 1;
	private static final byte LAYER_II = 2;
	private static final byte LAYER_I = 3;
	private static final byte VERSION_MPEG1 = 1;
	private static final byte VERSION_MPEG2 = 0;
	
	private RandomAccessFile file;

	/**
	 * Construct with a RandomAccessFile.
	 * 
	 * @param file the file
	 * @throws IOException if an error occurs
	 */
	public ID3v2_2MetadataResource(RandomAccessFile file) throws IOException {
		this.file = file;
		parseMetadata(getMpegAudioMetadata(file), getMP3Tag(file));
	}
	
	/**
	 * Parse the metadata from the MP3 audio and MP3 tags.
	 * @param mp3 the MP3 audio metadata
	 * @param id3 the MP3 tag metadata
	 */
	protected void parseMetadata(MP3AudioMetadata mp3, MP3Tag id3) {
		if ( log.isDebugEnabled() ) {
			StringBuilder buf = new StringBuilder();
			for ( Iterator<MP3MetadataItem> itr = id3.iterator(); itr.hasNext(); ) {
				MP3MetadataItem obj = itr.next();
				buf.append(obj.getIdentifier()).append("\n");
			}
			log.debug("Got ID3v2 items:\n" +buf);
		}
		
		// album
		setValue(AudioMetadataType.ALBUM, id3.getAlbumTitle());
		
		// album cover
		handleAlbumCover(id3);
		
		// artist
		setValue(AudioMetadataType.ARTIST, id3.getLeadArtist());
		
		// song name
		setValue(AudioMetadataType.SONG_NAME, id3.getSongTitle());
		
		// genre
		setValue(AudioMetadataType.GENRE, id3.getSongGenre());
		
		// track #
		String trackNum = id3.getTrackNumberOnAlbum();
		String[] tracks = trackNum.split("/",2);
		try {
			setValue(AudioMetadataType.TRACK_NUM, tracks[0]);
			if ( tracks.length == 2 ) {
				setValue(AudioMetadataType.TRACK_TOTAL, tracks[1]);
			}
		} catch ( Exception e ) {
			if ( log.isDebugEnabled() ) {
				log.debug("Unable to parse ID3v2 track # from file: " 
					+trackNum);
			}
		}
		
		// year
		setValue(AudioMetadataType.YEAR, id3.getYearReleased());
		
		// bit rate
		int bitRate = mp3.getBitRate();
		if ( mp3.isVariableBitRate() ) {
			setValue(AudioMetadataType.BIT_RATE, bitRate +" kbps (VBR)");
		} else if ( bitRate > 0 ) {
			setValue(AudioMetadataType.BIT_RATE, bitRate +" kbps");
		}
		
		if ( mp3.getFrequency() > 0 ) {
			setValue(AudioMetadataType.SAMPLE_RATE, mp3.getFrequency() +" kHz");
		}
		
		// format
		byte version = (byte)(mp3.getMpegVersion() & 0x1); // get around bug in lib?
		byte layer = mp3.getLayer();
		
		StringBuilder buf = new StringBuilder();
		buf.append("MPEG");
		switch ( version ) {
			case VERSION_MPEG1:
				buf.append("-1");
				break;
			case VERSION_MPEG2:
				buf.append("-2");
				break;	
		}
		switch (layer) {
			case LAYER_I:
				buf.append(", Layer 1");
				break;
			case LAYER_II:
				buf.append(", Layer 2");
				break;
			case LAYER_III:
				buf.append(", Layer 3");
				break;
		}
		
		setValue(AudioMetadataType.AUDIO_FORMAT, buf.toString());
	}
	
	/**
	 * Extract a {@link MetadataImage} for the {@link AudioMetadataType#ALBUM_COVER}.
	 * 
	 * <p>This implementation looks for a {@link MP3MetadataItem} with 
	 * the ID <code>PIC</code> and extracts the picture data from there.</p>
	 * 
	 * @param id3 the ID3 tag data to extract from
	 */
	protected void handleAlbumCover(MP3Tag id3) {
		// look for PIC tag
		Iterator<MP3MetadataItem> iterator = id3.iterator();
		while ( iterator.hasNext() ) {
			MP3MetadataItem meta = iterator.next();
			String id = meta.getIdentifier();
			if ( id == null || !id.startsWith("PIC") ) {
				continue;
			}
			MP3MetadataContainer body = meta.getBody();
			String mime = "image/jpeg";
			String imageFormat = (String)body.getObject("Image Format");
			if ( imageFormat != null ) {
				String format = imageFormat.toLowerCase();
				if ( "jpg".equals(format) ) {
					format = "jpeg";
				}
				mime = "image/" +format;
			}
			byte[] pic = (byte[])body.getObject("Picture Data");
			if ( mime != null && pic != null && pic.length > 0 ) {
				setValue(AudioMetadataType.ALBUM_COVER, 
						new BasicMetadataImage(mime, pic));
			}
		}
	}

	/**
	 * Get a {@link MP3Tag} implementation for a file.
	 * 
	 * @param mp3File the file
	 * @throws IOException if an IO error occurs
	 * @return the MP3Tag implementation
	 */
	protected MP3Tag getMP3Tag(RandomAccessFile mp3File) throws IOException {
		MP3Tag id3 = null;
		try {
			id3 = new ID3v2_2(mp3File);
		} catch ( TagException e ) {
			throw new RuntimeException(e);
		}
		return id3;
	}
	
	/**
	 * Get a {@link MP3AudioMetadata} for a file.
	 * 
	 * @param mp3File the file
	 * @throws IOException if an IO error occurs
	 * @return the audio metadata
	 */
	protected MP3AudioMetadata getMpegAudioMetadata(RandomAccessFile mp3File)
	throws IOException {
		return new MP3FileHelper(mp3File);
	}

	/**
	 * @return the file
	 */
	protected RandomAccessFile getFile() {
		return file;
	}

}
