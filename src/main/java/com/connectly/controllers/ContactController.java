package com.connectly.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.connectly.entities.Contact;
import com.connectly.entities.User;
import com.connectly.forms.ContactForm;
import com.connectly.forms.ContactSearchForm;
import com.connectly.helpers.AppConstants;
import com.connectly.helpers.Helper;
import com.connectly.helpers.Message;
import com.connectly.helpers.MessageType;
import com.connectly.services.ContactService;
import com.connectly.services.ImageService;
import com.connectly.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    private Logger logger = org.slf4j.LoggerFactory.getLogger(ContactController.class);

    //Add contact page handler
    @GetMapping("/add")
    public String addContactView(Model model){

        ContactForm contactForm = new ContactForm();
        
        //contactForm.setName("Nidhi");
        //contactForm.setFavourite(true);
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    @PostMapping("/add")
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm,
                                BindingResult bindingResult,
                                Authentication authentication, HttpSession session){

        //validate form data
        if(bindingResult.hasErrors()){

           //rBindingResult.getAllErrors().forEach(error -> logger.info(error.toString()));
            session.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());
            return "user/add_contact";
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);    
       
        //Save to db:contactform --> contact
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setFavourite(contactForm.isFavourite());
        contact.setAddress(contactForm.getAddress());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setDescription(contactForm.getDescription());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setUser(user);

       if(contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()){

        //process the contact picture->upload to db
        String filename = UUID.randomUUID().toString();
        String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
        contact.setPicture(fileURL);
        contact.setCloudinaryImagePublicId(filename);
       }

        contactService.saveContact(contact);

        //System.out.println("Contact saved to db");

        //Process the form data
        //System.out.println(contactForm);

        //set contact picture url


        //set messages to be displayed on the view
        session.setAttribute("message", Message.builder()
                    .content("Your contact has been added successfully.")
                    .type(MessageType.green)
                    .build());

        return "redirect:/user/contacts/add";

    }

    @GetMapping()
    public String viewContacts(
        @RequestParam(value = "page",defaultValue = "0") int page,
        @RequestParam(value = "size",defaultValue = AppConstants.PAGE_SIZE+"") int size,
        @RequestParam(value = "sortBy",defaultValue = "name") String sortBy,
        @RequestParam(value = "direction",defaultValue = "asc") String direction,
        Model model, Authentication authentication){
        //Load all the user contacts
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        Page<Contact> pageContact = contactService.getByUser(user, page, size,sortBy, direction);
        
        model.addAttribute("pageContact", pageContact);
        model.addAttribute("contactSearchForm", new ContactSearchForm());
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        return "user/contacts";
    }

    @GetMapping("/search")
    public String searchHandler(
        @ModelAttribute ContactSearchForm contactSearchForm,
        @RequestParam(value = "size",defaultValue = AppConstants.PAGE_SIZE+"") int size,
        @RequestParam(value = "page",defaultValue = "0") int page,
        @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
        @RequestParam(value = "direction", defaultValue = "asc") String direction,
        Model model,
        Authentication authentication
        ) {

            logger.info("field {} keyword {}",contactSearchForm.getField(),contactSearchForm.getKeyword());
            var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

            Page<Contact> pageContact = null;
            if(contactSearchForm.getField().equalsIgnoreCase("name")){
               pageContact = contactService.searchByName(contactSearchForm.getKeyword(), page, size, sortBy, direction, user);
            }
            else if(contactSearchForm.getField().equalsIgnoreCase("email")){
                pageContact = contactService.searchByEmail(contactSearchForm.getKeyword(), page, size, sortBy, direction, user);
             }
             else if(contactSearchForm.getField().equalsIgnoreCase("phoneNumber")){
                pageContact = contactService.searchByPhoneNumber(contactSearchForm.getKeyword(), page, size, sortBy, direction, user);
             }

             logger.info("pageContact {}", pageContact);
             model.addAttribute("contactSearchForm", contactSearchForm);
             model.addAttribute("pageContact", pageContact);
             model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

            return "user/search";
    }
    

    @GetMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable("contactId") String contactId, HttpSession session) {

        contactService.deleteContact(contactId);
        logger.info("contactId= {} deleted", contactId);

        session.setAttribute("message",
            Message.builder()
                    .content("Contact is deleted successfully!")
                    .type(MessageType.green)
                    .build()
            );

        return "redirect:/user/contacts";
    }


    @GetMapping("/view/{contactId}")
    public String updateContactFormView(
        @PathVariable("contactId") String contactId,
        Model model){

            var contact = contactService.getContactById(contactId);
            ContactForm contactForm = new ContactForm();
            contactForm.setName(contact.getName());
            contactForm.setEmail(contact.getEmail());
            contactForm.setFavourite(contact.isFavourite());
            contactForm.setAddress(contact.getAddress());
            contactForm.setPhoneNumber(contact.getPhoneNumber());
            contactForm.setDescription(contact.getDescription());
            contactForm.setLinkedInLink(contact.getLinkedInLink());
            contactForm.setWebsiteLink(contact.getWebsiteLink());
            contactForm.setPicture(contact.getPicture());
           
            model.addAttribute("contactForm", contactForm);
            model.addAttribute("contactId", contactId);

            return "user/update_contact_view";
    }


    @PostMapping("/update/{contactId}")
    public String updateContact(@PathVariable("contactId") String contactId,
                 @Valid @ModelAttribute ContactForm contactForm,
                 BindingResult bindingResult, 
                 Model model) {

        if (bindingResult.hasErrors()) {
            return "user/update_contact_view";         
        }

        //update the contact
        var contact = contactService.getContactById(contactId);
        contact.setContactId(contactId);
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavourite(contactForm.isFavourite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());

        //process image
        if(contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty() ){
            logger.info("File is not empty");
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            contact.setCloudinaryImagePublicId(fileName);
            contact.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);
        }
        else{
            logger.info("File is empty");
        }

        var updatedContact = contactService.updateContact(contact);
        logger.info("Updated contact {}",updatedContact);
        model.addAttribute("message", 
                Message.builder()
                .content("Contact updated successfully..")
                .type(MessageType.green)
                .build());

        return "redirect:/user/contacts/view/"+contactId;
    }
    
    

}
