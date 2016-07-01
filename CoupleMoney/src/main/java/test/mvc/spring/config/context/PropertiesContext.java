package test.mvc.spring.config.context;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

@Configuration
public class PropertiesContext {

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@Autowired
	private ResourceLoader resourceLoader;

	@PostConstruct
	public void addXmlProperties() throws Exception {
		addXmlProperties("classpath:default_properties.xml");
		addXmlProperties("classpath:api_properties.xml");
	}

	private void addXmlProperties(String location) throws IOException, InvalidPropertiesFormatException {
		Resource resource = resourceLoader.getResource(location);

		Properties properties = new Properties();
		properties.loadFromXML(resource.getInputStream());
		applicationContext.getEnvironment().getPropertySources().addLast(new PropertiesPropertySource(getNameForResource(resource), properties));
	}

	// from ResourcePropertySource
	private static String getNameForResource(Resource resource) {
		String name = resource.getDescription();
		if (!StringUtils.hasText(name)) {
			name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
		}
		return name;
	}
	
}
