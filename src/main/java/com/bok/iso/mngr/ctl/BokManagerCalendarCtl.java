package com.bok.iso.mngr.ctl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bok.iso.mngr.dao.dto.BokManagerCalendarHolidayDto;
import com.bok.iso.mngr.svc.BokManagerCalendarSvc;
import com.bok.iso.mngr.svc.BokManagerMainSvc;
import com.bok.iso.mngr.svc.BokManagerUserSvc;


@Controller
@RequestMapping("/manager")
public class BokManagerCalendarCtl {

    @Autowired
	private BokManagerMainSvc svc;
	@Autowired
	private BokManagerUserSvc loginSvc;
	@Autowired
	public BokManagerCalendarSvc holidaySvc;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private final String calendarPath = "./calendar.";
	
	@PostMapping("/calendar/holiday")
	public String postHoliday(
		@RequestParam(value="name") String name,
		@RequestParam(value="year", required=false) String year,
		@RequestParam(value="month", required=false) String month,
		@RequestParam(value="calDate") String calDate,
		@RequestParam(value="calData") String calData,
		@RequestParam(value="startDay", required=false, defaultValue="0") String startDay) {
			holidaySvc.insertItem(new BokManagerCalendarHolidayDto(calDate, calData, name));
			return "redirect:/manager/calendar/" + name + "?startDay=" + startDay + "&year="+year+"&month=" + month;
	}
	
	/**
	 * 캘린더 새로운 버전
	 * @param name
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 */
	@GetMapping("/calendar/{name}")
	public String calelndar2(
			@PathVariable(value="name") String name,
			@RequestParam(value="year", required=false) String year,
			@RequestParam(value="month", required=false) String month,
			@RequestParam(value="startDay", required=false, defaultValue="0") int startDay,
			@RequestParam(value="filterKey", required=false) String filterKey,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session,
			Model model) {
		String remoteIp = request.getRemoteAddr();
		logger.info("---------------------------------------");
		logger.info("--- APP NAME : /calendar/" + name);
		logger.info("--- DEFAULT PARAM [year] = ["+year+"]");
		logger.info("--- DEFAULT PARAM [month] = ["+month+"]");
		logger.info("--- DEFAULT PARAM [startDay] = ["+startDay+"]");
		logger.info("--- INPUT PARAM : [filterKey]=["+filterKey+"]");
		logger.info("--- ACCESS IP : " + remoteIp);
		/* 세션 검증 */
		if (  !loginSvc.isAuthentication(session) ) 
			return "redirect:/login";
		Calendar cal = Calendar.getInstance();
		int yearInt = cal.get(Calendar.YEAR);
		int monthInt = cal.get(Calendar.MONTH)+1;
		int dayInt = cal.get(Calendar.DAY_OF_MONTH);
		if ( year != null && month != null )  {
			yearInt = Integer.parseInt(year);
			monthInt = Integer.parseInt(month);
			if ( monthInt == 13 )  {
				yearInt += 1;
				monthInt = 1;
			} else if ( monthInt == 0 )  {
				yearInt -= 1;
				monthInt = 12;
			}
		}
		model.addAttribute("yearInt", yearInt);
		model.addAttribute("monthInt", monthInt);
		model.addAttribute("dayInt", dayInt);
		model.addAttribute("name", name);
		model.addAttribute("dayTable", svc.getCalendarTable(cal, yearInt, monthInt));
		int nextMonth = monthInt+1;
		int nextYear = yearInt;
		if ( nextMonth == 13 ) {
			nextYear += 1;
			nextMonth = 1;
		}
		model.addAttribute("dayTable2", svc.getCalendarTable(cal, nextYear, nextMonth));
		logger.info("--- calendar path : " + calendarPath + name+"."+yearInt+".dat");
		Map<String, String> result = svc.loadMap(calendarPath + name+"."+yearInt+".dat");
		Map<String, String> result2 = result;
		if ( yearInt != nextYear ) 
			result2 = svc.loadMap(calendarPath + name+"."+nextYear+".dat");
		if ( filterKey != null && filterKey.trim().length() > 0 ) {
	 		String temp = "";
			String [] lines = null;
			StringBuffer tempResult = null;
			for ( String key : result.keySet() ) {
				temp = result.get(key);
				
				temp = temp.replaceAll("<br>", "\n");
				temp = temp.replaceAll("<br/>", "\n");
				temp = temp.replaceAll("<br />", "\n");
				temp = temp.replaceAll("\t", "");
				temp = temp.replaceAll("<div", "\n<div");
				temp = temp.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
				temp = temp.replaceAll("\n\n", "\n");
				
				lines = temp.split("\n");
				tempResult = new StringBuffer();
				for ( String line : lines ) {
					if ( line.trim().length() == 0 ) continue;
					if ( line.contains(filterKey) ) {
						tempResult.append("<span style='background-color: #ffeedd;'>" + line + "</span><br/>");
					} else {
						tempResult.append("<span style='color: #CCCCCC;'>" + line + "</span><br/>");
					}
				}
				result.put(key, tempResult.toString());
			}
		}
		model.addAttribute("contents", result);
		model.addAttribute("contents2", result2);
		model.addAttribute("nextYear", nextYear);
		model.addAttribute("nextMonth", nextMonth);
		model.addAttribute("startDay", startDay);
		model.addAttribute("filterKey", filterKey);
		model.addAttribute("calHoliday", holidaySvc.selectItems(yearInt, monthInt, name));
		model.addAttribute("calHoliday2", holidaySvc.selectItems(nextYear, nextMonth, name));
		logger.info("---------------------------------------");
		return "calendar/calendar";
	}
	
