package net.darkmist.clf;

import java.io.File;

public interface FileHandler
{
	public void handleFile(File file);

	public interface Chained extends FileHandler
	{
		public void setNextFileHandler(FileHandler next);
		public FileHandler getNextFileHandler();

		public abstract class Abstract implements Chained
		{
			private FileHandler nextFileHandler;

			protected Abstract()
			{
			}

			protected Abstract(FileHandler next)
			{
				setNextFileHandler(next);
			}

			public abstract void handleFile(File file);

			protected void nextHandler(File file)
			{
				nextFileHandler.handleFile(file);
			}

			public void setNextFileHandler(FileHandler next)
			{
				nextFileHandler = next;
			}

			public FileHandler getNextFileHandler()
			{
				return nextFileHandler;
			}
		}
	}

	public interface Factory
	{
		public FileHandler makeFileHandler();

		public interface Recycling extends Factory
		{
			public void recycleFileHandler(FileHandler handler);
		}
	}
}
