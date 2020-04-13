package de.lall.tomgrabber;

import de.lall.tomgrabber.service.GrabberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TomgrabberApplication implements CommandLineRunner {

	private final GrabberService grabberService;

	public TomgrabberApplication(GrabberService grabberService) {
		this.grabberService = grabberService;
	}

	@Override
	public void run(String... args) throws Exception {
		grabberService.test();
	}

	public static void main(String[] args) {
		SpringApplication.run(TomgrabberApplication.class, args);
	}
}
