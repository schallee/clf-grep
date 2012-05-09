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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileToStreamHandler implements FileHandler
{
	private static final Class<FileToStreamHandler> CLASS = FileToStreamHandler.class;
	private static final Log logger = LogFactory.getLog(CLASS);
	private static final long MAX_BUF_SIZE = 1024 * 1024 * 5;	// 5 megs...
	private InputStreamHandler nextHandler;

	public FileToStreamHandler(InputStreamHandler streamHandler)
	{
		nextHandler = streamHandler;
	}

	protected void nextHandler(InputStream in) throws IOException
	{
		nextHandler.handleInputStream(in);
	}

	protected InputStream wrapInputStream(File file, InputStream in) throws IOException
	{
		return in;
	}

	public void handleFile(File file)
	{
		InputStream in = null;

		try
		{
			in = new FileInputStream(file);
			in = new BufferedInputStream(in, (int)Math.min(file.length(), MAX_BUF_SIZE));
			in = wrapInputStream(file, in);
			nextHandler(in);
		}
		catch(IOException e)
		{
			logger.warn("IOException handling " + file, e);
		}
		finally
		{
			in = Util.close(in, logger, file.getPath());
		}
	}
}
