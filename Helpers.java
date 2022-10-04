import java.util.*;

public class Helpers {
	
	int[] daysInMonthUsual;
	int[] daysInMonthLeap;
	public int[] prefixUsual; //prefix sum of the array containing days in each month in usual or leap year 
	public int[] prefixLeap;
	 
	public Helpers() {
		daysInMonthUsual = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		daysInMonthLeap = new int[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		prefixUsual = new int[12];  
		prefixLeap = new int[12];
		prefixUsual[0] = daysInMonthUsual[0];
		prefixLeap[0] = daysInMonthLeap[0];
		for (int j = 1; j < 12; j++) {
			prefixUsual[j] = prefixUsual[j-1] + daysInMonthUsual[j]; 
			prefixLeap[j] = prefixLeap[j-1] + daysInMonthLeap[j]; 
		}
	}
	
	public int[] ClockStringToSeconds(String clockTime) {
		String[] raw = clockTime.split("[-:]");
		return new int[]{Integer.parseInt(raw[0])*3600 + Integer.parseInt(raw[1])*60, Integer.parseInt(raw[2])*3600 + (Integer.parseInt(raw[3])-1)*60 + 59};
	}
	
	public boolean isLeap(int year) {
		return (year - 2020)%4 == 0;
	}
	
	public int DaysInMonthInYear(int month, int year) {
		//month is 0-based
		if (isLeap(year)) return daysInMonthLeap[month];
		return daysInMonthUsual[month]; 
	}
	
	public int dayOfWeek(TimePoint t) {
		// 2013 as reference, others shifted... 
		int shift = 0; 
		if (t.year >= 2013) shift = t.year - 2013 + (t.year - 2013)/4; 
		else shift = -(2013 - t.year) - (2016 - t.year)/4;
		if (t.month == 0) {
			int rem = ((t.day+1 + shift) % 7);
			if (rem >= 0) return rem;
			return 7+rem;
		}
		if (isLeap(t.year)) {
			int rem = ((prefixLeap[t.month-1] + t.day+1 + shift) % 7);
			if (rem >= 0) return rem;
			return 7+rem;
		}
		int rem = ((prefixUsual[t.month-1] + t.day+1 + shift) % 7);
		if (rem >= 0) return rem;
		return 7+rem;
	}
	
	public boolean isWeekend(TimePoint t) {
		int dow = dayOfWeek(t);
		return (dow==5 || dow==6);
	}
	
	public int secondsOfDay(TimePoint t) {
		return t.hour*3600+t.minute*60+t.second; 
	}
	
	public long timestamp(TimePoint t) {
		int days = t.day;
		if (isLeap(t.year) && t.month>0) days += prefixLeap[t.month-1];
		else if (!isLeap(t.year) && t.month>0) days += prefixUsual[t.month-1];
        int prev_days = 0;
        if (t.year >= 2013) prev_days = ((t.year - 2013)/4)*prefixLeap[11] + ((t.year - 2013) - (t.year - 2013)/4)*prefixUsual[11]; 
        else prev_days = -((2016-t.year)/4)*prefixLeap[11] - ((2013 - t.year) - (2016-t.year)/4)*prefixUsual[11]; 
        return (days + prev_days)*24*60*60 + t.hour*60*60 + t.minute*60 + t.second + 1356998400;
	}

}