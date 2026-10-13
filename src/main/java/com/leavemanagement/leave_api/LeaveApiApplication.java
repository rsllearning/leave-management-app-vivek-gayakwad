package com.leavemanagement.leave_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication (
    exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    }
)
public class LeaveApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeaveApiApplication.class, args);
	}

}
