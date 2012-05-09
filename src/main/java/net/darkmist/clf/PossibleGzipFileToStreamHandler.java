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
