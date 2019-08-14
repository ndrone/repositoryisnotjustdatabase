package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ContractTests {

	@Test
	void testContact() throws JsonProcessingException {
		String value = new ObjectMapper().writeValueAsString(new Contact(0, "first", "last"));
		Assertions.assertThat(value).contains("\"id\":");
		Assertions.assertThat(value).contains("\"firstName\":");
		Assertions.assertThat(value).contains("\"lastName\":");
	}
}
