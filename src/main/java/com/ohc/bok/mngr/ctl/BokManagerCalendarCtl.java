package com.ohc.bok.mngr.ctl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ohc.bok.mngr.dao.dto.BokManagerCalendarHolidayDto;
import com.ohc.bok.mngr.svc.BokManagerCalendarSvc;
import com.ohc.bok.mngr.svc.BokManagerUserSvc;

// servlet types are imported above (jakarta.servlet.http.*)


@Controller
@RequestMapping("/manager")
public class BokManagerCalendarCtl {

    private final BokManagerCalendarSvc calendarSvc;
	private final BokManagerUserSvc loginSvc;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final String calendarPath = "./calendar.";

	BokManagerCalendarCtl(BokManagerCalendarSvc calendarSvc, BokManagerUserSvc loginSvc) {
		this.calendarSvc = calendarSvc;
		this.loginSvc = loginSvc;
	}
	
	@PostMapping("/calendar/holiday")
	public String postHoliday(
		@RequestParam(value="name") String name,
		@RequestParam(value="year", required=false) String year,
		@RequestParam(value="month", required=false) String month,
		@RequestParam(value="calDate") String calDate,
		@RequestParam(value="calData") String calData,
		@RequestParam(value="startDay", required=false, defaultValue="1") String startDay) {
			logger.info("---------------------------------------");
			logger.info("--- APP NAME : /calendar/holiday/");
			logger.info("--- INPUT PARAM : [name]=["+name+"], [year] = ["+year+"], [month] = ["+month+"], [calDate] = ["+calDate+"], [calData] = ["+calData+"], [startDay] = ["+startDay+"]");
			calendarSvc.insertItem(new BokManagerCalendarHolidayDto(calDate, calData, name));
			logger.info("---------------------------------------");
			return "redirect:/manager/calendar?startDay=" + startDay + "&year="+year+"&month=" + month;
	}

	@PostMapping("/calendar-week/holiday")
	public String postHoliday_week(
		@RequestParam(value="name") String name,
		@RequestParam(value="year", required=false) String year,
		@RequestParam(value="month", required=false) String month,
		@RequestParam(value="calDate") String calDate,
		@RequestParam(value="calData") String calData,
		@RequestParam(value="startDay", required=false, defaultValue="0") String startDay) {
			logger.info("---------------------------------------");
			logger.info("--- APP NAME : /calendar/holiday/");
			logger.info("--- INPUT PARAM : [name]=["+name+"], [year] = ["+year+"], [month] = ["+month+"], [calDate] = ["+calDate+"], [calData] = ["+calData+"], [startDay] = ["+startDay+"]");
			calendarSvc.insertItem(new BokManagerCalendarHolidayDto(calDate, calData, name));
			logger.info("---------------------------------------");
			return "redirect:/manager/calendar-week?startDay=" + startDay + "&year="+year+"&month=" + month;
	}

	@GetMapping("/calendar")
	public String calelndar(
			@RequestParam(value="year", required=false) String year,
			@RequestParam(value="month", required=false) String month,
			@RequestParam(value="startDay", required=false, defaultValue="0") int startDay,
			@RequestParam(value="filterKey", required=false) String filterKey,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session,
			Model model) {
		/* 세션 검증 */
		if (  !loginSvc.isAuthentication(session) ) 
			return "redirect:/login";
		String name = loginSvc.getUserId(session);
		String remoteIp = request.getRemoteAddr();
		logger.info("---------------------------------------");
		logger.info("--- APP NAME : /calendar/" + name);
		logger.info("--- DEFAULT PARAM [year] = ["+year+"]");
		logger.info("--- DEFAULT PARAM [month] = ["+month+"]");
		logger.info("--- DEFAULT PARAM [startDay] = ["+startDay+"]");
		logger.info("--- INPUT PARAM : [filterKey]=["+filterKey+"]");
		logger.info("--- ACCESS IP : " + remoteIp);
		
		Calendar cal = Calendar.getInstance();
		int[] ymd = calendarSvc.resolveYearMonthDay(cal, year, month);
		int yearInt = ymd[0];
		int monthInt = ymd[1];
		int dayInt = ymd[2];
		/* 매주 당해 주를 첫줄에 표시하도록 변경 2026. 4. 19. */
		int [][] dayTableInt = calendarSvc.getCalendarTable(cal, yearInt, monthInt);
		startDay = calendarSvc.resolveStartDay(dayTableInt, dayInt, monthInt, startDay, filterKey);
		model.addAttribute("yearInt", yearInt);
		model.addAttribute("monthInt", monthInt);
		model.addAttribute("dayInt", dayInt);
		model.addAttribute("name", name);
		model.addAttribute("dayTable", dayTableInt);
		int[] nextYm = calendarSvc.resolveNextYearMonth(yearInt, monthInt);
		int nextYear = nextYm[0];
		int nextMonth = nextYm[1];
		model.addAttribute("dayTable2", calendarSvc.getCalendarTable(cal, nextYear, nextMonth));
		logger.info("--- calendar path : " + calendarPath + name+"."+yearInt+".dat");
		Map<String, String> result = calendarSvc.loadMap(calendarPath + name+"."+yearInt+".dat");
		Map<String, String> result2 = result;
		if ( yearInt != nextYear )
			result2 = calendarSvc.loadMap(calendarPath + name+"."+nextYear+".dat");
		result = calendarSvc.applyFilterHighlight(result, filterKey);
		model.addAttribute("contents", result);
		model.addAttribute("contents2", result2);
		model.addAttribute("nextYear", nextYear);
		model.addAttribute("nextMonth", nextMonth);
		model.addAttribute("startDay", startDay);
		model.addAttribute("filterKey", filterKey);
		model.addAttribute("calHoliday", calendarSvc.selectItems(yearInt, monthInt, name));
		model.addAttribute("calHoliday2", calendarSvc.selectItems(nextYear, nextMonth, name));
		model.addAttribute("calendarFontFamily", loginSvc.getCurrentFont("calendar"));
		model.addAttribute("calendarFontSize", loginSvc.getCurrentSize("calendar"));
		model.addAttribute("calendarLetterSpacing", loginSvc.getCurrentLetterSpacing("calendar"));
		model.addAttribute("calendarLineHeight", loginSvc.getCurrentLineHeight("calendar"));
		logger.info("---------------------------------------");
		return "calendar/calendar";
	}
	
