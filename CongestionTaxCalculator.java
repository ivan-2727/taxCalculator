import java.util.*;
import java.io.FileReader; 

public class CongestionTaxCalculator {
	
	//could be simple arrays but in case of larger data, we would need a faster lookup
	public TreeMap<Integer, int[]> timeZones; //key: start time, value: [endTime, price] 
	//if time zones were different for different vehicles, this should be nested into an outer hashmap where key is vehicle name 
	public HashSet<String> freeVehiclesNames; //names of tax-free vehicles as strings 
	public HashMap<Integer, List<HashSet<Integer>>> freeDates; //key: year, value (array of size 12): hashset of numbers of free dates for each month 
	public int oncePerThisPeriod; //one hour in the task, unit: second
	
	public CongestionTaxCalculator() {}
	
	//reads tax rults from a local file the name of which is provided as a string (localTaxRules)
	public void ReadLocalTaxRules(String localTaxRules) throws Exception {
		
		Helpers helpers = new Helpers(); 
		Scanner scanner = new Scanner(new FileReader(localTaxRules));
		timeZones = new TreeMap<>();
		int numberOfTimeZones = scanner.nextInt(); 
		for (int i = 0; i < numberOfTimeZones; i++) {
			String clockString = scanner.next();
			int[] startAndEndInSeconds = helpers.ClockStringToSeconds(clockString); 
			timeZones.put(startAndEndInSeconds[0], new int[]{startAndEndInSeconds[1], scanner.nextInt()});
		}
		
		freeVehiclesNames = new HashSet<>(); 
		int numberOfFreeVehicles = scanner.nextInt();
		for (int i = 0; i < numberOfFreeVehicles; i++) freeVehiclesNames.add(scanner.next().toLowerCase());
		
		freeDates = new HashMap<>();
		int numberOfYears = scanner.nextInt();
		for (int i = 0; i < numberOfYears; i++) {
			int year = scanner.nextInt(); 
			freeDates.put(year, new ArrayList<HashSet<Integer>>()); 
			for (int j = 0; j < 12; j++) {
				String month = scanner.next(); //not used in the code, kept in text file for convenience 
				int numberOfFreeDates = scanner.nextInt();
				freeDates.get(year).add(new HashSet<Integer>());
				if (numberOfFreeDates == -1) {
					for (int k = 0; k < helpers.DaysInMonthInYear(j, year); k++) freeDates.get(year).get(j).add(k);
				}
				else {
					for (int k = 0; k < numberOfFreeDates; k++) freeDates.get(year).get(j).add(scanner.nextInt()-1);
				}
			}
		}
		
		oncePerThisPeriod = scanner.nextInt();
		
	}
	
	//prints tax rules to console to make sure they are correct 
	public void PrintTaxRules() {
		System.out.println("Time zones: ");
		for (Map.Entry<Integer,int[]> zone : timeZones.entrySet()) {
			int[] value = zone.getValue(); 
			System.out.println("Start = " + Integer.toString(zone.getKey()) + ", End = " + Integer.toString(value[0]) + ", Tax = " + Integer.toString(value[1]));
		}      
		System.out.println("\nTax-free vehicles:");
		for (String vehicleName : freeVehiclesNames) {
			System.out.println(vehicleName);
		}
		
		System.out.println("\nTax-free dates:");
		for (Map.Entry<Integer, List<HashSet<Integer>>> yearInfo : freeDates.entrySet()) {
			System.out.println("Year = " + Integer.toString(yearInfo.getKey()));
			for (int j = 0; j < 12; j++) {
				System.out.print("Month = " + Integer.toString(j) + ": ");
				for (Integer day : yearInfo.getValue().get(j)) {
					System.out.print(Integer.toString(day) + ", ");
				}
				System.out.println("");
			}
		}  
		System.out.println("\nTaxation at most once per " + Integer.toString(oncePerThisPeriod) + " s");
		System.out.println("\n");
	}
	
	public int getTax(String vehicle, TimePoint[] times) {
		if (freeVehiclesNames.contains(vehicle.toLowerCase())) return 0; 
		Helpers helpers = new Helpers();
		List<TimePoint> timesNoHolidays = new ArrayList<TimePoint>(); 
		for (int i = 0; i < times.length; i++) {
			if (helpers.isWeekend(times[i])) continue;
			if (freeDates.containsKey(times[i].year)) {
				if (freeDates.get(times[i].year).get(times[i].month).contains(times[i].day)) continue; 
			}
			timesNoHolidays.add(times[i]);
		}
		timesNoHolidays.sort((t1, t2) -> {
			long ts1 = helpers.timestamp(t1);
			long ts2 = helpers.timestamp(t2);
			if (ts1 < ts2) return -1;
			if (ts1 > ts2) return 1;
			return 0;
		});
		
		int totalTax = 0;
		int oneTax = 0; 
		TimePoint lastTaxation = new TimePoint(); 
		for (int i = 0; i < timesNoHolidays.size(); i++) {
			boolean needTax = false; 
			if (i==0) lastTaxation = timesNoHolidays.get(i);
			else if (helpers.timestamp(timesNoHolidays.get(i)) - helpers.timestamp(lastTaxation) >= oncePerThisPeriod) {
				lastTaxation = timesNoHolidays.get(i); 
				needTax = true;
			}
			if (needTax) {
				totalTax += oneTax; 
				oneTax = 0;
			}
			int sec = helpers.secondsOfDay(timesNoHolidays.get(i)); 
			Map.Entry<Integer, int[]> zone = timeZones.floorEntry(sec);
			if (zone == null) {
				zone = timeZones.lastEntry();
				if (zone == null) continue;
				int[] value = zone.getValue(); 
				if (value[0] < zone.getKey() && value[0] >= sec) oneTax = Math.max(oneTax, value[1]);
				continue; 
			}
			int[] value = zone.getValue(); 
			if (value[0] >= zone.getKey() && value[0] >= sec) oneTax = Math.max(oneTax, value[1]);
			else if (value[0] < zone.getKey()) oneTax = Math.max(oneTax, value[1]);
 		}
		
		totalTax += oneTax; 
		
		return totalTax;
	} 
	
}