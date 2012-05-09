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
