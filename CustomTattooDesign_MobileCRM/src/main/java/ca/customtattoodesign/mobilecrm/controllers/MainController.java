package ca.customtattoodesign.mobilecrm.controllers;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * The {@code MainController} class is used for handling non-RESTful API requests.
 * 
 * @author Fernando Fedele
 * @author Andreea Popa
 * @author Roman Krutikov
 * @author Thomas Chapman
 */
@Controller	
public class MainController {

	/**
	 * Mapping for returning the index page.
	 * 
	 * @return {@code index.html} which directs user to the home page.
	 */
	@GetMapping("/")
	public String indexHtmlHelloWorld() {
		return "index.html";
	}
	
	/**
	 * Mapping for returning the index page and prints hello world to the console.
	 * 
	 * @return {@code index.html} which directs user to the home page.
	 */
	@GetMapping("test")
	public String testSystemOut() {
		System.out.println("Hello World");
		return "index.html";
	}
	
}
