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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.collections.map.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogParser
{
	private static final Class<LogParser> CLASS = LogParser.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	private static final Log logger = LogFactory.getLog(CLASS);

	private static final String REGEX_DATE = "(\\d{2})/(\\w{3})/(\\d{4}):(\\d{2}):(\\d{2}):(\\d{2}) ([\\-+]?\\d{4})";
	private static final Pattern PATTERN_DATE = Pattern.compile(REGEX_DATE);

	private static final String REGEX_IP_USER = "([^ ]+) ([^ ]+) ([^ ]+) ";
	//private static final String REGEX_TIME = "\\[(\\d{2})/(\\w{3})/(\\d{4}):(\\d{2}):(\\d{2}):(\\d{2}) ([\\-+]?\\d{4})\\] ";
	private static final String REGEX_TIME = "\\[([^\\]]*)\\] ";
	// from http1.1 spec rfc2616
	private static final String REGEX_TOKEN = "[\\p{ASCII}&&[^\\p{Cntrl}\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\{\\} \t]]+";
	private static final String REGEX_METH = REGEX_TOKEN;
	// from http1.1 spec: rfc2616
	private static final String REGEX_PROTO = "HTTP/\\d+\\.\\d+";
	// URI characters from mrfc2396
	private static final String REGEX_URI_RESERVED = ";/\\?:@\\&=\\+\\$,";
	private static final String REGEX_URI_MARK = "\\-_\\.!~\\*'\\(\\)";
	private static final String REGEX_URI_ALPHANUM = "a-zA-Z0-9";
	private static final String REGEX_URI_UNRESERVED = REGEX_URI_ALPHANUM + REGEX_URI_MARK;
	private static final String REGEX_URI_ESCAPE = "%";
	private static final String REGEX_URI_CHAR = "[" + REGEX_URI_UNRESERVED + REGEX_URI_RESERVED + REGEX_URI_ESCAPE + "]";
	private static final String REGEX_STAT_SIZE = " (-|\\d+) +(-|\\d+)$";

	// exacting request line
	private static final String REGEX_EXACT_REQ_LINE = "(" + REGEX_METH + ") (" + REGEX_URI_CHAR + "+) (" + REGEX_PROTO + ")";
	private static final String REGEX_EXACT = REGEX_IP_USER + REGEX_TIME + "\"" + REGEX_EXACT_REQ_LINE + "\"" + REGEX_STAT_SIZE;
	@SuppressWarnings("unused")
	private static final Pattern PATTERN_EXACT = Pattern.compile(REGEX_EXACT);

	// non-http/log messup request line: no space or double quote
	private static final String REGEX_NO_SP_URI_CHAR = "[^ ]";
	private static final String REGEX_NO_SP_REQ_LINE = "(" + REGEX_METH + ") (" + REGEX_NO_SP_URI_CHAR + "+) (" + REGEX_PROTO + ")";
	private static final String REGEX_NO_SP = REGEX_IP_USER + REGEX_TIME + "\"" + REGEX_NO_SP_REQ_LINE + "\"" + REGEX_STAT_SIZE;
	private static final Pattern PATTERN_NO_SP = Pattern.compile(REGEX_NO_SP);

	// allow anything which requires the regex to back track
	private static final String REGEX_BACK_TRACK_URI_CHAR = ".";
	private static final String REGEX_BACK_TRACK_REQ_LINE = "(" + REGEX_METH + ") (" + REGEX_BACK_TRACK_URI_CHAR + "+) (" + REGEX_PROTO + ")";
	private static final String REGEX_BACK_TRACK = REGEX_IP_USER + REGEX_TIME + "\"" + REGEX_BACK_TRACK_REQ_LINE + "\"" + REGEX_STAT_SIZE;
	private static final Pattern PATTERN_BACK_TRACK = Pattern.compile(REGEX_BACK_TRACK);

	// border manager errors: (bad request line)
	// this does not include a protocol!
	private static final String REGEX_BM_METH = "(?:" + REGEX_TOKEN + "|\\(bad request line\\))";
	private static final String REGEX_BM_URI_CHAR = "[^\"]";
	private static final String REGEX_BM_REQ_LINE = "(" + REGEX_BM_METH + ") (" + REGEX_BM_URI_CHAR + "+)";
	private static final String REGEX_BM = REGEX_IP_USER + REGEX_TIME + "\"" + REGEX_BM_REQ_LINE + "\"" + REGEX_STAT_SIZE;
	private static final Pattern PATTERN_BM = Pattern.compile(REGEX_BM);

	private static final int PART_IP = 1;
	private static final int PART_IDENT = PART_IP+1;
	private static final int PART_USR = PART_IDENT+1;

	private static final int PART_TIME = PART_USR+1;
	private static final int PART_METH = PART_TIME+1;

	/*
	private static final int PART_DAY = PART_USR+1;
	private static final int PART_MON = PART_DAY+1;
	private static final int PART_YEAR = PART_MON+1;
	private static final int PART_HOUR = PART_YEAR+1;
	private static final int PART_MIN = PART_HOUR+1;
	private static final int PART_SEC = PART_MIN+1;
	private static final int PART_TZ = PART_SEC+1;
	private static final int PART_METH = PART_TZ+1;
	*/

	private static final int PART_URI = PART_METH+1;
	private static final int PART_PROTO = PART_URI+1;
	private static final int PART_STATUS = PART_PROTO+1;
	private static final int PART_SIZE = PART_STATUS+1;
	private static final int NUM_PARTS = PART_SIZE+1;

	/*
	// old
	private static final String REGEX_IP_USER = "([^ ]+) ([^ ]+) ([^ ]+) ";
	private static final String REGEX_TIME = "\\[(\\d{2})/(\\w{3})/(\\d{4}):(\\d{2}):(\\d{2}):(\\d{2}) ([\\-+]?\\d{4})\\] ";
	// from http1.1 spec rfc2616
	private static final String REGEX_TOKEN = "[\\p{ASCII}&&[^\\p{Cntrl}\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\{\\} \t]]+";
	private static final String REGEX_METH = REGEX_TOKEN;
	private static final String REGEX_METH_BM = "(?:" + REGEX_METH + "|\\(bad request line\\))";
	// from http1.1 spec: rfc2616
	private static final String REGEX_PROTO = "HTTP/\\d+\\.\\d+";
	// URI... anything except ...
	private static final String REGEX_NON_GROUPING_STAT_SIZE = " (?:-|\\d+) +(?:-|\\d+)$";
	private static final String REGEX_SPACE_NO_PROTO_DQ_STAT_SIZE = " (?!" + REGEX_PROTO + "\""  + REGEX_NON_GROUPING_STAT_SIZE + ")";
	private static final String REGEX_DQ_NO_STAT_SIZE = "\"(?!" + REGEX_NON_GROUPING_STAT_SIZE +")";
	//private static final String REGEX_URI = "(?:[^ \"]+|" + REGEX_SPACE_NO_PROTO_DQ_STAT_SIZE + "|\"(?! [\\-\\d]))+";
	private static final String REGEX_URI = ".+";

	private static final String REGEX_REQ_LINE = "(" + REGEX_METH_BM + ") (" + REGEX_URI + ") (" + REGEX_PROTO + ")";
	//private static final String REGEX_REQ_LINE = "(" + REGEX_METH_BM + ") (.+) (" + REGEX_PROTO + ")";
	// these don't usually have a protocol so we'll just leave that empty...
	private static final String REGEX_BM_BAD_REQ_LINE = "\\(bad request line\\) (.+)()";
	private static final String REGEX_REQ = "\"" + REGEX_REQ_LINE + "\"";
	private static final String REGEX_STAT_SIZE = " (-|\\d+) +(-|\\d+)$";
	private static final String REGEX = REGEX_IP_USER + REGEX_TIME + REGEX_REQ + REGEX_STAT_SIZE;
	*/

	private final DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
	private transient Matcher cachedMatcher = null;
	private transient Matcher cachedDateMatcher = null;
	private transient Map<String,Integer> monthCache = newHardCache(12);
	private transient Map<String,TimeZone> tzCache = newHardCache(24);
	private transient Map<String,InetAddress> ipCache = newWeakCache();
	private transient String lastDateStr = null;
	private transient Date lastDate = null;
	//private transient Map<String,Date> dateCache = newWeakCache();

	@SuppressWarnings("unused")
	private static <N,V> Map<N,V> newHardCache()
	{
		return new HashMap<N,V>();
	}

	private static <N,V> Map<N,V> newHardCache(int size)
	{
		return new HashMap<N,V>(size);
	}

	@SuppressWarnings("unchecked")
	private static <N,V> Map<N,V> newWeakCache()
	{
		return new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
	}

	private TimeZone parseTimeZone(String str)
	{
		/*
		boolean neg = combinedOffset < 0;
		int min;
		int hour;
		int off;

		if(neg)
			combinedOffset = -combinedOffset;
		min = combinedOffset%100;
		hour = combinedOffset/100;
		((hour * 60 + min) * 60 )*1000
		*/
		// looks like we can prepend GMT and just let the JVM do it...
		TimeZone tz;

		if((tz = tzCache.get(str))==null)
		{
			if(logger.isDebugEnabled())
				logger.debug("Getting TimeZone GMT"+str);
			tz = TimeZone.getTimeZone("GMT"+str);
			tzCache.put(str,tz);
		}
		return tz;
	}

	private int parseMonth(String str) throws LogFormatException
	{
		Integer ret;
		Month mon;

		if((ret = monthCache.get(str))==null)
		{
			if(logger.isDebugEnabled())
				logger.debug("Getting month "+str);
			mon = Month.valueOfIgnoreCase(str);
			if(mon == null)
				throw new LogFormatException("Unknown month: " + str);
			ret = mon.getCalendarValue();
			monthCache.put(str, ret);
		}
		return ret;
	}

	private static int parseInt(String str)
	{
		return Integer.parseInt(str);
	}

	private static int parse2DigitInt(String str)
	{
		// return parseInt(str);
		int ret = 0;

		switch(str.charAt(0))
		{
			case '0':
				ret = 0;
				break;
			case '1':
				ret = 10;
				break;
			case '2':
				ret = 20;
				break;
			case '3':
				ret = 30;
				break;
			case '4':
				ret = 40;
				break;
			case '5':
				ret = 50;
				break;
			case '6':
				ret = 60;
				break;
			case '7':
				ret = 70;
				break;
			case '8':
				ret = 80;
				break;
			case '9':
				ret = 90;
				break;
			default:
				throw new NumberFormatException("Non-digit in " + str);
		}
		switch(str.charAt(1))
		{
			case '0':
				return ret;
			case '1':
				return ret+1;
			case '2':
				return ret+2;
			case '3':
				return ret+3;
			case '4':
				return ret+4;
			case '5':
				return ret+5;
			case '6':
				return ret+6;
			case '7':
				return ret+7;
			case '8':
				return ret+8;
			case '9':
				return ret+9;
			default:
				throw new NumberFormatException("Non-digit in " + str);
		}
	}

	private static int parse4DigitInt(String str)
	{
		return parseInt(str);
	}

	private Date parseDate(String[] parts) throws LogFormatException
	{
		/*
		dateStr = matcher.group(4);
		try
		{
			return dateFormat.parse(dateStr);
		}
		catch(ParseException e)
		{
			throw new LogFormatException("Unable to parse date: " + dateStr, e);
		}
		*/
		int year;
		int mon;
		int day;
		int hour;
		int min;
		int sec;
		TimeZone tz;
		GregorianCalendar cal;
		String dateStr;
		Date ret;

		dateStr = parts[PART_TIME];
		/*
		if((ret = dateCache.get(dateStr))!=null)
			return ret;
		*/
		if(lastDateStr != null && dateStr.equals(lastDateStr) && lastDate != null)
			return lastDate;
		if(cachedDateMatcher == null)
			cachedDateMatcher = PATTERN_DATE.matcher(dateStr);
		else
			cachedDateMatcher.reset(dateStr);
		if(!cachedDateMatcher.matches())
			throw new LogFormatException("Time stamp " + dateStr + " does not match regex.");
		day = parse2DigitInt(cachedDateMatcher.group(1));
		mon = parseMonth(cachedDateMatcher.group(2));
		year = parse4DigitInt(cachedDateMatcher.group(3));
		hour = parse2DigitInt(cachedDateMatcher.group(4));
		min = parse2DigitInt(cachedDateMatcher.group(5));
		sec = parse2DigitInt(cachedDateMatcher.group(6));
		tz = parseTimeZone(cachedDateMatcher.group(7));
		
		/*
		year = parse4DigitInt(parts[PART_YEAR]);
		mon = parseMonth(parts[PART_MON]);
		day = parse2DigitInt(parts[PART_DAY]);
		hour = parse2DigitInt(parts[PART_HOUR]);
		min = parse2DigitInt(parts[PART_MIN]);
		sec = parse2DigitInt(parts[PART_SEC]);
		tz = parseTimeZone(parts[PART_TZ]);
		*/

		// build
		cal = new GregorianCalendar(year, mon, day, hour, min, sec);
		cal.setTimeZone(tz);
		ret = cal.getTime();
		//dateCache.put(dateStr, ret);
		lastDateStr = dateStr;
		lastDate = ret;
		return ret;
	}

	private String toString(Date date)
	{
		return dateFormat.format(date);
	}

	private static final String[] matcher2parts(Matcher matcher)
	{
		String[] parts;

		parts = new String[NUM_PARTS];
		for(int i=0;i<NUM_PARTS;i++)
			parts[i] = matcher.group(i);
		return parts;
	}

	private String[] parseParts(String line) throws LogFormatException
	{
		Matcher matcher;

		try
		{
			if(cachedMatcher == null)
				cachedMatcher = PATTERN_NO_SP.matcher(line);
			else
				cachedMatcher.reset(line);
			if(cachedMatcher.matches())
				return matcher2parts(cachedMatcher);

			// any special handling here...
			/*
			matcher = PATTERN_NO_SP.matcher(line);
			if(matcher.matches())
			{
				if(logger.isDebugEnabled())
					logger.debug("PATTERN_NO_SP match: " + line);
				return matcher2parts(matcher);
			}
			*/

			// this is happening more than the back track below...
			matcher = PATTERN_BM.matcher(line);
			if(matcher.matches())
			{	// since there is no proto, we need to provide one
				String[] parts;

				//if(logger.isDebugEnabled())
					//logger.debug("PATTERN_BM match: " + line);
		
				parts = new String[NUM_PARTS];
				for(int i=0;i<PART_PROTO;i++)
					parts[i] = matcher.group(i);
				parts[PART_PROTO] = null;
				for(int i=PART_STATUS;i<NUM_PARTS;i++)
					parts[i] = matcher.group(i-1);
				return parts;
			}

			matcher = PATTERN_BACK_TRACK.matcher(line);
			if(matcher.matches())
			{
				if(logger.isDebugEnabled())
					logger.debug("PATTERN_BACKTRACK match: " + line);
				return matcher2parts(matcher);
			}

			throw new LogFormatException("Line did not match any regex: " + line);
		}
		catch(Error e)
		{	// log the line that triggered the Error
			logger.error("rethrowing Error " + e.getClass().getName() + " caused by: " + line);
			throw e;
		}
		catch(RuntimeException e)
		{	// log the line that triggered the RuntimeException
			logger.error("rethrowing RuntimeException " + e.getClass().getName() + " caused by: " + line);
			throw e;
		}
	}

	private InetAddress parseIP(String ip) throws UnknownHostException
	{
		//return InetAddress.getByName(ip);
		InetAddress addr;

		if((addr = ipCache.get(ip))==null)
		{
			addr = InetAddress.getByName(ip);
			ipCache.put(ip,addr);
		}
		return addr;
	}

	public LogEntry parse(String line) throws LogFormatException
	{
		String[] parts;
		InetAddress ip;
		String ident;
		String usr;
		Date date;
		int status;
		long size;

		// split this out because I want to see the profile better.
		parts = parseParts(line);

		// convert stuff to the format we want
		try
		{
			//ip = InetAddress.getByName(parts[PART_IP]);
			ip = parseIP(parts[PART_IP]);
		}
		catch(UnknownHostException e)
		{
			throw new LogFormatException("UnknownHostException parsing: " + line, e);
		}
		ident = nullIfDash(parts[PART_IDENT]);
		usr = nullIfDash(parts[PART_USR]);
		date = parseDate(parts);
		try
		{
			status = negIntIfDash(parts[PART_STATUS]);
		}
		catch(NumberFormatException e)
		{
			throw new LogFormatException("Unable to parse integer status " + parts[PART_STATUS] + " in line " + line, e);
		}
		try
		{
			size = zeroLongIfDash(parts[PART_SIZE]);
		}
		catch(NumberFormatException e)
		{
			throw new LogFormatException("Unable to parse long byte size " + parts[PART_SIZE] + " in line " + line, e);
		}

		// build our object
		return new LogEntry(ip, ident, usr, date, parts[PART_METH], parts[PART_URI], parts[PART_PROTO], status, size);
	}

	private static String nullIfDash(String str)
	{
		if(str == null)
			return null;
		if(str.length() == 1 && str.charAt(0) == '-')
			return null;
		return str;
	}

	private static long zeroLongIfDash(String str) throws NumberFormatException
	{
		if(str.length() == 1 && str.charAt(0) == '-')
			return 0;
		return Long.parseLong(str);
	}

	private static int negIntIfDash(String str) throws NumberFormatException
	{
		if(str.length() == 1 && str.charAt(0) == '-')
			return -1;
		return parseInt(str);
	}

	private static String dashIfNull(String s)
	{
		if(s == null)
			return "-";
		return s;
	}

	public String format(LogEntry entry)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(entry.getIP().getHostAddress());
		sb.append(' ');
		sb.append(dashIfNull(entry.getIdent()));
		sb.append(' ');
		sb.append(dashIfNull(entry.getUser()));
		sb.append(" [");
		sb.append(toString(entry.getDate()));
		sb.append("] \"");
		sb.append(entry.getMethod());
		sb.append(' ');
		sb.append(entry.getURI());
		sb.append(' ');
		sb.append(entry.getProtocol());
		sb.append("\" ");
		if(entry.getStatus()<0)
			sb.append('-');
		else
			sb.append(entry.getStatus());
		sb.append(' ');
		if(entry.getSize() <= 0)
			sb.append('-');
		else
			sb.append(entry.getSize());
		return sb.toString();
	}

	public static void main(String[] args) throws IOException, LogFormatException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		LogParser parser = new LogParser();
		String line;
		LogEntry entry;

		while((line = in.readLine())!=null)
		{
			entry = parser.parse(line);
			System.out.println("Orig: " + line);
			System.out.println("New:  " + parser.format(entry));
		}
	}
}
