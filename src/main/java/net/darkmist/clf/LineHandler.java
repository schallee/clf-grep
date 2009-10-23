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
