package models;

public class Appointment {
    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private String startTime;
    private String  endTime;
    private int customerId;
    private int contactId;
    private Contact contact;

    /**
     * Constructor for the Appointment Model class
     * @param id Primary Key ID of appointment
     * @param title Title of appointment
     * @param description Description of appointment
     * @param location Location of the appointment
     * @param type Type of Appointment
     * @param startTime Appointment Start Time
     * @param endTime Appointment End Time
     * @param customerId Associated Customer ID
     * @param contactId Associated Contact Id
     */
    public Appointment(int id,
                       String title,
                       String description,
                       String location,
                       String type,
                       String startTime,
                       String endTime,
                       int customerId,
                       int contactId)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerId = customerId;
        this.contactId = contactId;
    }

    /**
     * Gets appointment title
     * @return string of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets appointment title
     * @param title string of title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets appointment description
     * @return string of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets appointment description
     * @param description string of description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets appointment location
     * @return string of location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets appointment location
     * @param location string of location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets appointment type
     * @return string of type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets appointment type
     * @param type string of type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets appointment startTime
     * @return string of startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets appointment startTime
     * @param startTime string of startTime to set
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets appointment Id
     * @return int of Appointment_ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets appointment id
     * @param id int of Appointment_ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets appointment endTime
     * @return string of endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets appointment endTime
     * @param endTime string of endTime to set
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets appointment customerId
     * @return int of Customer_ID
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets Customer id
     * @param customerId int of Customer_ID to set
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets appointment contactId
     * @return string of Contact_ID
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Sets Contact id
     * @param contactId int of Contact_ID to set
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Gets appointment Contact object
     * @return Contact Entity of Contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Sets Contact Object
     * @param contact object of Contact to set
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * Gets appointment Contact Name
     * @return string of Contact entity's name
     */
    public String getContactName(){
        if(contact != null){
            return contact.getName();
        }
        return "";
    }
}
