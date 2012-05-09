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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndexedLogFileHandler extends RegexChainedFileHandler
{
	private static final Class<IndexedLogFileHandler> CLASS = IndexedLogFileHandler.class;
	private static final Log logger = LogFactory.getLog(CLASS);
	private static final String REGEX = "(.*\\.LOG)(?:|\\.gz)";
	private static final Pattern PATTERN = Pattern.compile(REGEX);
	private static final String INDEX_EXT = ".users.gz";
	private static final long MAX_BUF_SIZE = 1024 * 1024;	// 1 meg

	private Pattern usrPat;

	public IndexedLogFileHandler(Pattern usrPat, FileHandler nextHandler)
	{
		super(PATTERN, nextHandler);
		this.usrPat = usrPat;
	}

	/** Checks for index file and presence of the user in the index if it exists.
	 * @param file The log file being examined
	 * @param base The base name of the log file
	 * @return true if the index file exists and the index file does NOT contain the user.
	 */
	private boolean notInIndex(File file, String base)
	{
		File dir;
		File index;
		InputStream in = null;
		String previousThreadName;
		Thread thread;
		int bufSize;

		if((dir = file.getParentFile())==null)
		{
			logger.warn("Log file " + file + " doesn't have a parent directory?");
			return false;
		}
		index = new File(dir, base + INDEX_EXT);
		if(!index.exists())
		{
			if(logger.isDebugEnabled())
				logger.debug("no index file " + index + " for log file " + file);
			return false;
		}
		thread = Thread.currentThread();
		previousThreadName = thread.getName();
		thread.setName("idx: " + index);
		if(logger.isDebugEnabled())
			logger.debug("index scan of " + index);
		bufSize = (int)Math.min(index.length(), MAX_BUF_SIZE);
		try
		{
			in = new GZIPInputStream(new BufferedInputStream(new FileInputStream(index),bufSize));
			if(LineMatcher.readerContains(usrPat, new BufferedReader(new InputStreamReader(in))))
			{
				logger.debug("found usr in index");
				return false;
			}
			logger.debug("did not find usr in index");
			return true;
		}
		catch(IOException e)
		{
			logger.warn("IOException reading from index " + index, e);
		}
		finally
		{
			in = Util.close(in, logger, index.toString());
			thread.setName(previousThreadName);
		}
		return false;
	}

	protected void nextHandler(File file)
	{
		Matcher matcher;
		String base;
		String previousThreadName;
		Thread thread;

		// get the base name...
		matcher = getMatcher();
		base = matcher.group(1);

		// check the index for it
		if(notInIndex(file,base))
			return;

		// pass it on
		thread = Thread.currentThread();
		previousThreadName = thread.getName();
		thread.setName("log: " + file);
		logger.info("scanning");
		try
		{
			super.nextHandler(file);
		}
		finally
		{
			thread.setName(previousThreadName);
		}
	}
}
