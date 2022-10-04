import java.util.*;

public class TimePoint {
	Integer year, month, day, hour, minute, second; 
	public TimePoint() {}
	public TimePoint(String[] rawTimePoint) {
		year = Integer.valueOf(rawTimePoint[0]);
		month = Integer.valueOf(rawTimePoint[1]) - 1;
		day = Integer.valueOf(rawTimePoint[2]) - 1;
		hour = Integer.valueOf(rawTimePoint[3]);
		minute = Integer.valueOf(rawTimePoint[4]);
		second = Integer.valueOf(rawTimePoint[5]);
	}
	public void print() {
		System.out.println(Integer.toString(year) + "-" + Integer.toString(month)+1 + "-" + Integer.toString(day)+1 + " " 
		+ Integer.toString(hour) + ":" + Integer.toString(minute) + ":" + Integer.toString(second));
	}
}