package minicraft.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

public class TimeData {
	public static final LocalDateTime time = LocalDateTime.now();
	
	public static Month month() {
		return time.getMonth();
	}
	
	public static int day() {
		return time.getDayOfMonth();
	}
	
	public static LocalDate date() {
		return time.toLocalDate();
	}
}
