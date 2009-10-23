package net.darkmist.clf;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Util
{
	private static final Class<Util> CLASS = Util.class;
	private static final Log logger = LogFactory.getLog(CLASS);

	public interface Converter<T,U>
	{
		public U convert(T src);
	}

	private static Converter<String,File> stringToFileConverter = new Converter<String,File>()
	{
		public File convert(String name)
		{
			return new File(name);
		}
	};

	public static Converter<String,File> getStringToFileConverter()
	{
		return stringToFileConverter;
	}

	public static <T extends Closeable> T close(T toClose, Log logExceptionTo, String name)
	{
		if(toClose == null)
			return null;
		try
		{
			toClose.close();
		}
		catch(IOException e)
		{
			logExceptionTo.warn("IOException closing " + name + " ignored.", e);
		}
		return null;
	}

	public static <T extends Closeable> T close(T toClose, String name)
	{
		return close(toClose, logger, name);
	}

	public static <T extends Closeable> T close(T toClose)
	{
		if(toClose == null)
			return null;
		return close(toClose, logger, toClose.toString());
	}

	public static <T> Queue<T> newQueue()
	{
		return new LinkedList<T>();
	}

	public static <T> Queue<T> addTo(Queue<T> q, T[] toAdd, int off, int len)
	{
		int end = Math.min(len + off, toAdd.length);

		for(int c=off;c<end;c++)
			q.offer(toAdd[c]);
		return q;
	}

	public static <T> Queue<T> addTo(Queue<T> q, T...contents)
	{
		return addTo(q,contents,0,contents.length);
	}

	public static <T> Queue<T> newQueue(T[] contents, int off, int len)
	{
		Queue<T> q = newQueue();

		return addTo(q, contents, off, len);
	}

	public static <T> Queue<T> newQueue(T...contents)
	{
		return newQueue(contents,0,contents.length);
	}

	public static <U,T> Queue<T> addTo(Queue<T> q, Converter<U,T> converter, U[] toAdd, int off, int len)
	{
		int end = Math.min(len + off, toAdd.length);

		for(int c=off;c<end;c++)
			q.offer(converter.convert(toAdd[c]));
		return q;
	}

	public static <U,T> Queue<T> addTo(Queue<T> q, Converter<U,T> converter, U...toAdd)
	{
		return addTo(q, converter, toAdd, 0, toAdd.length);
	}

	public static <U,T> Queue<T> newQueue(Converter<U,T> converter, U[] contents, int off, int len)
	{
		Queue<T> q = newQueue();
		return addTo(q,converter,contents,off,len);
	}

	public static <U,T> Queue<T> newQueue(Converter<U,T> converter, U...contents)
	{
		return newQueue(converter, contents, 0, contents.length);
	}

	public static <T> T[] merge(T[] a, T[] b, T[] dest, Comparator<T> comparator)
	{
		int ai,bi,di;
		int dlen = a.length + b.length;

		if(dest.length < dlen)
			throw new IllegalArgumentException("Destination array is not large enough");
		for(ai=bi=di=0;ai<a.length && bi<b.length && di<dlen;di++)
		{
			if(comparator.compare(a[ai],b[bi]) <= 0)
				dest[di] = a[ai++];
			else
				dest[di] = b[bi++];
		}
		// one array has been used up here...
		if(ai<a.length)
			System.arraycopy(a,ai,dest,di,a.length - ai);
		else
			System.arraycopy(b,bi,dest,di,b.length - bi);
		return dest;
	}

	public static <U,T extends Collection<U>> T checkedAdd(T collection, U element)
	{
		if(!collection.add(element))
			throw new IllegalStateException("Collection " + collection + " refused to add element " + element);
		return collection;
	}

	public static <U,T extends Collection<U>> T append(T dest, Iterator<U> i)
	{
		while(i.hasNext())
			checkedAdd(dest, i.next());
		return dest;
	}

	public static <T,U extends Collection<T>> U merge(Iterator<T> ai, Iterator<T> bi, U dest, Comparator<T> comparator)
	{
		T af,bf;	// first elements

		// get the initial stat of the iterators
		if(!ai.hasNext())
			return append(dest, bi);
		if(!bi.hasNext())
			return append(dest,ai);
		af = ai.next();
		bf = bi.next();

		while(true)
		{
			if(comparator.compare(af,bf)<=0)
			{
				checkedAdd(dest,af);
				if(!ai.hasNext())
				{	// nothing left in a
					checkedAdd(dest, bf);
					return append(dest, bi);
				}
				af = ai.next();
			}
			else
			{
				checkedAdd(dest,bf);
				if(!bi.hasNext())
				{	// nothing left in b
					checkedAdd(dest,af);
					return append(dest, ai);
				}
				bf = bi.next();
			}
		}
	}
}