	@PostMapping("/calendar")
	public String calelndarPost(
			@RequestParam(value="year") String year,
			@RequestParam(value="month") String month,
			@RequestParam(value="key") String key,
			@RequestParam(value="value") String value,
			@RequestParam(value="startDay", required=false, defaultValue="0") String startDay,
			HttpServletRequest request, HttpSession session) {
		/* 세션 검증*/
		if (  !loginSvc.isAuthentication(session) ) 
			return "redirect:/login";
		String name = loginSvc.getUserId(session);
		String remoteIp = request.getRemoteAddr();
		logger.info("---------------------------------------");
		logger.info("--- APP NAME : /calendar (" + name + ", POST)");
		logger.info("--- DEFAULT PARAM [year] = ["+year+"]");
		logger.info("--- DEFAULT PARAM [month] = ["+month+"]");
		logger.info("--- INPUT PARAM : key=["+key+"], value=["+value.trim()+"]");
		logger.info("--- ACCESS IP : " + remoteIp);
		int yearInt = Integer.parseInt(year);
		String filePath = calendarPath+name+"."+yearInt+".dat";
		logger.info("--- SAVE RESULT : " + calendarSvc.saveMap(filePath, key, value));
		logger.info("---------------------------------------");
		return "redirect:/manager/calendar?year=" + year + "&month=" + month + "&startDay=" + startDay;
	}
	
