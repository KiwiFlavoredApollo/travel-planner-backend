package com.travelplanner.demo.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Boot가 application.yml을 파싱하기 전에
 * .env 파일을 읽어 시스템 프로퍼티/환경변수로 등록합니다.
 * 이렇게 하면 ${JWT_SECRET_KEY} 같은 placeholder가 정상 치환됩니다.
 */
public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String DOTENV_PROPERTY_SOURCE_NAME = "dotenv";
    private static final String[] POSSIBLE_LOCATIONS = {
            "./.env",                          // 프로젝트 루트 (gradlew 실행 시)
            "../.env",                         // 상위 디렉토리
            System.getProperty("user.dir") + "/.env",  // user.dir 기준
            new File("").getAbsolutePath() + "/.env"   // 현재 디렉토리 기준
    };

    // Static initializer to verify class loading
    static {
        System.out.println(">>>> [DotEnvEnvironmentPostProcessor] Class loaded");
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println(">>>> [DotEnvEnvironmentPostProcessor] postProcessEnvironment called");

        Dotenv dotenv = null;
        String loadedFrom = null;

        // 여러 위치에서 .env 파일 탐색
        for (String location : POSSIBLE_LOCATIONS) {
            File file = new File(location);
            File dir = file.getParentFile();
            String filename = file.getName();

            System.out.println(">>>> [DotEnvEnvironmentPostProcessor] Trying: " + location + " (dir exists: " + (dir != null && dir.exists()) + ")");

            if (dir != null && dir.exists() && dir.isDirectory()) {
                try {
                    dotenv = Dotenv.configure()
                            .directory(dir.getAbsolutePath())
                            .filename(filename)
                            .ignoreIfMissing()
                            .load();

                    if (!dotenv.entries().isEmpty()) {
                        loadedFrom = location;
                        System.out.println(">>>> [DotEnvEnvironmentPostProcessor] Found .env at: " + location);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(">>>> [DotEnvEnvironmentPostProcessor] Error loading from " + location + ": " + e.getMessage());
                }
            }
        }

        if (dotenv == null || dotenv.entries().isEmpty()) {
            System.out.println(">>>> [DotEnvEnvironmentPostProcessor] .env file not found in any location, skipping");
            return;
        }

        // MapPropertySource로 환경에 추가 (최우선 순위)
        Map<String, Object> properties = new HashMap<>();
        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            properties.put(key, value);
            // System.setProperty도 해두면 @Value("${key}") 등에서도 참조 가능
            System.setProperty(key, value);
        });

        environment.getPropertySources().addFirst(
                new MapPropertySource(DOTENV_PROPERTY_SOURCE_NAME, properties)
        );

        System.out.println(">>>> [DotEnvEnvironmentPostProcessor] Loaded .env properties from " + loadedFrom + ": " + properties.keySet());
    }
}
