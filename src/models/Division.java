package models;

public class Division {
    private int id;
    private String name;
    private int countryId;
    private String countryName;
    private Country country;

    /**
     * Constructor for the Division Model class
     * @param id Primary Key ID of division
     * @param name Name of division
     * @param countryId Country_ID of division
     */
    public Division(int id,
                    String name,
                    int countryId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
    }

    /**
     * Gets division id
     * @return int of id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets division id
     * @param id int of id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets division name
     * @return string of name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets division name
     * @param name string of name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets division country id
     * @return int of country id
     */
    public int getCountryId() {
        return countryId;
    }

    /**
     * Sets division country id
     * @param countryId int of id to set
     */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /**
     * Gets division country name
     * @return string of country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Sets division country name
     * @param countryName string of country name to set
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**
     * Gets division country
     * @return country object of country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Sets division country
     * @param country Country object to set
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * returns name as Division toString override
     * @return string from getName()
     */
    @Override
    public String toString(){
        return getName();
    }
}
