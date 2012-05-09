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
import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineMatcher
{
	public static boolean readerContains(Pattern pat, BufferedReader reader) throws IOException
	{
		String line;
		Matcher matcher = null;

		while((line = reader.readLine())!=null)
		{
			if(matcher == null)
				matcher = pat.matcher(line);
			else
				matcher.reset(line);
			if(matcher.find())
				return true;
		}
		return false;
	}

	public static int copyMatches(Pattern pat, BufferedReader in, Writer out) throws IOException
	{
		String line;
		Matcher matcher = null;
		int count = 0;

		while((line = in.readLine())!=null)
		{
			if(matcher == null)
				matcher = pat.matcher(line);
			else
				matcher.reset(line);
			if(matcher.find())
			{
				out.write(line);
				out.write('\n');
				count++;
			}
		}
		return count;
	}
}
