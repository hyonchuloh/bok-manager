public class CalendarDayTest {

    public static void main(String[] args) {
        CalendarDayTest test = new CalendarDayTest();
        test.testCalendarDayCreation();
        test.testCalendarDayComparison();
    }

    @Test
    public void testCalendarDayCreation() {
        Calendar cal = Calendar.getInstance();
		int yearInt = cal.get(Calendar.YEAR);
		int monthInt = cal.get(Calendar.MONTH)+1;
		int dayInt = cal.get(Calendar.DAY_OF_MONTH);
        int [][] dayTableInt = this.getCalendarTable(cal, yearInt, monthInt);
        // dayTableInt를 sysout에 찍는다
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(dayTableInt[i][j] + " ");
            }
            System.out.println();
        }
        assertTrue(true);
    }
    
    @Test
    public void testCalendarDayComparison() {
        // TODO: Add comparison tests
    }

    private int[][] getCalendarTable(Calendar calendar, int year, int month) {
		calendar.set(year, month-1, 1);
		int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int firstDay = calendar.get(Calendar.DAY_OF_WEEK);
		int retValue[][] = new int[7][7];
		int daycount = 1;
		for ( int i=0; i<7; i++) {
			for ( int j=0; j<7; j++) {
				if ( firstDay -1 > 0 ) {
					retValue[i][j] = 0;
					firstDay--;
					continue;
				} else if ( daycount > lastDay ) {
					retValue[i][j] = -1;
				} else {
					retValue[i][j] = daycount;
				}
				daycount++;
			}
		}
		return retValue;
	}

    
}