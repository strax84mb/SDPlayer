package rs.trznica.dragan.conf;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import rs.trznica.dragan.forms.ApplicationFrame;

@SpringBootApplication
@EnableAutoConfiguration
@Import(AppConfig.class)
@ComponentScan(basePackages={"rs.trznica.dragan.forms", "rs.trznica.dragan.dao"})
public class ApplicationStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext ctx = new SpringApplicationBuilder(ApplicationStarter.class).headless(false).run();
		ApplicationFrame frame = ctx.getBean(ApplicationFrame.class);
		frame.setVisible(true);
	}

}
