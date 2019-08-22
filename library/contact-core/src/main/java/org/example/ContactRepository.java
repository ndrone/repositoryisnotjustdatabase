package org.example;

public interface ContactRepository {

	/**
	 * Search for the contact with the existing <code>id</code>
	 *
	 * @param id of the contact to be searched for
	 * @return <code>contact</code> maybe <code>null</code> if <code>contact</code>
	 * with the <code>id</code> doesn't exist
	 * @throws IllegalArgumentException if <code>id</code> is <code>null</code>
	 * @throws ContactException         if an error has occurred
	 */
	Contact findContact(Integer id) throws IllegalArgumentException, ContactException;

	/**
	 * Searching for <code>contact</code> that exactly matches the parameters
	 * passed into the method.
	 *
	 * @param firstName to be exactly searched with
	 * @param lastName  to be exactly searched with
	 * @return a collection of <code>contact</code> that match the parameters
	 * @throws IllegalArgumentException if both parameters are <code>null</code>
	 * @throws ContactException         if an error has occurred
	 */
	Iterable<Contact> findContacts(String firstName, String lastName)
			throws IllegalArgumentException, ContactException;

	/**
	 * Saves a new or updated contact information. If a <code>contact</code> is passed with the
	 * <code>id</code> is <code>null</code>. The contact will be treated as a new contact, and
	 * an <code>id</code> will be generated for it. If an <code>contact</code> has an existing
	 * <code>id</code> the <code>contact</code> with be updated.
	 *
	 * @param contact to be saved or updated
	 * @return saved <code>contact</code>
	 * @throws IllegalArgumentException if a <code>contact</code> with an existing <code>id</code>
	 *                                  can not be found.
	 * @throws ContactException         if an error has occurred
	 */
	Contact saveContact(Contact contact) throws IllegalArgumentException, ContactException;
}
