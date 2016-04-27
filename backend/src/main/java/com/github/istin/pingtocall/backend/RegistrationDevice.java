package com.github.istin.pingtocall.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/** The Objectify object model for device registrations we are persisting */
@Entity
public class RegistrationDevice {

    @Id
    Long id;

    @Index
    private String regId;

    @Index
    private String email;

    @Index
    private String pin;

    // you can add more fields...

    public RegistrationDevice() {}

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String pEmail) {
        email = pEmail;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pPin) {
        pin = pPin;
    }
}