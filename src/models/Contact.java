package models;

public class Contact {
    private int id;
    private String name;
    private String email;

    /**
     * Constructor for the Contact Model class
     * @param id Primary Key ID of contact
     * @param name Name of contact
     * @param email Email address of contact
     */
    public Contact(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Gets contact email
     * @return string of email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets contact email
     * @param email string of email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets contact name
     * @return string of name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets contact name
     * @param name string of name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets contact id
     * @return int of id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets contact id
     * @param id int of id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * returns name as Contact toString override
     * @return string from getName()
     */
    @Override
    public String toString(){
        return getName();
    }
}
