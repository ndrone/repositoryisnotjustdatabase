package org.example.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.example.Contact;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

class MockDispatcher extends Dispatcher
{

    private ObjectMapper          objectMapper  = new ObjectMapper();
    private Map<Integer, Contact> savedContacts = new HashMap<>();

    @NotNull
    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException
    {

        if ("POST".equals(request.getMethod()))
        {
            return saveContact(request);
        }
        else
        {
            HttpUrl requestUrl = request.getRequestUrl();
            String id = requestUrl.queryParameter("id");
            String firstName = requestUrl.queryParameter("firstName");
            String lastName = requestUrl.queryParameter("lastName");
            System.out.println(requestUrl + id);
            if ("0".equals(id) || "1".equals(id))
            {
                return new MockResponse().setResponseCode(200);
            }
            else if (id != null)
            {
                Contact foundContact = savedContacts.get(Integer.valueOf(id));
                if (foundContact != null)
                {
                    try
                    {
                        return new MockResponse().setResponseCode(200)
                                .setBody(objectMapper.writeValueAsString(foundContact));
                    }
                    catch (JsonProcessingException e)
                    {
                        throw new InterruptedException(e.getMessage());
                    }
                }
            }
            else if ((firstName != null && !firstName.isBlank())
                    && (lastName != null && !lastName.isBlank()))
            {
				List<Contact> contacts = savedContacts.values().stream()
						.filter(contact -> firstName.equals(contact.getFirstName()) &&
								lastName.equals(contact.getLastName()))
						.collect(Collectors.toList());
				try
				{
					return new MockResponse().setResponseCode(200)
							.setBody(objectMapper.writeValueAsString(contacts));
				}
				catch (JsonProcessingException e)
				{
					throw new InterruptedException(e.getMessage());
				}
			}
			else if (firstName != null && !firstName.isBlank())
			{
				List<Contact> contacts = savedContacts.values().stream()
						.filter(contact -> firstName.equals(contact.getFirstName()))
						.collect(Collectors.toList());
				try
				{
					return new MockResponse().setResponseCode(200)
							.setBody(objectMapper.writeValueAsString(contacts));
				}
				catch (JsonProcessingException e)
				{
					throw new InterruptedException(e.getMessage());
				}
			}
			else if (lastName != null && !lastName.isBlank())
			{
				List<Contact> contacts = savedContacts.values().stream()
						.filter(contact -> lastName.equals(contact.getLastName()))
						.collect(Collectors.toList());
				try
				{
					return new MockResponse().setResponseCode(200)
							.setBody(objectMapper.writeValueAsString(contacts));
				}
				catch (JsonProcessingException e)
				{
					throw new InterruptedException(e.getMessage());
				}
			}
        }
        return new MockResponse().setResponseCode(404);
    }

    private MockResponse saveContact(RecordedRequest request) throws InterruptedException
    {
        String body = request.getBody().readUtf8();
        try
        {
            Contact contact = objectMapper.readValue(body, Contact.class);
            if (contact.getId() == null)
            {
                contact.setId(new Random().nextInt(Integer.MAX_VALUE));
                savedContacts.put(contact.getId(), contact);
                return new MockResponse().setResponseCode(200)
                        .setBody(objectMapper.writeValueAsString(contact));
            }
            else
            {
                Contact foundContact = savedContacts.get(contact.getId());
                if (foundContact != null)
                {
                    foundContact.setFirstName(contact.getFirstName());
                    foundContact.setLastName(contact.getLastName());
                    return new MockResponse().setResponseCode(200)
                            .setBody(objectMapper.writeValueAsString(foundContact));
                }
                else
                {
                    return new MockResponse().setResponseCode(400);
                }
            }
        }
        catch (IOException e)
        {
            throw new InterruptedException(e.getMessage());
        }
    }
}
