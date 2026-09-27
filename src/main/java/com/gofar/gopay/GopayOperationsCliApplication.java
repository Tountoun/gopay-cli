package com.gofar.gopay;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
public class GopayOperationsCliApplication {

	public static void main(String[] args) {
		SpringApplication.run(GopayOperationsCliApplication.class, args);
	}

	@Bean
	public PromptProvider promptProvider() {
		return () -> new AttributedString("gopay-sh:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
	}
}
