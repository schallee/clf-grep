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

public interface LineHandler
{
	public void handleLine(String line);

	public static interface Chained extends LineHandler
	{
		public void setNextLineHandler(LineHandler next);
		public LineHandler getNextLineHandler();

		public static abstract class Abstract implements Chained
		{
			protected LineHandler nextLineHandler;

			protected Abstract()
			{
			}

			protected Abstract(LineHandler next)
			{
				setNextLineHandler(next);
			}

			public abstract void handleLine(String line);

			protected void nextHandler(String line)
			{
				nextLineHandler.handleLine(line);
			}

			public void setNextLineHandler(LineHandler next)
			{
				nextLineHandler = next;
			}

			public LineHandler getNextLineHandler()
			{
				return nextLineHandler;
			}
		}
	}

	public static class Utils
	{
		private Utils()
		{
		}

		private static final class Synchronized extends Chained.Abstract
		{
			Synchronized(LineHandler next)
			{
				super(next);
			}

			public synchronized void handleLine(String line)
			{
				nextHandler(line);
			}

			public synchronized void setNextLineHandler(LineHandler next)
			{
				super.setNextLineHandler(next);
			}

			public synchronized LineHandler getNextLineHandler()
			{
				return super.getNextLineHandler();
			}
		}

		public static final Chained synchronizedLineHandler(LineHandler target)
		{
			return new Synchronized(target);
		}
	}
}
