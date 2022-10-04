# Tax calculator (Java)

### Structure of the app 

##### Class CongestionTaxCalculator
Tax rules are stored in three objects:
- `TreeMap<Integer, int[]> timeZones`, key: start time, value: array with two elements (endTime, price)
- `HashSet<String> freeVehiclesNames`, names of tax-free vehicles as strings 
- `HashMap<Integer, List<HashSet<Integer>>> freeDates`, key: year, value (array of size 12): hashset of numbers of free dates for each month 
- `ReadLocalTaxRules` reads from the given text file
- `PrintTaxRules` prints tax rules to the console
- `GetTax(String vehicle, TimePoint[] times)` calculates the tax

##### Class TimePoint
Own and probably more convenient implementation of the Date object. Has Year, Month (0-based), Day (0-based), Hour, Minute, and Second properties. Function `print()` prints the time point in human-readable date format. 

##### Class Helpers
- `ClockStringToSeconds` takes a time interval as a string of format "HH:MM-HH:MM" into `[l,r]` array where `l` is the starting point in seconds (from the beginning of the day) and `r` is ending point. It's ok if `r < l`, means that they belong to different days. 
- `isLeap` returns true if year is leap
- `DaysInMonthInYear` returns number of days in the given month (0-based) or the given year
- `dayOfWeek` returns the number of the day in a week (monday = 0, tuesday = 1...) based on the time point
- `isWeekend` returns true if the given time point belongs to Saturday or Sunday
- `secondsOfDay` returns number of seconds passed from the beginning of the day which the time point belongs to
- `timestamp` is Unix timestamp in seconds


##### Class App
Creates a basic http server which can process vehicle data from POST requests. 
- getTaxes(BufferedReader br) calculates taxes based on the request body. The format of request is text (not json) and strictly defined as follows (the below test is an example):
```
requestID
vehicleName
numberOfTimePoints
YYYY-MM-DD HH:MM:SS
YYYY-MM-DD HH:MM:SS
(one blank line)
```

### Running
- Compile: `javac TimePoint.java Helpers.java CongestionTaxCalculator.java App.java`
- Run: `java App`
Tax rules are stored in `localTaxRules.txt`. Note that the last time zone price is changed to 1 to test how it's used in the calculation!
The app will run at **localhost:8000**. Test through Postman POST request with the following text body:
```
1. Special time zone splitted by midnight (expected: 1)
Car
1
2013-01-14 21:01:00

2. Tax-free day (expected: 0)
Car
1
2013-01-01 08:30:00

3. Weekends (expected: 0)
Car
2
2013-01-05 08:30:00
2013-01-06 08:30:00

4. Tax-free vehicle (expected: 0)
Tractor
1
2013-01-03 08:30:00

5. Multiple points within hour (expected: 18)
Car
5
2013-01-03 06:31:00
2013-01-03 06:47:00
2013-01-03 06:59:00
2013-01-03 07:10:00
2013-01-03 07:15:00

6. Unsorted points (expected: 18)
Car
5
2013-01-03 07:15:00
2013-01-03 06:31:00
2013-01-03 06:59:00
2013-01-03 06:47:00
2013-01-03 07:10:00

7. Multiple points within the midnight time zone (expected: 2)
Car
3
2013-01-03 21:01:00
2013-01-03 21:05:00
2013-01-04 03:30:00

8. Data from the task (expected: 96)
Car
16
2013-01-14 21:00:00
2013-01-15 21:00:00 
2013-02-07 06:23:27
2013-02-07 15:27:00
2013-02-08 06:27:00
2013-02-08 06:20:27
2013-02-08 14:35:00
2013-02-08 15:29:00
2013-02-08 15:47:00
2013-02-08 16:01:00
2013-02-08 16:48:00
2013-02-08 17:49:00
2013-02-08 18:29:00
2013-02-08 18:35:00
2013-03-26 14:25:00
2013-03-28 14:07:27

```

You should see this in the response:
```
1. Special time zone splitted by midnight (expected: 1) ... 1
2. Tax-free day (expected: 0) ... 0
3. Weekends (expected: 0) ... 0
4. Tax-free vehicle (expected: 0) ... 0
5. Multiple points within hour (expected: 18) ... 18
6. Unsorted points (expected: 18) ... 18
7. Multiple points within the midnight time zone (expected: 2) ... 2
8. Data from the task (expected: 96) ... 96
```

Explanation of the last answer:
<img src="https://github.com/ivan-2727/taxCalculator/blob/main/example.png" width="1000">
