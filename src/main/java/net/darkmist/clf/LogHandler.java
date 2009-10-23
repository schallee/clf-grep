package net.darkmist.clf;

public interface LogHandler
{
	public void handleLogEntry(LogEntry entry);

	public static interface Chained extends LogHandler
	{
		public void setNextLogHandler(LogHandler next);
		public LogHandler getNextLogHandler();

		public static abstract class Abstract implements Chained
		{
			private LogHandler nextLogHandler;

			protected Abstract()
			{
			}

			protected Abstract(LogHandler next)
			{
				setNextLogHandler(next);
			}

			public abstract void handleLogEntry(LogEntry entry);

			protected void nextHandler(LogEntry entry)
			{
				nextLogHandler.handleLogEntry(entry);
			}

			public void setNextLogHandler(LogHandler next)
			{
				nextLogHandler = next;
			}

			public LogHandler getNextLogHandler()
			{
				return nextLogHandler;
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
			Synchronized(LogHandler next)
			{
				super(next);
			}

			public synchronized void handleLogEntry(LogEntry entry)
			{
				nextHandler(entry);
			}

			public synchronized void setNextLogHandler(LogHandler next)
			{
				super.setNextLogHandler(next);
			}

			public synchronized LogHandler getNextLogHandler()
			{
				return super.getNextLogHandler();
			}
		}

		public static final LogHandler.Chained synchronizedLogHandler(LogHandler target)
		{
			return new Synchronized(target);
		}
	}
}
