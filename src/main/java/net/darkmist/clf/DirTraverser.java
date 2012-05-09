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
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DirTraverser
{
	private static final Class<DirTraverser> CLASS = DirTraverser.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(CLASS);

	private FileHandler fileHandler;
	private Queue<File> frontier;

	public DirTraverser(Queue<File> frontier, FileHandler fileHandler)
	{
		if(frontier == null)
			frontier = Util.newQueue();
		this.frontier = frontier;
		this.fileHandler = fileHandler;
	}

	public DirTraverser(File start, FileHandler fileHandler)
	{
		this(Util.newQueue(start), fileHandler);
	}

	private void onFile(File file)
	{
		if(file.isDirectory())
			frontier.offer(file);
		else if(fileHandler != null)
			fileHandler.handleFile(file);
	}

	private static <T> T[] sort(T...a)
	{
		Arrays.sort(a);
		return a;
	}

	public void run()
	{
		File dir;

		try
		{
			while((dir = frontier.remove())!=null)
			{
				for(File file : sort(dir.listFiles()))
					onFile(file);
			}
		}
		catch(NoSuchElementException ignored)
		{
			// we're done
		}
	}
}
