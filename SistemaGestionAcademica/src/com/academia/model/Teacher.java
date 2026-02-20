package com.academia.model;

import java.io.Serializable;

/**
 * Represents a teacher who can be assigned to academic groups.
 */
public class Teacher implements Serializable, Cloneable {

    private String id;
    private String firstName;
    private String lastName;
    private String speciality;
    private String email;
    private String phone;

    public Teacher(String id, String firstName, String lastName,
                   String speciality, String email, String phone) {
        this.id          = id;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.speciality  = speciality;
        this.email       = email;
        this.phone       = phone;
    }

    @Override
    public Teacher clone() {
        try {
            return (Teacher) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error cloning Teacher", e);
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public String getId()                       { return id; }
    public void   setId(String id)              { this.id = id; }

    public String getFirstName()                        { return firstName; }
    public void   setFirstName(String firstName)        { this.firstName = firstName; }

    public String getLastName()                         { return lastName; }
    public void   setLastName(String lastName)          { this.lastName = lastName; }

    public String getFullName()                         { return firstName + " " + lastName; }

    public String getSpeciality()                       { return speciality; }
    public void   setSpeciality(String speciality)      { this.speciality = speciality; }

    public String getEmail()                    { return email; }
    public void   setEmail(String email)        { this.email = email; }

    public String getPhone()                    { return phone; }
    public void   setPhone(String phone)        { this.phone = phone; }

    @Override
    public String toString() {
        return getFullName() + " - " + speciality;
    }
}