package org.example.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.example.Contact;
import org.example.ContactException;
import org.example.ContactRepository;
import org.example.ContactRepositoryValidator;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

class HttpContactRepository implements ContactRepository {

	private final OkHttpClient client;
	private final ObjectMapper objectMapper;
	private final String uri;

	HttpContactRepository(OkHttpClient client, ObjectMapper objectMapper, String uri) {
		if (client == null) {
			throw new IllegalArgumentException("client must not be null");
		}
		this.client = client;

		if (objectMapper == null) {
			throw new IllegalArgumentException("objectMapper must not be null");
		}
		this.objectMapper = objectMapper;

		if (uri == null || uri.isBlank()) {
			throw new IllegalArgumentException("uri must not be null");
		}
		this.uri = uri;
	}

	@Override
	public Contact findContact(Integer id) throws IllegalArgumentException, ContactException {
		ContactRepositoryValidator.notNull(id);

		Request request = new Request.Builder().url(String.format("%s?id=%s", uri, id)).build();
		return execute(request, Contact.class);
	}

	private <T> T execute(Request request, Class<T> responseType) {
		T contact = null;
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException(String.format("Unexpected response code: %s", response.code()));
			}
			Optional<ResponseBody> responseBody = Optional.ofNullable(response.body());
			if (responseBody.isPresent()) {
				String body = responseBody.get().string();
				if (!body.isBlank()) {
					contact = objectMapper.readValue(body, responseType);
				}
			}
		} catch (IOException e) {
			throw new ContactException(e.getMessage(), e);
		}
		return contact;
	}

	@Override
	public Iterable<Contact> findContacts(String firstName, String lastName)
			throws IllegalArgumentException, ContactException {
		ContactRepositoryValidator.firstNameOrLastNameHasText(firstName, lastName);

		Request request = new Request.Builder()
				.url(String.format("%s?firstName=%s&lastName=%s", uri, firstName, lastName)).build();
		return execute(request, List.class);
	}

	@Override
	public Contact saveContact(Contact contact) throws IllegalArgumentException, ContactException {
		ContactRepositoryValidator.notNull(contact);

		Contact contactToSave = contact;
		if (contactToSave.getId() != null) {
			Contact existingContact = findContact(contactToSave.getId());
			if (existingContact == null) {
				throw new IllegalArgumentException(
						String.format("Contact with id: %s not found for updating", contactToSave.getId()));
			}
			existingContact.setFirstName(contactToSave.getFirstName());
			existingContact.setLastName(contactToSave.getLastName());
		}
		try {
			Request request = new Request.Builder().url(String.format("%s", uri))
					.post(RequestBody.create(
							objectMapper.writeValueAsString(contactToSave), MediaType.parse("application/json")))
					.build();
			return execute(request, Contact.class);
		} catch (JsonProcessingException e) {
			throw new ContactException(e.getMessage(), e);
		}
	}
}
