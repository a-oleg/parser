package com.github.a_oleg.textloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.github.a_oleg.textloader.models", "com.github.a_oleg.textloader"})
public class TextLoaderApplication {

    public static void main(String[] args) {
            SpringApplication.run(TextLoaderApplication.class, args);
    }

}
