import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;


public class Test {
	public static void main(String[] args) {
		ZonedDateTime start = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("GMT"));
		TemporalUnit unit_ = ChronoUnit.MONTHS;
		ZonedDateTime end = start.plus(2,unit_);
		end = end.minusWeeks(1);
		long k = start.until(end, unit_);
		System.out.println(k);
		
		ZonedDateTime curr = start;
		long d = curr.until(end, ChronoUnit.SECONDS);
		while(d >=0){
			System.out.println(curr.toString());
			curr = curr.plus(1, unit_);
			d = curr.until(end, ChronoUnit.SECONDS);
		}
	}
}
