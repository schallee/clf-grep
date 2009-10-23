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
