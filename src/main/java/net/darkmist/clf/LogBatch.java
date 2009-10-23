package net.darkmist.clf;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;

public interface LogBatch extends Serializable
{
	public List<LogEntry> getLogsAsList();
	public LogEntry[] getLogsAsArray();
	public int getNumLogs();
	public boolean preferArray();
	public boolean preferList();

	public interface Appendable extends LogBatch
	{
		public void appendLog(LogEntry log);
	}

	public interface Sorted extends LogBatch
	{
		public LogEntry getFirstLog();
		public LogEntry getLastLog();
		public Comparator<LogEntry> getLogComparator();
	}

	public class ListBased extends LinkedList<LogEntry> implements Appendable
	{
		private static final long serialVersionUID = 1l;

		public List<LogEntry> getLogsAsList()
		{
			return this;
		}

		public LogEntry[] getLogsAsArray()
		{
			return toArray(LogEntry.EMPTY_ARRAY);
		}

		public void appendLog(LogEntry log)
		{
			add(log);
		}

		public boolean preferArray()
		{
			return false;
		}

		public boolean preferList()
		{
			return true;
		}

		public int getNumLogs()
		{
			return size();
		}
	}

	public class ArrayBased implements LogBatch
	{
		private static final long serialVersionUID = 1l;
		protected LogEntry[] array;

		protected ArrayBased()
		{
		}

		protected void setArray(LogEntry[] array)
		{
			this.array = array;
		}

		public ArrayBased(LogEntry[] array)
		{
			setArray(array);
		}
			
		public List<LogEntry> getLogsAsList()
		{
			return Arrays.asList(array);
		}

		public LogEntry[] getLogsAsArray()
		{
			return array;
		}

		public boolean preferArray()
		{
			return true;
		}

		public boolean preferList()
		{
			return false;
		}

		public int getNumLogs()
		{
			return array.length;
		}
	}

	public class SortedArrayBased extends ArrayBased implements Sorted
	{
		private static final long serialVersionUID = 1l;
		private Comparator<LogEntry> comparator;

		/**
		 * Protected no argument constructor for subclassing.
		 */
		protected SortedArrayBased()
		{
		}

		/**
		 * Protected setter for subclass usage.
		 * @param comparator The comparator to the log entries
		 * are sorted by.
		 */
		protected void setComparator(Comparator<LogEntry> comparator)
		{
			this.comparator = comparator;
		}

		/**
		 * @param comparator The comparator to compare LogEntries with
		 * @param array The array of log entries for the batch
		 * @param sort If set, the array will be sorted inside
		 *	the constructor. If unset, the array is assumed
		 *	to already be sorted.
		 */
		public SortedArrayBased(Comparator<LogEntry> comparator, LogEntry[] array, boolean sort)
		{
			if(sort)
				Arrays.sort(array, comparator);
			setArray(array);
			setComparator(comparator);
		}

		public SortedArrayBased(Comparator<LogEntry> comparator, LogEntry[] array)
		{
			super(array);
			setComparator(comparator);
		}

		public LogEntry getFirstLog()
		{
			return array[0];
		}

		public LogEntry getLastLog()
		{
			return array[array.length -1];
		}

		public Comparator<LogEntry> getLogComparator()
		{
			return comparator;
		}
	}

	public class Utils
	{
		private Utils()
		{
		}

		public static Sorted sort(Comparator<LogEntry> comparator, LogBatch batch) 
		{
			LogEntry[] batchArray;

			if(batch instanceof Sorted)
				return (Sorted)batch;
			batchArray = batch.getLogsAsArray();
			Arrays.sort(batchArray,comparator);
			return new SortedArrayBased(comparator, batchArray);
		}
	}
}