	/**
	 * 캘린더 새로운 버전
	 * @param name
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 */
	@PostMapping("/calendar/{name}")
	public String calelndarPost2(
			@PathVariable(value="name") String name,
			@RequestParam(value="year") String year,
			@RequestParam(value="month") String month,
			@RequestParam(value="key") String key,
			@RequestParam(value="value") String value,
			@RequestParam(value="startDay", required=false) String startDay,
			HttpServletRequest request, HttpServletResponse response, 
			HttpSession session,
			Model model) {
		String remoteIp = request.getRemoteAddr();
		logger.info("---------------------------------------");
		logger.info("--- APP NAME : /calendar/" + name);
		logger.info("--- DEFAULT PARAM [year] = ["+year+"]");
		logger.info("--- DEFAULT PARAM [month] = ["+month+"]");
		logger.info("--- INPUT PARAM : key=["+key+"], value=["+value.trim()+"]");
		logger.info("--- ACCESS IP : " + remoteIp);
		/* 세션 검증 (미실시) */
		Calendar cal = Calendar.getInstance();
		int yearInt = Integer.parseInt(year);
		int monthInt = Integer.parseInt(month);
		int dayInt = cal.get(Calendar.DAY_OF_MONTH);
		String filePath = calendarPath+name+"."+yearInt+".dat";
		logger.info("--- SAVE RESULT : " + svc.saveMap(filePath, key, value));
		model.addAttribute("yearInt", yearInt);
		model.addAttribute("monthInt", monthInt);
		model.addAttribute("dayInt", dayInt);
		model.addAttribute("name", name);
		model.addAttribute("dayTable", svc.getCalendarTable(cal, yearInt, monthInt));
		Map<String, String> result1 = svc.loadMap(filePath);
		model.addAttribute("contents", result1);
		model.addAttribute("startDay", startDay);
		int nextMonth = monthInt+1;
		int nextYear = yearInt;
		if ( nextMonth == 13 ) {
			nextYear += 1;
			nextMonth = 1;
		}
		model.addAttribute("dayTable2", svc.getCalendarTable(cal, nextYear, nextMonth));
		Map<String, String> result2 = result1;
		if ( yearInt != nextYear ) 
			result2 = svc.loadMap(calendarPath + name+"."+nextYear+".dat");
		model.addAttribute("contents2", result2);
		model.addAttribute("nextYear", nextYear);
		model.addAttribute("nextMonth", nextMonth);
		model.addAttribute("calHoliday", holidaySvc.selectItems(yearInt, monthInt, name));
		model.addAttribute("calHoliday2", holidaySvc.selectItems(nextYear, nextMonth, name));
		logger.info("---------------------------------------");
		return "calendar/calendar";
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
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="year", required=false) String year,
			@RequestParam(value="searchkey", required=false) String searchKey,
			Model model,
			HttpSession session
			) {
		logger.info("---------------------------------------");
		logger.info("--- APP NAME : /calendar/" + name + "/" + year + "/" + searchKey);
		model.addAttribute("name", name);
		model.addAttribute("year", year);
		model.addAttribute("searchkey", searchKey);
		/* 세션 검증 */
		if (  !loginSvc.isAuthentication(session) ) 
			return "redirect:/login";
		if ( name == null || year == null ) {
			model.addAttribute("result", new HashMap<String, String>());
			logger.info("---------------------------------------");
			return "calendar/calfind";
		}
		int yearInt = Integer.parseInt(year);
		String filePath = calendarPath+name+"."+yearInt+".dat";
		Map<String, String> data = svc.loadMap(filePath);
		Map<String, String> result = new HashMap<String, String>();
		for ( String key : data.keySet() ) {
			if ( data.get(key).contains(searchKey) ) {
				logger.info("--- RESULT : ["+key+"]=["+data.get(key)+"]");
				result.put(key, data.get(key).replaceAll(searchKey, "<span style='background-color: yellow;'>" + searchKey + "</span>"));
			}
		}
		model.addAttribute("result", result);
		logger.info("---------------------------------------");
		return "calendar/calfind";
	}

}
