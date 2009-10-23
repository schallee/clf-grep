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

