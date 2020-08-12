package com.hey.common;

import java.util.List;

public class ContactList extends MessageContent{
    private List<Client> contacts;

    public ContactList(List<Client> contacts) {
        this.contacts = contacts;
    }

    public List<Client> getContacts() {
        return contacts;
    }

    public void setContacts(List<Client> contacts) {
        this.contacts = contacts;
    }
}
