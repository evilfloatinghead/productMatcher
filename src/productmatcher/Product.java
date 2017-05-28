/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package productmatcher;

import java.util.Date;

/**
 *
 * @author Chad
 */
public class Product {
    
    private String product_name;
    private String manufacturer;
    private String model;
    private Date announced_date;

    /**
     * @return the product_name
     */
    public String getProduct_name() {
        return product_name;
    }

    /**
     * @param product_name the product_name to set
     */
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    /**
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return the announced_date
     */
    public Date getAnnounced_date() {
        return announced_date;
    }

    /**
     * @param announced_date the announced_date to set
     */
    public void setAnnounced_date(Date announced_date) {
        this.announced_date = announced_date;
    }
    
}
