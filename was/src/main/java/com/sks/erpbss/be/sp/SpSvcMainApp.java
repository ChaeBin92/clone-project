package com.sks.erpbss.be.sp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.sks.erpbss.be.sp"})
@EnableAsync
public class SpSvcMainApp {
    public static void main(String[] args) {
        SpringApplication.run(SpSvcMainApp.class, args);
    }
}

/*
 * 개선안(참고용, 실제 적용하지 않음):
 *
 * 1) 엔트리포인트 클래스 인스턴스화 방지
 * public final class SpSvcMainApp {
 *     private SpSvcMainApp() {
 *         throw new IllegalStateException("Utility class");
 *     }
 * }
 *
 * 2) 설정 의도를 Javadoc으로 명시
 * - SecurityAutoConfiguration / UserDetailsServiceAutoConfiguration 제외 이유
 * - @EnableAsync 사용 시 별도 TaskExecutor 구성 권장
 */
