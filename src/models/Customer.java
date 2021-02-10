package models;

public class Customer {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionId;
    private Division division;
    private String divisionName;
    private String countryName;

    /**
     * Constructor for the Customer Model class
     * @param id Primary Key ID of customer
     * @param name Name of customer
     * @param address Address of customer
     * @param postalCode Postal Code of customer
     * @param phone Phone number of customer
     * @param divisionId Division_ID of customer
     */
    public Customer(
            int id,
            String name,
            String address,
            String postalCode,
            String phone,
            int divisionId)
    {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
    }

    /**
     * Gets customer id
     * @return int of id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets customer id
     * @param id int of id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets customer name
     * @return string of name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets customer name
     * @param name string of name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets customer address
     * @return string of address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets customer address
     * @param address string of address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets customer postal code
     * @return string of postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets customer postal code
     * @param postalCode string of postalCode to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets customer phone
     * @return string of phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets customer phone
     * @param phone string of phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets customer division id
     * @return int of Division_ID
     */
    public int getDivisionId() {
        return divisionId;
    }

    /**
     * Sets customer name
     * @param divisionId int of divisionId to set
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    /**
     * Gets customer division name
     * @return string of Division Name
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * Sets customer name
     * @param divisionName String of divisionName to set
     */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * Gets customer country name
     * @return string of country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Sets country name
     * @param countryName String of countryName to set
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**
     * Gets customer division
     * @return Division object
     */
    public Division getDivision() {
        return division;
    }

    /**
     * Sets customer division
     * @param division Division object to set
     */
    public void setDivision(Division division) {
        this.division = division;
    }

    /**
     * Sets customer country
     * @param country Country object to set
     */
    public void setCountry(Country country) {
        this.division.setCountry(country);
    }

    /**
     * returns name as Customer toString override
     * @return string from getName()
     */
    @Override
    public String toString(){
        return getName();
    }
}
