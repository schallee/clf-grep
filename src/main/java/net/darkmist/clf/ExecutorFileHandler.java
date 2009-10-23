package net.darkmist.clf;

import java.io.File;
import java.util.concurrent.Executor;

public class ExecutorFileHandler implements FileHandler
{
	private Executor executor;
	private FileHandler.Factory factory;

	private static class FileHandlerRunnable implements Runnable
	{
		private FileHandler.Factory factory;
		private File file;

		FileHandlerRunnable(FileHandler.Factory factory, File file)
		{
			this.factory = factory;
			this.file = file;
		}

		public void run()
		{
			FileHandler handler = null;

			try
			{
				handler = factory.makeFileHandler();
				handler.handleFile(file);
			}
			finally
			{
				if(handler != null && factory instanceof FileHandler.Factory.Recycling)
					((FileHandler.Factory.Recycling)factory).recycleFileHandler(handler);
			}
		}
	}

	protected ExecutorFileHandler()
	{
	}

	protected void setExecutor(Executor executor)
	{
		this.executor = executor;
	}

	protected void setFactory(FileHandler.Factory factory)
	{
		this.factory = factory;
	}

	public ExecutorFileHandler(Executor executor, FileHandler.Factory factory)
	{
		setExecutor(executor);
		setFactory(factory);
	}

	public Executor getExecutor()
	{
		return executor;
	}

	public FileHandler.Factory getFactory()
	{
		return factory;
	}

	public void handleFile(File file)
	{
		FileHandlerRunnable job = new FileHandlerRunnable(factory,file);
		executor.execute(job);
	}
}
