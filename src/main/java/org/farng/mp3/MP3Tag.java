/* ===================================================================
 * MP3Tag.java
 * 
 * Created Jan 19, 2007 8:01:52 AM
 * 
 * Copyright (c) 2007 Matt Magoffin (spamsqr@msqr.us)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * ===================================================================
 * $Id$
 * ===================================================================
 */

package org.farng.mp3;

import java.util.Iterator;

/**
 * API for extracting various metdata from an MPEG audio file.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public interface MP3Tag {

	/**
	 * Get an iterator over all tags available.
	 * @return iterator
	 */
    public Iterator<MP3MetadataItem> iterator();
    
	/**
	 * Get the song title.
	 * @return song title
	 */
	public String getSongTitle();

	/**
	 * Get the lead artist.
	 * @return lead artist
	 */
	public String getLeadArtist();

	/**
	 * Get the song's album title.
	 * @return album title
	 */
	public String getAlbumTitle();

	/**
	 * Get the year the song was released.
	 * @return year
	 */
	public String getYearReleased();

	/**
	 * Get a song comment.
	 * @return comment
	 */
	public String getSongComment();

	/**
	 * Get the genre.
	 * @return genre
	 */
	public String getSongGenre();

	/**
	 * Get the track number.
	 * @return track number
	 */
	public String getTrackNumberOnAlbum();

	/**
	 * Get the song lyrics.
	 * @return song lyrics
	 */
	public String getSongLyric();

	/**
	 * Get the author/composer.
	 * @return author/composer
	 */
	public String getAuthorComposer();

}
