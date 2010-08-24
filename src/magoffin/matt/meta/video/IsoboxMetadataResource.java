/* ===================================================================
 * IsoboxMetadataResource.java
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
import java.util.EnumSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.support.LocalizablePlaceholder;
import magoffin.matt.meta.support.LocalizablePlaceholder.ValueGenerator;

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
public class IsoboxMetadataResource extends AbstractVideoMetadataResource {
	
	//private Map<String, Object> internalData

	/**
	 * Construct from a File.
	 * 
	 * @param f
	 *            the file
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public IsoboxMetadataResource(File f) throws IOException {
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
		Box box = (Box) get(isoFile, "/moov/udta/meta/ilst/©nam");
		if (box instanceof AppleTrackTitleBox) {
			AppleTrackTitleBox titleBox = (AppleTrackTitleBox) box;
			System.out.println("Track title: " + titleBox.getTrackTitle());
		}*/
	}
	
	private void extractVideoMetadata(MediaHeaderBox header, SampleDescriptionBox sampleDescBox, 
			VisualSampleEntry visualEntry) {
		final String type = getTypeName(visualEntry.getType());
		
		setValue(VideoMetadataType.VIDEO_FORMAT, new LocalizablePlaceholder(new ValueGenerator() {
			public Object getValueForLocale(Locale locale) {
				try {
					return SampleFormat.forType(type).getDescription(locale);
				} catch ( IllegalArgumentException e ) {
					return type;
				}
			}
		}));
		
		if ( visualEntry.getWidth() > 0 ) {
			setValue(VideoMetadataType.WIDTH, visualEntry.getWidth());
		}
		if ( visualEntry.getHeight() > 0 ) {
			setValue(VideoMetadataType.HEIGHT, visualEntry.getHeight());
		}
		SampleSizeBox sampleSize = getFirstBox(sampleDescBox.getParent(), SampleSizeBox.class);
		long sampleCount = sampleSize.getSampleCount();
		if ( sampleCount > 0 ) {
			float fps = (sampleCount * header.getTimescale()) / header.getDuration();
			if ( fps > 0.0 ) {
				setValue(VideoMetadataType.FPS, fps);
			}
		}
	}
	
	private void extractAudioMetadata(MediaHeaderBox header,
			AudioSampleEntry audioEntry) {
		final String type = getTypeName(audioEntry.getType());
		final Double rate;
		if ( audioEntry.getSampleRate() > 0f ) {
			rate = audioEntry.getSampleRate();
		} else if ( header.getTimescale() > 0 ) {
			rate = (double)header.getTimescale();
		} else {
			rate = null;
		}
		final Integer size;
		if ( audioEntry.getSampleSize() > 0 ) {
			size = audioEntry.getSampleSize();
		} else {
			size = null;
		}
		final Integer count;
		if ( audioEntry.getChannelCount() > 0 ) {
			count = audioEntry.getChannelCount();
		} else {
			count = null;
		}
		setValue(VideoMetadataType.AUDIO_FORMAT, new LocalizablePlaceholder(new ValueGenerator() {
			public Object getValueForLocale(Locale locale) {
				StringBuilder buf = new StringBuilder();
				try {
					buf.append(SampleFormat.forType(type).getDescription(locale));
				} catch ( IllegalArgumentException e ) {
					buf.append(type);
				}

				if ( rate != null ) {
					buf.append(", ")
						.append(String.format("%.1f", rate.doubleValue() / 1000.0))
						.append(" kHz");
				}
				if ( size != null ) {
					buf.append(", ").append(size)
						.append(' ')
						.append(getGeneralVideoMessage("bit", locale));
				}
				if ( count != null ) {
					buf.append(", ");
					switch ( count ) {
						case 1:
							buf.append(getGeneralVideoMessage("mono", locale));
							break;
						case 2:
							buf.append(getGeneralVideoMessage("stereo", locale));
							break;
						default:
							buf.append(count).append(' ')
								.append(getGeneralVideoMessage("channels", locale));
					}
				}
				return buf.toString();
			}
		}));
	}

	private String getTypeName(byte[] type) {
		String t;
		try {
			t = new String(type, "ISO-8859-1");
		} catch ( UnsupportedEncodingException e ) {
			t = new String(type);
		}
		return t;
	}
	
	private String getGeneralVideoMessage(String key, Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(
				"magoffin/matt/meta/video/general", locale);
		try {
			return bundle.getString(key);
		} catch ( MissingResourceException e ) {
			return null;
		}
	}
	
	private enum SampleFormat {
		
		MP4A("mp4a"),
		MP4V("mp4v"),
		MP4S("mp4s"),
		AVC1("avc1"),
		ALAC("alac"),
		OWMA("owma"),
		OVC1("ovc1"),
		AVCP("avcp"),
		DRAC("drac"),
		DRA1("dra1"),
		AC_3("ac-3"),
		EC_3("ec-3"),
		G726("g726"),
		MJP2("mjp2"),
		OKSD("oksd"),
		RAW_("raw "),
		RTP_("rtp "),
		S263("s263"),
		SAMR("samr"),
		SAWB("sawb"),
		SAWP("sawp"),
		SEVC("sevc"),
		SQCP("sqcp"),
		SRTP("srtp"),
		SSMV("ssmv"),
		TEXT("tetx"),
		TWOS("twos"),
		TX3G("tx3g"),
		VC_1("vc-1"),
		XML_("xml ");
		
		private String type;
		
		private SampleFormat(String t) {
			this.type = t;
		}
		
		/**
		 * Get a description of this format.
		 * 
		 * @param locale the Locale to get the description for
		 * @return textual description
		 */
		public String getDescription(Locale locale) {
			ResourceBundle bundle = ResourceBundle.getBundle(
					"magoffin/matt/meta/video/mp4", locale);
			try {
				return bundle.getString("format."+name());
			} catch ( MissingResourceException e ) {
				return type;
			}
		}

		/**
		 * Get the type as a String.
		 * 
		 * @return the type
		 */
		public String getType() {
			return type;
		}
		
		/**
		 * Get a SampleFormat for a given type.
		 * 
		 * @param type the type
		 * @return the format
		 */
		public static SampleFormat forType(String type) {
			Set<SampleFormat> set = EnumSet.allOf(SampleFormat.class);
			for ( SampleFormat sf : set ) {
				if ( sf.getType().equals(type) ) {
					return sf;
				}
			}
			throw new IllegalArgumentException(type);
		}
	}
 
}
