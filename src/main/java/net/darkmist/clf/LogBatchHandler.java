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

public interface LogBatchHandler
{
	public void handleLogBatch(LogBatch batch);

	public static interface Chained extends LogBatchHandler
	{
		public void setNextLogBatchHandler(LogBatchHandler next);
		public LogBatchHandler getNextLogBatchHandler();

		public static abstract class Abstract implements Chained
		{
			private LogBatchHandler nextLogBatchHandler;

			protected Abstract()
			{
			}

			protected Abstract(LogBatchHandler next)
			{
				setNextLogBatchHandler(next);
			}

			public abstract void handleLogBatch(LogBatch batch);

			protected void nextHandler(LogBatch batch)
			{
				nextLogBatchHandler.handleLogBatch(batch);
			}

			public void setNextLogBatchHandler(LogBatchHandler handler)
			{
				nextLogBatchHandler = handler;
			}

			public LogBatchHandler getNextLogBatchHandler()
			{
				return nextLogBatchHandler;
			}
		}
	}

	public class Utils
	{
		private Utils()
		{
		}

		private static class Synchronized extends Chained.Abstract
		{
			protected Synchronized(LogBatchHandler next)
			{
				super(next);
			}

			public synchronized void handleLogBatch(LogBatch batch)
			{
				nextHandler(batch);
			}

			public synchronized void setNextLogBatchHandler(LogBatchHandler handler)
			{
				super.setNextLogBatchHandler(handler);
			}

			public synchronized LogBatchHandler getNextLogBatchHandler()
			{
				return super.getNextLogBatchHandler();
			}
		}

		public static final LogBatchHandler.Chained synchronizedLogBatchHandler(LogBatchHandler target)
		{
			return new Synchronized(target);
		}
	}
}
