package ca.customtattoodesign.mobilecrm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ca.customtattoodesign.mobilecrm.dao.TornadoHuntersDao;

@Controller	
public class MainController {

	@GetMapping("/")
	public String indexHtmlHelloWorld() {
		return "index.html";
	}
	
	@GetMapping("test")
	public String testSystemOut() {
		System.out.println("Hello World");
		return "index.html";
	}
	
	@GetMapping("testDB")
	public String testDB() {
		
		try {
			TornadoHuntersDao db = TornadoHuntersDao.getInstance();
			db.testQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return "index.html";
	}
	
}
