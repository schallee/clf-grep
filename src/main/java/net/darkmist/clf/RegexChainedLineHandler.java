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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RegexChainedLineHandler extends LineHandler.Chained.Abstract
{
	private static final Class<RegexChainedLineHandler> CLASS = RegexChainedLineHandler.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(CLASS);
	private Pattern pattern;
	private transient Matcher matcher = null;

	protected RegexChainedLineHandler()
	{
	}
	
	public RegexChainedLineHandler(Pattern pattern, LineHandler next)
	{
		super(next);
		setPattern(pattern);
	}

	public Pattern getPattern()
	{
		return pattern;
	}

	public void setPattern(Pattern pattern)
	{
		matcher = null;
		this.pattern = pattern;
	}

	public void handleLine(String line)
	{
		if(matcher == null)
			matcher = pattern.matcher(line);
		else
			matcher.reset(line);
		if(matcher.matches())
			nextLineHandler.handleLine(line);
	}
}
