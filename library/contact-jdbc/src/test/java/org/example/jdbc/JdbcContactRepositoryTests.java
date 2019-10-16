package org.example.jdbc;

import org.example.ContactRepositoryTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

class JdbcContactRepositoryTests extends ContactRepositoryTests {

	private List<ContactEntity> savedEntities;

	@BeforeEach
	void init() {
		savedEntities = new ArrayList<>();
		ContactEntityRepository contactEntityRepository = Mockito
				.mock(ContactEntityRepository.class);
		contactRepository = new JdbcContactRepository(contactEntityRepository);

		Mockito.when(contactEntityRepository.findById(Mockito.anyInt())).thenAnswer(invocationOnMock -> {
			Integer id = invocationOnMock.getArgument(0);
			for (ContactEntity entity : savedEntities) {
				if (entity.getId().equals(id)) {
					return Optional.of(entity);
				}
			}
			return Optional.empty();
		});

		Mockito.when(contactEntityRepository.findAllByFirstNameAndLastName(Mockito.any(), Mockito.any()))
				.thenAnswer(invocationOnMock -> {
					String firstName = invocationOnMock.getArgument(0);
					String lastName = invocationOnMock.getArgument(1);

					return savedEntities.stream()
							.filter(contactEntity -> contactEntity.getFirstName().equals(firstName)
									&& contactEntity.getLastName().equals(lastName))
							.collect(Collectors.toList());
				});

		Mockito.when(contactEntityRepository.findAllByFirstNameOrLastName(Mockito.any(), Mockito.any()))
				.thenAnswer(invocationOnMock -> {
					String firstName = invocationOnMock.getArgument(0);
					String lastName = invocationOnMock.getArgument(1);

					return savedEntities.stream()
							.filter(contactEntity -> contactEntity.getFirstName().equals(firstName)
									|| contactEntity.getLastName().equals(lastName))
							.collect(Collectors.toList());
				});

		Mockito.when(contactEntityRepository.save(Mockito.any(ContactEntity.class))).thenAnswer(invocationOnMock -> {
			ContactEntity entityToSave = invocationOnMock.getArgument(0);

			ContactEntity savedEntity = null;
			if (entityToSave.getId() == null) {
				Random random = new Random(System.currentTimeMillis());
				savedEntity = new ContactEntity(random.nextInt(), entityToSave.getFirstName(), entityToSave.getLastName());
				savedEntities.add(savedEntity);
			} else {
				for (ContactEntity entity : savedEntities) {
					if (entity.getId().equals(entityToSave.getId())) {
						entity.setFirstName(entityToSave.getFirstName());
						entity.setLastName(entityToSave.getLastName());
						savedEntity = entity;
					}
				}
			}
			return savedEntity;
		});
	}

	@Test
	void constructorException() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
				new JdbcContactRepository(null));
		Assertions.assertNotNull(exception);
	}
}