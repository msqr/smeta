/* ===================================================================
 * MP4MetadataResource.java
 * 
 * Created Aug 23, 2010 11:00:34 AM
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

package magoffin.matt.meta.video;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import magoffin.matt.meta.MetadataResource;

import com.coremedia.iso.FileRandomAccessDataSource;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.RandomAccessDataSource;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.BoxContainer;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.TrackMetaData;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.SampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;

/**
 * {@link MetadataResource} for video metadata using mp4parser.
 * 
 * <p>
 * See <a href="http://code.google.com/p/mp4parser/">mp4parser</a>.
 * </p>
 * 
 * @author matt
 * @version $Revision$ $Date$
 */
public class MP4MetadataResource extends AbstractVideoMetadataResource {

	/**
	 * Construct from a File.
	 * 
	 * @param f
	 *            the file
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public MP4MetadataResource(File f) throws IOException {
		RandomAccessDataSource dataSource = null;
		try {
			dataSource = new FileRandomAccessDataSource(f);
			extractMetadata(dataSource);
		} finally {
			if (dataSource != null) {
				dataSource.close();
			}
		}
	}
	
	private <T extends Box> T getFirstBox(BoxContainer container, Class<T> clazz) {
		if ( container == null ) {
			return null;
		}
		T[] boxes = container.getBoxes(clazz);
		if ( boxes == null || boxes.length < 1 ) {
			return null;
		}
		return boxes[0];
	}
	
	private void extractMetadata(RandomAccessDataSource dataSource)
	throws IOException {
		IsoFile isoFile = new IsoFile(dataSource);
		isoFile.parse();
		isoFile.parseMdats();
		
		MovieBox movie = getFirstBox(isoFile, MovieBox.class);
		MovieHeaderBox movieHeader = getFirstBox(movie, MovieHeaderBox.class);
		if ( movieHeader == null ) {
			return;
		}
		
		double duration = movieHeader.getDuration(); // units
		double timeScale = movieHeader.getTimescale(); // units/sec
		long ms = Math.round((duration / timeScale) * 1000);
		setValue(VideoMetadataType.DURATION, Long.valueOf(ms));
		setValue(VideoMetadataType.DURATION_TIME, getDurationTime(ms));

		TrackBox[] tracks = movie.getBoxes(TrackBox.class);
		for ( TrackBox trackBox : tracks ) {
			TrackHeaderBox trackHeader = trackBox.getTrackHeaderBox();
			TrackMetaData<TrackBox> trackMeta = movie.getTrackMetaData(trackHeader.getTrackId());
			MediaHeaderBox header = getFirstBox(getFirstBox(trackBox, MediaBox.class), MediaHeaderBox.class);
			SampleDescriptionBox sampleBox = trackMeta.getSampleDescriptionBox();
			SampleEntry sampleEntry = getFirstBox(sampleBox, SampleEntry.class);
			if ( sampleEntry instanceof AudioSampleEntry ) {
				extractAudioMetadata(header, (AudioSampleEntry)sampleEntry);
			} else if ( sampleEntry instanceof VisualSampleEntry ) {
				extractVideoMetadata(header, sampleBox, (VisualSampleEntry)sampleEntry);
			}
		}
		/*
		Box[] boxes = isoFile.getBoxes();
		if (log.isDebugEnabled()) {
			log.debug("Got boxes: " + Arrays.toString(boxes));
		}

		Box box = (Box) get(isoFile, "/moov/udta/meta/ilst/©nam");
		if (box instanceof AppleTrackTitleBox) {
			AppleTrackTitleBox titleBox = (AppleTrackTitleBox) box;
			System.out.println("Track title: " + titleBox.getTrackTitle());
		}*/

	}
	
	private void extractVideoMetadata(MediaHeaderBox header, SampleDescriptionBox sampleDescBox, 
			VisualSampleEntry visualEntry) {
		StringBuilder buf = new StringBuilder();
		buf.append(getTypeName(visualEntry.getType()));
		setValue(VideoMetadataType.VIDEO_FORMAT, buf);
		if ( visualEntry.getWidth() > 0 ) {
			setValue(VideoMetadataType.WIDTH, visualEntry.getWidth());
		}
		if ( visualEntry.getHeight() > 0 ) {
			setValue(VideoMetadataType.HEIGHT, visualEntry.getHeight());
		}
		SampleSizeBox sampleSize = getFirstBox(sampleDescBox.getParent(), SampleSizeBox.class);
		if ( sampleSize != null ) {
			if ( sampleSize.getSampleSize() > 0 ) {
				
			} else {
				long[] entrySizes = sampleSize.getEntrySize();
				long total = 0;
				for ( long l : entrySizes ) {
					total += l;
				}
				log.debug("Total sample size: " +total);
			}
		}
	}
	
	private void extractAudioMetadata(MediaHeaderBox header,
			AudioSampleEntry audioEntry) {
		StringBuilder buf = new StringBuilder();
		buf.append(getTypeName(audioEntry.getType()));
		if (audioEntry.getSampleRate() > 0f ) {
			buf.append(", ").append(audioEntry.getSampleRate()).append(" Hz, ");
		} else if ( header.getTimescale() > 0 ) {
			long mhz = header.getTimescale();
			buf.append(", ").append(String.format("%.1f", mhz / 1000.0)).append(" kHz, ");
		}
		if (audioEntry.getSampleSize() > 0) {
			buf.append(audioEntry.getSampleSize()).append(
					" bit, ");
		}
		switch (audioEntry.getChannelCount()) {
			case 1:
				buf.append("mono");
				break;
			case 2:
				buf.append("stereo");
				break;
			default:
				buf.append(audioEntry.getChannelCount()).append(
						" channels");
		}
		setValue(VideoMetadataType.AUDIO_FORMAT, buf.toString());
	}

	private String getTypeName(byte[] type) {
		try {
			return new String(type, "ISO-8859-1");
		} catch ( UnsupportedEncodingException e ) {
			return new String(type);
		}
	}

}
