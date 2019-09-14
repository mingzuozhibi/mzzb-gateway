package mingzuozhibi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "mingzuozhibi")
public class MzzbGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MzzbGatewayApplication.class, args);
    }
}
