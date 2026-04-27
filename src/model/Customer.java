package model;

import java.sql.Date;

public class Customer {
    private int    customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private Date   joinDate;

    public Customer() {}

    public Customer(int customerId, String firstName, String lastName,
                    String email, String phone, String address, Date joinDate) {
        this.customerId = customerId;
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.email      = email;
        this.phone      = phone;
        this.address    = address;
        this.joinDate   = joinDate;
    }

    public int    getCustomerId() { return customerId; }
    public String getFirstName()  { return firstName; }
    public String getLastName()   { return lastName; }
    public String getEmail()      { return email; }
    public String getPhone()      { return phone; }
    public String getAddress()    { return address; }
    public Date   getJoinDate()   { return joinDate; }

    public void setCustomerId(int v)    { this.customerId = v; }
    public void setFirstName(String v)  { this.firstName = v; }
    public void setLastName(String v)   { this.lastName = v; }
    public void setEmail(String v)      { this.email = v; }
    public void setPhone(String v)      { this.phone = v; }
    public void setAddress(String v)    { this.address = v; }
    public void setJoinDate(Date v)     { this.joinDate = v; }
}
