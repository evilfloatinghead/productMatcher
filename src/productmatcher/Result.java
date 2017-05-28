/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package productmatcher;

/**
 *
 * @author Chad
 */
public class Result {
    
    private String product_name;
    private Listing[] listings;

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
     * @return the listings
     */
    public Listing[] getListings() {
        return listings;
    }

    /**
     * @param listings the listings to set
     */
    public void setListings(Listing[] listings) {
        this.listings = listings;
    }
    
}
