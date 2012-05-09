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

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogEntry implements Serializable
{
	private static final Class<LogEntry> CLASS = LogEntry.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(CLASS);
	private static final long serialVersionUID = 1l;
	private static final boolean INTERN_EVERY = false;
	private static final boolean INTERN_IDENT = true;
	private static final boolean INTERN_USR = true;
	private static final boolean INTERN_METHOD = true;
	private static final boolean INTERN_URI = false;
	private static final boolean INTERN_PROTOCOL = true;
	public static final LogEntry[] EMPTY_ARRAY = new LogEntry[0];

	private InetAddress ip;
	private String ident;
	private String usr;
	private Date date;
	private String method;
	private String uri;
	private String protocol;
	private int status;
	private long size;
	private transient boolean stringsInterned = false;

	private static class DateOnlyComparator implements Comparator<LogEntry>
	{
		private static final DateOnlyComparator singleton = new DateOnlyComparator();

		private DateOnlyComparator()
		{
		}

		static DateOnlyComparator instance()
		{
			return singleton;
		}

		public int compare(LogEntry a, LogEntry b)
		{
			Date da;
			Date db;

			da = a.getDate();
			db = b.getDate();
			return da.compareTo(db);
		}
	}

	/**
	 * Get a comparator that compares logs by date &amp; time only.
	 * @return comparator that compares logs by date &amp; time only.
	 * Note: this comparator imposes orderings that are inconsistent with equals.
	 */
	public static Comparator<LogEntry> getDateOnlyComparator()
	{
		return DateOnlyComparator.instance();
	}

	LogEntry(InetAddress ip, String ident, String usr, Date date, String method, String uri, String protocol, int status, long size)
	{
		this.ip = ip;
		this.ident = ident;
		this.usr = usr;
		this.date = date;
		this.method = method;
		this.uri = uri;
		this.protocol = protocol;
		this.status = status;
		this.size = size;
		if(INTERN_EVERY)
			internStrings();
	}

	private static String internIfNotNull(String str)
	{
		return (str==null)?null:str.intern();
	}

	public void internStrings()
	{
		if(stringsInterned)
			return;

		if(INTERN_IDENT)
			ident = internIfNotNull(ident);
		if(INTERN_USR)
			usr = internIfNotNull(usr);
		if(INTERN_METHOD)
			method = internIfNotNull(method);
		if(INTERN_URI)
			uri = internIfNotNull(uri);
		if(INTERN_PROTOCOL)
			protocol = internIfNotNull(protocol);
		stringsInterned = true;
	}

	public InetAddress getIP()
	{
		return ip;
	}

	public String getIdent()
	{
		return ident;
	}

	public String getUser()
	{
		return usr;
	}

	public Date getDate()
	{
		return date;
	}

	public String getMethod()
	{
		return method;
	}

	public String getURI()
	{
		return uri;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public int getStatus()
	{
		return status;
	}

	public long getSize()
	{
		return size;
	}

	@Override
	public String toString()
	{
		return new LogParser().format(this);
	}

	private static final boolean nullSafeEquals(Object a, Object b)
	{
		if(a == null)
		{
			if(b == null)
				return true;
			return false;
		}
		if(b == null)
			return false;
		return a.equals(b);
	}

	private static final boolean nullSafeEquals(boolean interned, String a, String b)
	{
		if(interned)
			return (a==b);
		return nullSafeEquals(a,b);
	}

	public boolean equals(LogEntry other)
	{
		if(other == null)
			return false;
		// do the non-strings first
		if(!nullSafeEquals(this.ip,other.ip))
			return false;
		if(!nullSafeEquals(this.date,other.date))
			return false;
		if(status != other.status)
			return false;
		if(size != other.size)
			return false;

		internStrings();
		other.internStrings();

		if(!nullSafeEquals(INTERN_IDENT, ident,other.ident))
			return false;
		if(!nullSafeEquals(INTERN_USR, usr,other.usr))
			return false;
		if(!nullSafeEquals(INTERN_METHOD, method,other.method))
			return false;
		if(!nullSafeEquals(INTERN_URI,uri,other.uri))
			return false;
		if(!nullSafeEquals(INTERN_PROTOCOL,protocol,other.protocol))
			return false;
		// nothing is different so we're equal!
		return true;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof LogEntry)
			return equals((LogEntry)o);
		return false;
	}

	private static int nullSafeHashCode(Object o)
	{
		if(o == null)
			return 0;
		return o.hashCode();
	}

	@Override
	public int hashCode()
	{
		int h = 0;

		h ^= nullSafeHashCode(ip);
		h ^= nullSafeHashCode(ident);
		h ^= nullSafeHashCode(usr);
		h ^= nullSafeHashCode(date);
		h ^= nullSafeHashCode(method);
		h ^= nullSafeHashCode(uri);
		h ^= nullSafeHashCode(protocol);
		h ^= status;
		h ^= (int)(size);
		h ^= (int)(size >> 32);
		return h;
	}
}
