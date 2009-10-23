package net.darkmist.clf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RegexChainedLineHandler extends LineHandler.Chained.Abstract
{
	private static final Class<RegexChainedLineHandler> CLASS = RegexChainedLineHandler.class;
	private static final String CLASS_NAME = CLASS.getName();
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
