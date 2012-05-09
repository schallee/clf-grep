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

public class UserRegexChainedLogHandler extends LogHandler.Chained.Abstract
{
	private Pattern pat;
	private transient Matcher matcher = null;

	public UserRegexChainedLogHandler(Pattern pat, LogHandler next)
	{
		super(next);
		this.pat = pat;
	}

	public void handleLogEntry(LogEntry entry)
	{
		String usr = entry.getUser();

		if(usr == null)
			return;
		if(matcher == null)
			matcher = pat.matcher(usr);
		else
			matcher.reset(usr);
		if(matcher.matches())
			nextHandler(entry);
	}
}
