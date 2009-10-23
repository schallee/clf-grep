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
