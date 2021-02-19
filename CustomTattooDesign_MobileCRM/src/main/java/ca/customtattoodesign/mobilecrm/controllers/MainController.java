package ca.customtattoodesign.mobilecrm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
	
}
