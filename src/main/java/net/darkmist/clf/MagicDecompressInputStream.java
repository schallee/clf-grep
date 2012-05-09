/*
 *  Copyright (C) 2012 Ed Schaller <schallee@darkmist.net>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.darkmist.clf;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

/**
 * Decompress either GZIP or ZIP depending on initial magic bytes.
 */
public class MagicDecompressInputStream extends FilterInputStream
{
	private static final byte[] GZIP_MAGIC = new byte[]{(byte)0x1f,(byte)0x8b};

	private void wrapGZIP(InputStream in) throws IOException
	{
		this.in = new GZIPInputStream(in);
	}

	private void wrapZIP(InputStream in) throws IOException
	{
		this.in = new ZipInputStream(in);
		if(((ZipInputStream)this.in).getNextEntry() == null)
			throw new IOException("No entries in ZIP file");
	}

	private static boolean readMatches(InputStream in, byte[] bytes) throws IOException
	{
		int b;

		for(int i=0;i<bytes.length;i++)
		{
			if((b = in.read())<0)
				throw new EOFException("Unexpected end of file reading magic bytes");
			if((byte)b != bytes[i])
				return false;
		}
		return true;
	}

	public MagicDecompressInputStream(InputStream in) throws IOException
	{
		super(null);	// we will set the wrapped input in a sec...
		boolean isGzip;

		if(in instanceof BufferedInputStream)
			in = (BufferedInputStream)in;
		else
			in = new BufferedInputStream(in);
		if(!in.markSupported())
			throw new IllegalStateException("BufferedInputStream's docs say it handles marks but markSupported returned false");
		in.mark(GZIP_MAGIC.length);
		isGzip = readMatches(in,GZIP_MAGIC);
		in.reset();
		if(isGzip)
			wrapGZIP(in);
		else
			wrapZIP(in);
	}
}
