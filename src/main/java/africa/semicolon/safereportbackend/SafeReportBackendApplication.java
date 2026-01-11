package africa.semicolon.safereportbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SafeReportBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafeReportBackendApplication.class, args);
    }

}
