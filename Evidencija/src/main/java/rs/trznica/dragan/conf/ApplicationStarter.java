package rs.trznica.dragan.conf;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import rs.trznica.dragan.forms.ApplicationFrame;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages={"rs.trznica.dragan.forms"}, basePackageClasses={AppConfig.class})
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
