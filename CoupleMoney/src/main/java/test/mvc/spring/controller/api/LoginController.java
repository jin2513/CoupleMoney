package test.mvc.spring.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="api")
public class LoginController {
	@RequestMapping(value="/login", method = RequestMethod.GET, produces = "application/json")
	public String login() {
		return "";
	}
}
