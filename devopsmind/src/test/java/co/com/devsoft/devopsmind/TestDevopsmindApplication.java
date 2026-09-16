package co.com.devsoft.devopsmind;

import org.springframework.boot.SpringApplication;

public class TestDevopsmindApplication {

	public static void main(String[] args) {
		SpringApplication.from(DevopsmindApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
