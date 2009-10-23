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
