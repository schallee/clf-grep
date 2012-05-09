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
