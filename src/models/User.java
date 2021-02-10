package models;

public class User {
    private int id;
    private String userName;
    private String password;

    /**
     * Constructor for the User Model class
     * @param id Primary Key ID of user
     * @param userName UserName of user
     * @param password Password of user
     */
    public User(int id,
                String userName,
                String password){
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Gets user id
     * @return int of id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets user id
     * @param id int of id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets user userName
     * @return string of userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets user userName
     * @param userName string of userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets user password
     * @return string of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets user password
     * @param password string of password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