	@RequestMapping(value="/download")
	public ResponseEntity<Resource> download(
			@RequestParam(value="path") String path,
			HttpServletRequest request, HttpServletResponse response)  throws Exception {
		logger.info("---------------------------------------");
		logger.info("--- URL : /manager/download (GET)");
		logger.info("--- REQUEST PARAM [path]	=["+path+"]");
		
		File file = new File(path);
		if ( file.isDirectory() == true ) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.contentType(MediaType.parseMediaType("text"))
					.body(null);
		}
		Resource resource = new InputStreamResource(new FileInputStream(file));
		logger.info("---------------------------------------");
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
				.body(resource);
	}
	
	@GetMapping("/calfind")
	public String calfind(
			@RequestParam(value="year", required=false) String year,
			@RequestParam(value="searchkey", required=false) String searchKey,
			Model model,
			HttpSession session
			) {
		/* 세션 검증 */
		if (  !loginSvc.isAuthentication(session) ) 
			return "redirect:/login";
		String name = loginSvc.getUserId(session);
		logger.info("---------------------------------------");
		logger.info("--- APP NAME : /calendar/" + name + "/" + year + "/" + searchKey);
		model.addAttribute("name", name);
		model.addAttribute("year", year);
		model.addAttribute("searchkey", searchKey);
		if ( name == null || year == null ) {
			model.addAttribute("result", new HashMap<String, String>());
			logger.info("---------------------------------------");
			return "calendar/calfind";
		}
		int yearInt = Integer.parseInt(year);
		String filePath = calendarPath+name+"."+yearInt+".dat";
		Map<String, String> data = calendarSvc.loadMap(filePath);
		Map<String, String> result = calendarSvc.applySearchHighlight(data, searchKey);
		model.addAttribute("result", result);
		logger.info("---------------------------------------");
		return "calendar/calfind";
	}

	/* 주말 제외 평일만 보여주는 페이지 신설 */
	@GetMapping("/calendar-week")
	public String calelndar_week(
			@RequestParam(value="year", required=false) String year,
			@RequestParam(value="month", required=false) String month,
			@RequestParam(value="startDay", required=false, defaultValue="0") int startDay,
			@RequestParam(value="filterKey", required=false) String filterKey,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session,
			Model model) {
		/* 세션 검증 */
		if (  !loginSvc.isAuthentication(session) ) 
			return "redirect:/login";
		String name = loginSvc.getUserId(session);
		String remoteIp = request.getRemoteAddr();
		logger.info("---------------------------------------");
		logger.info("--- APP NAME : /calendar/" + name);
		logger.info("--- DEFAULT PARAM [year] = ["+year+"]");
		logger.info("--- DEFAULT PARAM [month] = ["+month+"]");
		logger.info("--- DEFAULT PARAM [startDay] = ["+startDay+"]");
		logger.info("--- INPUT PARAM : [filterKey]=["+filterKey+"]");
		logger.info("--- ACCESS IP : " + remoteIp);
		Calendar cal = Calendar.getInstance();
		int[] ymd = calendarSvc.resolveYearMonthDay(cal, year, month);
		int yearInt = ymd[0];
		int monthInt = ymd[1];
		int dayInt = ymd[2];
		/* 매주 당해 주를 첫줄에 표시하도록 변경 2026. 4. 19. */
		int [][] dayTableInt = calendarSvc.getCalendarTable(cal, yearInt, monthInt);
		startDay = calendarSvc.resolveStartDay(dayTableInt, dayInt, monthInt, startDay, filterKey);
		model.addAttribute("yearInt", yearInt);
		model.addAttribute("monthInt", monthInt);
		model.addAttribute("dayInt", dayInt);
		model.addAttribute("name", name);
		model.addAttribute("dayTable", dayTableInt);
		int[] nextYm = calendarSvc.resolveNextYearMonth(yearInt, monthInt);
		int nextYear = nextYm[0];
		int nextMonth = nextYm[1];
		model.addAttribute("dayTable2", calendarSvc.getCalendarTable(cal, nextYear, nextMonth));
		logger.info("--- calendar path : " + calendarPath + name+"."+yearInt+".dat");
		Map<String, String> result = calendarSvc.loadMap(calendarPath + name+"."+yearInt+".dat");
		Map<String, String> result2 = result;
		if ( yearInt != nextYear )
			result2 = calendarSvc.loadMap(calendarPath + name+"."+nextYear+".dat");
		result = calendarSvc.applyFilterHighlight(result, filterKey);
		model.addAttribute("contents", result);
		model.addAttribute("contents2", result2);
		model.addAttribute("nextYear", nextYear);
		model.addAttribute("nextMonth", nextMonth);
		model.addAttribute("startDay", startDay);
		model.addAttribute("filterKey", filterKey);
		model.addAttribute("calHoliday", calendarSvc.selectItems(yearInt, monthInt, name));
		model.addAttribute("calHoliday2", calendarSvc.selectItems(nextYear, nextMonth, name));
		model.addAttribute("calendarFontFamily", loginSvc.getCurrentFont("calendar"));
		model.addAttribute("calendarFontSize", loginSvc.getCurrentSize("calendar"));
		model.addAttribute("calendarLetterSpacing", loginSvc.getCurrentLetterSpacing("calendar"));
		model.addAttribute("calendarLineHeight", loginSvc.getCurrentLineHeight("calendar"));
		logger.info("---------------------------------------");
		return "calendar/calendar_week";
	}

	@PostMapping("/calendar-week")
	public String calelndarPost_week(
			@RequestParam(value="year") String year,
			@RequestParam(value="month") String month,
			@RequestParam(value="key") String key,
			@RequestParam(value="value") String value,
			@RequestParam(value="startDay", required=false, defaultValue="1") String startDay,
			HttpServletRequest request, HttpServletResponse response, 
			HttpSession session) {
		/* 세션 검증 */
		if (  !loginSvc.isAuthentication(session) ) 
			return "redirect:/login";
		String name = loginSvc.getUserId(session);
		String remoteIp = request.getRemoteAddr();
		logger.info("---------------------------------------");
		logger.info("--- APP NAME : /calendar/" + name);
		logger.info("--- DEFAULT PARAM [year] = ["+year+"]");
		logger.info("--- DEFAULT PARAM [month] = ["+month+"]");
		logger.info("--- INPUT PARAM : key=["+key+"], value=["+value.trim()+"]");
		logger.info("--- ACCESS IP : " + remoteIp);
		int yearInt = Integer.parseInt(year);
		String filePath = calendarPath+name+"."+yearInt+".dat";
		logger.info("--- SAVE RESULT : " + calendarSvc.saveMap(filePath, key, value));
		logger.info("---------------------------------------");
		return "redirect:/manager/calendar-week?year=" + year + "&month=" + month + "&startDay=" + startDay;
	}

}
