import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


//引入配置事物的xml文件
//@ImportResource("classpath*:config/transaction.xml")
//@ServletComponentScan
@SpringBootApplication(scanBasePackages = { "com.common.util", "com" },exclude = { SecurityAutoConfiguration.class })//@PropertySource(value = "classpath:application-outSystem.properties")
//@PropertySource(value = "classpath:application-outSystem.properties")
@EnableTransactionManagement(order = 1)
@EnableScheduling
public class RdexpenseApplication {

	public static Logger logger = Logger.getLogger(RdexpenseApplication.class);

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(RdexpenseApplication.class);
		application.run(args);
		logger.info("=========启动结束---RdexpenseApplication=========");
	}

}
