package com.scheduler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController{
	@RequestMapping("{_:^(?!index\\.html|api).*$}")
	public String routeHomepage() {
		return "/index.html";
	}
}