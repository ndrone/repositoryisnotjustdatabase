package org.example.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;
import org.example.ContactRepositoryTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

class HttpContactRepositoryTests extends ContactRepositoryTests {

	private static MockWebServer server;
	private static String uri;

	private OkHttpClient okHttpClient = new OkHttpClient();
	private ObjectMapper objectMapper = new ObjectMapper();


	@BeforeAll
	static void setup() {
		server = new MockWebServer();
		try {
			server.start();
			uri = server.url("/api/contact").toString();
			server.setDispatcher(new MockDispatcher());
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail(e.getMessage());
		}
	}

	@BeforeEach
	void init() {
		contactRepository = new HttpContactRepository(okHttpClient, objectMapper, uri);
	}

	@AfterAll
	static void done() {
		try {
			server.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void missingOkHttpClient() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
				new HttpContactRepository(null, null, null));
		Assertions.assertNotNull(exception);
	}

	@Test
	void missingObjectMapper() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
				new HttpContactRepository(okHttpClient, null, null));
		Assertions.assertNotNull(exception);
	}

	private static Stream<Arguments> provideStringsForUriIsBlank() {
		return Stream.of(
				Arguments.of((String) null),
				Arguments.of(""),
				Arguments.of("  ")
		);
	}

	@ParameterizedTest
	@MethodSource("provideStringsForUriIsBlank")
	void missingOrIsBlankUri(String uri) {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> new HttpContactRepository(okHttpClient, objectMapper, uri));
		Assertions.assertNotNull(exception, "uri must have text");
	}

}
