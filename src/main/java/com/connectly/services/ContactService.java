package com.connectly.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.connectly.entities.Contact;
import com.connectly.entities.User;

@Service
public interface ContactService {

    Contact saveContact(Contact contact);

    Contact updateContact(Contact contact);

    List<Contact> getAllContacts();

    Contact getContactById(String id);

    void deleteContact(String id);

    Page<Contact> searchByName(String nameKeyword, int page, int size, String sortBy, String sortDirection, User user);

    Page<Contact> searchByEmail(String emailKeyword, int page, int size, String sortBy, String sortDirection, User user);

    Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, int page, int size, String sortBy, String sortDirection, User user);

    List<Contact> getByUserId(String userId);

    Page<Contact> getByUser(User user, int page, int size, String sortBy, String sortDirection);

}
