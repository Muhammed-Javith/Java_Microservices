package com.mj.payroll.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerDocConfig {

	@Bean
	public OpenAPI openAPI() {
		Server server = new Server();
		server.setUrl("http://localhost:8081");
		server.setDescription("Development");

		Contact myContact = new Contact();
		myContact.setName("Muhammed Javith");
		myContact.setEmail("mjavith@gmail.com");
		myContact.setUrl("https://www.linkedin.com/in/muhammedjavith-sde/");

		Info information = new Info().title("Payroll Management System API").version("v1.0")
				.description("This API exposes endpoints to manage  Employee_Payroll Microservice.").contact(myContact)
				.termsOfService("https://swagger.io/docs/")
				.license(new License().name("Apache 2.0").url("http://springdoc.org"));

		return new OpenAPI().info(information).servers(List.of(server));
	}

}
