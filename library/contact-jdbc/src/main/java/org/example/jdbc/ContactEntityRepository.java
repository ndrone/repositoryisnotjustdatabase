package org.example.jdbc;

import org.springframework.data.repository.CrudRepository;

interface ContactEntityRepository extends CrudRepository<ContactEntity, Integer> {

	Iterable<ContactEntity> findAllByFirstNameAndLastName(String firstName, String lastName);

	Iterable<ContactEntity> findAllByFirstNameOrLastName(String firstName, String lastName);
}
