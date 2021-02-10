package models;

public class Country {
    private int id;
    private String name;

    /**
     * Constructor for the Country Model class
     * @param id Primary Key ID of country
     * @param name Name of country
     */
    public Country(int id,
                   String name)
    {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets country id
     * @return int of id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets country id
     * @param id int of id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets country name
     * @return string of name
     */
    public String getName() {
        if(name != null){
            return name;
        }
        return "";
    }

    /**
     * Sets country name
     * @param name string of name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns name as Country toString override
     * @return string from getName()
     */
    @Override
    public String toString() {
        return getName();
    }
}
