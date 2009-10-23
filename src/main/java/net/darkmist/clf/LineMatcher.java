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
