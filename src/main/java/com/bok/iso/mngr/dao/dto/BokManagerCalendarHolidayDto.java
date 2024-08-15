package com.bok.iso.mngr.dao.dto;

public class BokManagerCalendarHolidayDto {

    /* CAL_YYYY_MM_DD PRIMARY KEY, CAL_CLCD, CAL_DATA */
    private int calYear;
    private int calMonth;
    private int calDay;
    private int calClcd;
    private String calData;
    
    public BokManagerCalendarHolidayDto(int year, int month, int day, int clcd, String data) {
        this.calYear = year;
        this.calMonth = month;
        this.calDay = day;
        this.calClcd = clcd;
        this.calData = data;
    } 
    public BokManagerCalendarHolidayDto(String calKey, String data) {
        /* CAL.2024.8.15 = 광복절 */
        this.calYear = Integer.parseInt(calKey.split("\\.")[1]);
        this.calMonth = Integer.parseInt(calKey.split("\\.")[2]);
        this.calDay = Integer.parseInt(calKey.split("\\.")[3]);
        try {
            this.calClcd = Integer.parseInt(data.substring(0,1));
            this.calData = data.substring(1);
        } catch ( Exception e ) {
            this.calClcd = 1;
            this.calData = data;
        }
    }
    
    public int getCalYear() {
        return calYear;
    }
    public int getCalMonth() {
        return calMonth;
    }
    public int getCalDay() {
        return calDay;
    }
    public int getCalClcd() {
        return calClcd;
    }
    public String getCalData() {
        return calData;
    }
    
    public void setCalYear(int calYear) {
        this.calYear = calYear;
    }
    public void setCalMonth(int calMonth) {
        this.calMonth = calMonth;
    }
    public void setCalDay(int calDay) {
        this.calDay = calDay;
    }
    public void setCalClcd(int calClcd) {
        this.calClcd = calClcd;
    }
    public void setCalData(String calData) {
        this.calData = calData;
    }
    @Override
    public String toString() {
        return "BokManagerCalHolidayDto [calYear=" + calYear + ", calMonth=" + calMonth + ", calDay=" + calDay
                + ", calClcd=" + calClcd + ", calData=" + calData + "]";
    }

    

}
