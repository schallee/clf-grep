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
