package com.bok.iso.mngr;

import java.util.Calendar;

public class CalendarDayTest {

    public static void main(String[] args) {
        CalendarDayTest test = new CalendarDayTest();
        test.testCalendarDayCreation();
        test.testCalendarDayComparison();

    }

    public void testCalendarDayCreation() {
        Calendar cal = Calendar.getInstance();
		int yearInt = cal.get(Calendar.YEAR);
		int monthInt = cal.get(Calendar.MONTH)+1;
        int [][] dayTableInt = this.getCalendarTable(cal, yearInt, monthInt);

        int yearInt2 = cal.get(Calendar.YEAR);
		int monthInt2 = cal.get(Calendar.MONTH)+1+1;
        if ( monthInt2 == 13 ) {
            monthInt2 = 1;
            yearInt2++;
        }
        int [][] dayTableInt2 = this.getCalendarTable(cal, yearInt2, monthInt2);

        boolean isEndDay = false;
        int breakColIndex = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
               if ( isEndDay == false && dayTableInt[i][j] == -1 ) {
                    isEndDay = true;
                    breakColIndex = i;
                    dayTableInt[i][j] = dayTableInt2[i-breakColIndex][j] + 100;
               } if ( isEndDay == true && dayTableInt[i][j] == -1) {
                    dayTableInt[i][j] = dayTableInt2[i-breakColIndex][j] + 100;
                } 
            }
        }

        // Print the calendar table for verification
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(dayTableInt[i][j] + " ");
            }
            System.out.println();
        }

    }
    
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
