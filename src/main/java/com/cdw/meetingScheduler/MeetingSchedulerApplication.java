package com.cdw.meetingScheduler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MeetingSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetingSchedulerApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(String args[]) {
		return runner -> { System.out.println("Welcome to CDW Meeting Scheduler !"); };
	}

}
