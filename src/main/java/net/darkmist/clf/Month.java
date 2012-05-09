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

import java.util.Calendar;

public enum Month
{
	JAN (Calendar.JANUARY),
	FEB (Calendar.FEBRUARY),
	MAR (Calendar.MARCH),
	APR (Calendar.APRIL),
	MAY (Calendar.MAY),
	JUN (Calendar.JUNE),
	JUL (Calendar.JULY),
	AUG (Calendar.AUGUST),
	SEP (Calendar.SEPTEMBER),
	OCT (Calendar.OCTOBER),
	NOV (Calendar.NOVEMBER),
	DEC (Calendar.DECEMBER);
	
	private final int calVal;

	Month(int calVal)
	{
		this.calVal = calVal;
	}

	public int getCalendarValue()
	{
		return calVal;
	}

	public static Month valueOfIgnoreCase(String str)
	{
		return valueOf(str.toUpperCase());
	}
}

