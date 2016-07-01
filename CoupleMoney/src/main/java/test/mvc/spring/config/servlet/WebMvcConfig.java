package test.mvc.spring.config.servlet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

//@Configuration
//@ComponentScan(basePackages = "test.mvc.spring", includeFilters = @Filter({ Controller.class,
//		Service.class }), useDefaultFilters = false)
//public class WebMvcConfig extends WebMvcConfigurationSupport {
@Configuration
@EnableWebMvc
@ComponentScan("test.mvc.spring")
public class WebMvcConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {
	private static final String[] RESOURCES = { "resources", "webjars" };

	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		for (String resource : RESOURCES) {
			String resourceHandler = "/" + resource + "/";
			String resourceLocations = resourceHandler + "**";
			System.out.println("resourceHandler : " + resourceHandler + " / resourceLocations : " + resourceLocations);
			registry.addResourceHandler(resourceHandler).addResourceLocations(resourceLocations).setCachePeriod(31556926);
		}
	}
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding("UTF-8");
		return resolver;
	}

	@Bean
	public TemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setEnableSpringELCompiler(true);
		engine.setTemplateResolver(templateResolver());
		return engine;
	}

	private ITemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix("/WEB-INF/templates/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		return resolver;
	}
}