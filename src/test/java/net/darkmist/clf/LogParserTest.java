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

import java.net.InetAddress;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogParserTest extends TestCase
{
	private static final Class<LogParserTest> CLASS = LogParserTest.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(CLASS);
	private static final String LOG_TIME_AS_STR = "05/Jun/2007:08:09:10 -0100";
	private static final Date LOG_TIME_AS_DATE = mkLogTimeAsDate();

	private LogParser parser;

	private static Date mkLogTimeAsDate()
	{
		GregorianCalendar cal = new GregorianCalendar(2007, 5, 5, 8, 9, 10);
		cal.setTimeZone(TimeZone.getTimeZone("GMT-1"));
		return cal.getTime();
	}

	@Override
	protected void setUp()
	{
		parser = new LogParser();
	}

	public void testSimpleLog() throws Exception
	{
		String in="1.2.3.4 Ident User [" + LOG_TIME_AS_STR + "] \"GET http://localhost:80/ HTTP/1.1\" 200 2";
		LogEntry entry = parser.parse(in);

		assertEquals(InetAddress.getByName("1.2.3.4"), entry.getIP());
		assertEquals("Ident", entry.getIdent());
		assertEquals("User", entry.getUser());
		assertEquals(LOG_TIME_AS_DATE, entry.getDate());
		assertEquals("GET", entry.getMethod());
		assertEquals("http://localhost:80/", entry.getURI());
		assertEquals("HTTP/1.1", entry.getProtocol());
		assertEquals(200, entry.getStatus());
		assertEquals(2, entry.getSize());
	}

	public void testDashes() throws Exception
	{
		String in="1.2.3.4 - - [" + LOG_TIME_AS_STR + "] \"GET http://localhost:80/ HTTP/1.1\" - -";
		LogEntry entry = parser.parse(in);

		assertEquals(InetAddress.getByName("1.2.3.4"), entry.getIP());
		assertEquals(null, entry.getIdent());
		assertEquals(null, entry.getUser());
		assertEquals(LOG_TIME_AS_DATE, entry.getDate());
		assertEquals("GET", entry.getMethod());
		assertEquals("http://localhost:80/", entry.getURI());
		assertEquals("HTTP/1.1", entry.getProtocol());
		assertEquals(-1, entry.getStatus());
		assertEquals(0, entry.getSize());
	}

	public void testURIWithDoubleQuote() throws Exception
	{
		String in="1.2.3.4 Ident User [" + LOG_TIME_AS_STR + "] \"GET http://localhost:80/\" HTTP/1.1\" 200 2";
		LogEntry entry = parser.parse(in);

		assertEquals(InetAddress.getByName("1.2.3.4"), entry.getIP());
		assertEquals("Ident", entry.getIdent());
		assertEquals("User", entry.getUser());
		assertEquals(LOG_TIME_AS_DATE, entry.getDate());
		assertEquals("GET", entry.getMethod());
		assertEquals("http://localhost:80/\"", entry.getURI());
		assertEquals("HTTP/1.1", entry.getProtocol());
		assertEquals(200, entry.getStatus());
		assertEquals(2, entry.getSize());
	}

	public void testExtraSpacesBeforeSize() throws Exception
	{
		String in="1.2.3.4 Ident User [" + LOG_TIME_AS_STR + "] \"GET http://localhost:80/ HTTP/1.1\" 200   2";
		LogEntry entry = parser.parse(in);

		assertEquals(InetAddress.getByName("1.2.3.4"), entry.getIP());
		assertEquals("Ident", entry.getIdent());
		assertEquals("User", entry.getUser());
		assertEquals(LOG_TIME_AS_DATE, entry.getDate());
		assertEquals("GET", entry.getMethod());
		assertEquals("http://localhost:80/", entry.getURI());
		assertEquals("HTTP/1.1", entry.getProtocol());
		assertEquals(200, entry.getStatus());
		assertEquals(2, entry.getSize());
	}

	public void testOdd() throws Exception
	{
		String in="10.126.132.105 - U276515.GBU.AUS.TX.HCSC [" + LOG_TIME_AS_STR + "] \"POST http://realvideoe.house.state.tx.us:554/SmpDsBhgRl HTTP/1.0\" -   0";
		LogEntry entry = parser.parse(in);

		assertEquals(InetAddress.getByName("10.126.132.105"), entry.getIP());
		assertEquals(null, entry.getIdent());
		assertEquals("U276515.GBU.AUS.TX.HCSC", entry.getUser());
		assertEquals(LOG_TIME_AS_DATE, entry.getDate());
		assertEquals("POST", entry.getMethod());
		assertEquals("http://realvideoe.house.state.tx.us:554/SmpDsBhgRl", entry.getURI());
		assertEquals("HTTP/1.0", entry.getProtocol());
		assertEquals(-1, entry.getStatus());
		assertEquals(0, entry.getSize());
	}

	/* not doing this until we find we need it
	public void testExtraSpaces() throws Exception
	{
		String in=" 1.2.3.4  Ident  User  [" + LOG_TIME_AS_STR + "]  \" GET  http://localhost:80/  HTTP/1.1 \"  200  2 ";
		LogEntry entry = parser.parse(in);

		assertEquals(InetAddress.getByName("1.2.3.4"), entry.getIP());
		assertEquals("Ident", entry.getIdent());
		assertEquals("User", entry.getUser());
		assertEquals(LOG_TIME_AS_DATE, entry.getDate());
		assertEquals("GET", entry.getMethod());
		assertEquals("http://localhost:80/", entry.getURI());
		assertEquals("HTTP/1.1", entry.getProtocol());
		assertEquals(200, entry.getStatus());
		assertEquals(2, entry.getSize());
	}
	*/

	public void testBorderManagerBadRequestLine() throws Exception
	{
		String in="1.2.3.4 Ident User [" + LOG_TIME_AS_STR + "] \"(bad request line) http://localhost:80/\" 200 2";
		LogEntry entry = parser.parse(in);

		assertEquals(InetAddress.getByName("1.2.3.4"), entry.getIP());
		assertEquals("Ident", entry.getIdent());
		assertEquals("User", entry.getUser());
		assertEquals(LOG_TIME_AS_DATE, entry.getDate());
		assertEquals("(bad request line)", entry.getMethod());
		assertEquals("http://localhost:80/", entry.getURI());
		assertEquals(null, entry.getProtocol());
		assertEquals(200, entry.getStatus());
		assertEquals(2, entry.getSize());
	}

	@Override
	protected void tearDown()
	{
		parser = null;
	}

	public static Test suite()
	{
		return new TestSuite(CLASS);
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
}
