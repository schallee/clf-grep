package net.darkmist.clf;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class PossibleGzipFileToStreamHandler extends FileToStreamHandler
{
	private static final String GZIP_EXT = ".gz";

	public PossibleGzipFileToStreamHandler(InputStreamHandler streamHandler)
	{
		super(streamHandler);
	}

	protected InputStream wrapInputStream(File file, InputStream in) throws IOException
	{
		String name = file.getName();

		if(name.substring(name.length()-GZIP_EXT.length()).equalsIgnoreCase(GZIP_EXT))
		{	// our logs end in .gz but most are now actually zips...
			return new MagicDecompressInputStream(in);
		}
		return in;
	}
}
