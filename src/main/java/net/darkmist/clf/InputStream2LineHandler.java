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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InputStream2LineHandler extends LineHandler.Chained.Abstract implements InputStreamHandler
{
	private static final Class<InputStream2LineHandler> CLASS = InputStream2LineHandler.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(CLASS);

	public InputStream2LineHandler()
	{
	}

	public InputStream2LineHandler(LineHandler next)
	{
		super(next);
	}

	public void handleLine(String line)
	{
		nextHandler(line);
	}

	public void handleBufferedReader(BufferedReader in) throws IOException
	{
		String line;

		while((line = in.readLine())!=null)
			nextLineHandler.handleLine(line);
	}

	public void handleReader(Reader in) throws IOException
	{
		if(in instanceof BufferedReader)
			handleBufferedReader((BufferedReader)in);
		else
			handleBufferedReader(new BufferedReader(in));
	}

	public void handleInputStream(InputStream in) throws IOException
	{
		handleReader(new InputStreamReader(in));
	}
}
