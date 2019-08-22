package org.example.jdbc;

import org.example.ContactRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
public class JdbcContactConfiguration {

	@Bean
	public ContactRepository contactRepository(ContactEntityRepository contactEntityRepository) {
		return new JdbcContactRepository(contactEntityRepository);
	}
}
