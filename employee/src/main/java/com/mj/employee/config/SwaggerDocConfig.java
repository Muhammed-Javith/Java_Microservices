package com.mj.employee.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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

		Info information = new Info().title("Employee Management System API").version("v1.0")
				.description("This API exposes endpoints to manage employees.").contact(myContact)
				.termsOfService("https://swagger.io/docs/")
				.license(new License().name("Apache 2.0").url("http://springdoc.org"));

		// Define the security scheme (Bearer Token)
		SecurityScheme securityScheme = new SecurityScheme().name("Authorization").type(SecurityScheme.Type.HTTP)
				.scheme("bearer").bearerFormat("JWT");

		// Add the security scheme to the OpenAPI Components
		Components components = new Components().addSecuritySchemes("Authorization", securityScheme);

		// Add a security requirement referencing the scheme
		SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");

		return new OpenAPI().info(information).servers(List.of(server)).components(components)
				.addSecurityItem(securityRequirement);
	}

}
