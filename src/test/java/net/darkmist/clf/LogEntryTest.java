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

public class LogEntryTest extends TestCase
{
	private static final Class<LogEntryTest> CLASS = LogEntryTest.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(CLASS);

	private InetAddress ip_a;
	private InetAddress ip_a_dup;
	private InetAddress ip_b;
	private Date date_a;
	private Date date_a_dup;
	private Date date_b;
	private LogEntry a;
	private LogEntry a_dup;
	private LogEntry b;

	private static final Date mkStaticDate()
	{
		GregorianCalendar cal = new GregorianCalendar(2007, 5, 5, 8, 9, 10);
		cal.setTimeZone(TimeZone.getTimeZone("GMT-1"));
		return cal.getTime();
	}

	@Override
	protected void setUp() throws Exception
	{
		ip_a = InetAddress.getByName("1.1.1.1");
		ip_a_dup = InetAddress.getByName("1.1.1.1");
		ip_b = InetAddress.getByName("2.2.2.2");

		date_a = mkStaticDate();
		date_a_dup = mkStaticDate();
		date_b = new Date();

		a = new LogEntry(ip_a, "a_ident", "a_usr", date_a, "a_method", "a_uri", "a_proto", 1, 2);
		a_dup = new LogEntry(ip_a_dup, "a_ident", "a_usr", date_a_dup, "a_method", "a_uri", "a_proto", 1, 2);
		b = new LogEntry(ip_b, "b_ident", "b_usr", date_b, "b_method", "b_uri", "b_proto", 3, 4);
	}

	public void testNotEquals() throws Exception
	{

		assertFalse(a.equals(b));
	}

	public void testEqualsDup() throws Exception
	{
		assertTrue(a.equals(a_dup));
	}

	public void testEquals() throws Exception
	{
		assertTrue(a.equals(a));
	}

	public void testNotEqualsNull() throws Exception
	{
		assertFalse(a.equals(null));
	}

	@Override
	protected void tearDown()
	{
		ip_a = null;
		ip_a_dup = null;
		ip_b = null;
		date_a = null;
		date_a_dup = null;
		date_b = null;
		a = null;
		a_dup = null;
		b = null;
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
