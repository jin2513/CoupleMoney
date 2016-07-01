package test.mvc.spring.junit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import test.mvc.spring.config.WebAppInitializer;
import test.mvc.spring.config.servlet.WebMvcConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes={WebAppInitializer.class, WebMvcConfig.class})
public class AppTest {
	private static Logger LOG = LoggerFactory.getLogger(AppTest.class);
	
	@Before
	public void init() {
	}
	
	@Test
	public void test() {
		LOG.info("test start");
		
		LOG.info("test end");
	}
}
