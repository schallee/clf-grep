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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** FileHandler that matches files against a log regex and passes matches on to the next handler.
 * This is NOT thread safe as the matcher is cached.
 */
public class RegexChainedFileHandler extends FileHandler.Chained.Abstract
{
	private Pattern pattern;
	private transient Matcher matcher = null;

	public RegexChainedFileHandler(Pattern pattern, FileHandler nextHandler)
	{
		super(nextHandler);
		this.pattern = pattern;
	}

	public RegexChainedFileHandler(String regex, FileHandler nextHandler)
	{
		this(Pattern.compile(regex), nextHandler);
	}

	protected Matcher getMatcher()
	{
		return matcher;
	}

	public void handleFile(File file)
	{
		if(matcher == null)
			matcher = pattern.matcher(file.getName());
		else
			matcher.reset(file.getName());
		if(matcher.matches())
			nextHandler(file);
	}
}
