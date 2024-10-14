package com.connectly.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.UUID;

import com.connectly.entities.Contact;
import com.connectly.entities.User;
import com.connectly.helpers.ResourceNotFoundException;
import com.connectly.repositories.ContactRepo;
import com.connectly.services.ContactService;

@Service
public class ContactServiceImpl implements ContactService{

    @Autowired
    private ContactRepo contactRepo;

    @Override
    public Contact saveContact(Contact contact) {

        String contactId = UUID.randomUUID().toString();
        contact.setContactId(contactId);
        return contactRepo.save(contact);
    }

    @Override
    public Contact updateContact(Contact contact) {
       
        var oldContact = contactRepo.findById(contact.getContactId())
                .orElseThrow(()-> new ResourceNotFoundException("Contact not found"));

                oldContact.setName(contact.getName());
                oldContact.setEmail(contact.getEmail());
                oldContact.setPhoneNumber(contact.getPhoneNumber());
                oldContact.setAddress(contact.getAddress());
                oldContact.setDescription(contact.getDescription());
                oldContact.setPicture(contact.getPicture());
                oldContact.setFavourite(contact.isFavourite());
                oldContact.setWebsiteLink(contact.getWebsiteLink());
                oldContact.setLinkedInLink(contact.getLinkedInLink());
                oldContact.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());

                return contactRepo.save(oldContact);
        
    }

    @Override
    public List<Contact> getAllContacts() {
       return contactRepo.findAll();
    }

    @Override
    public Contact getContactById(String id) {
        return contactRepo.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Contact not found with given ID: "+ id));
    }

    @Override
    public void deleteContact(String id) {
       var contact = contactRepo.findById(id).orElseThrow(
        () -> new ResourceNotFoundException("Contact not found with given ID: "+ id));

        contactRepo.delete(contact);
    }

    @Override
    public List<Contact> getByUserId(String userId) {
       return contactRepo.findByUserId(userId);
    }

    @Override
    public Page<Contact> searchByName(String nameKeyword, int page, int size, String sortBy, String sortDirection, User user) {
        Sort sort = sortDirection.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);        
        return contactRepo.findByUserAndNameContaining(user, nameKeyword, pageable);
   }

    @Override
    public Page<Contact> searchByEmail(String emailKeyword, int page, int size, String sortBy, String sortDirection, User user) {
        Sort sort = sortDirection.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);        
        return contactRepo.findByUserAndEmailContaining(user, emailKeyword, pageable);        
    }

    @Override
    public Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, int page, int size, String sortBy,
            String sortDirection, User user) {
        Sort sort = sortDirection.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);        
        return contactRepo.findByUserAndPhoneNumberContaining(user, phoneNumberKeyword, pageable);                
        
    }

    @Override
    public Page<Contact> getByUser(User user, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);
        return contactRepo.findByUser(user, pageable);
    }
    

}
