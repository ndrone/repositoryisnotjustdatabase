package org.example.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import okhttp3.*;
import org.example.Contact;
import org.example.ContactException;
import org.example.ContactRepository;
import org.example.ContactRepositoryValidator;

import java.io.IOException;
import java.util.ArrayList;
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

		String responseBody = execute(request);
		if (responseBody != null && !responseBody.isBlank()) {
			try {
				return objectMapper.readValue(responseBody, Contact.class);
			} catch (IOException e) {
				throw new ContactException(e.getMessage(), e);
			}
		} else {
			return null;
		}
	}

	private String execute(Request request) {
		String body = null;
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException(
						String.format("Unexpected response code: %s", response.code()));
			}
			Optional<ResponseBody> responseBody = Optional.ofNullable(response.body());
			if (responseBody.isPresent()) {
				body = responseBody.get().string();
			}
		} catch (IOException e) {
			throw new ContactException(e.getMessage(), e);
		}
		return body;
	}

	@Override
	public Iterable<Contact> findContacts(String firstName, String lastName)
			throws IllegalArgumentException, ContactException {
		ContactRepositoryValidator.firstNameOrLastNameHasText(firstName, lastName);

		Request request = null;
		if (firstName != null && lastName != null) {
			request = new Request.Builder()
					.url(String.format("%s?firstName=%s&lastName=%s", uri, firstName, lastName))
					.build();
		} else if (firstName != null) {
			request = new Request.Builder()
					.url(String.format("%s?firstName=%s", uri, firstName)).build();
		} else if (lastName != null) {
			request = new Request.Builder()
					.url(String.format("%s?lastName=%s", uri, lastName)).build();
		}
		String responseBody = execute(request);
		if (responseBody != null && !responseBody.isBlank()) {
			CollectionType collectionType = objectMapper.getTypeFactory()
					.constructCollectionType(List.class, Contact.class);
			try {
				return objectMapper.readValue(responseBody, collectionType);
			} catch (IOException e) {
				throw new ContactException(e.getMessage(), e);
			}
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public Contact saveContact(Contact contact) throws
			IllegalArgumentException, ContactException {
		ContactRepositoryValidator.notNull(contact);
		Contact savedContact;
		if (contact.getId() != null) {
			Contact existingContact = findContact(contact.getId());
			if (existingContact == null) {
				throw new IllegalArgumentException(
						String.format("Contact with id: %s not found for updating",
								contact.getId()));
			}
			existingContact.setFirstName(contact.getFirstName());
			existingContact.setLastName(contact.getLastName());
		}
		try {
			Request request = new Request.Builder().url(String.format("%s", uri))
					.post(RequestBody.create(
							objectMapper.writeValueAsString(contact),
							MediaType.parse("application/json")))
					.build();
			String responseBody = execute(request);
			if (responseBody != null && !responseBody.isBlank()) {
				savedContact = objectMapper.readValue(responseBody, Contact.class);
			} else {
				savedContact = null;
			}
		} catch (IOException e) {
			throw new ContactException(e.getMessage(), e);
		}
		return savedContact;
	}
}
