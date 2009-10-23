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
