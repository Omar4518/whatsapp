package com.example.whatsapp;

public class Contacts
{
    public String name,statues,image;

    public Contacts()
    {
    }

    public Contacts(String name, String statues, String image) {
        this.name = name;
        this.statues = statues;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatues() {
        return statues;
    }

    public void setStatues(String statues) {
        this.statues = statues;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
