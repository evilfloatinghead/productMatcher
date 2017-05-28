/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package productmatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * ProductFinder uses n-gram matching to find product matches.  It considers
 * the best and second best possible overlap of n-grams between product and
 * listing, takes their average, and gets the percentage of the top
 * possible n-gram match for the product.  Note, this will always be less than
 * a perfect match intentionally.
 * Furthermore, it considers the percentage overlap of all the combined n-grams
 * between product and listing, and multiplies this value to the matching score.
 * That is to penalize excessively long listings trying to match every keyword.
 * 
 * With a proper training set of listings and expected products, a machine
 * learning model (like an SVM) could be trained and tuned to get better
 * accuracy (precision and recall), but something like the output of this
 * utility could be used as a starting point.
 * 
 * @author Chad
 */
public class ProductFinder {

    // Lower these values to increase recall, but at the cost of precision
    public static final double MIN_SCORE_THRESHOLD = 0.12;
    public static final double MIN_GAP_FOR_TOP = 0.05;
    
    private final Map<Product, Features> m_featuresByProductMap;
    private final Logger log;
    
    public ProductFinder(Set<Product> products) {
        log = Logger.getLogger(ProductFinder.class.getName());
        m_featuresByProductMap = new HashMap<>();
        for (Product product : products) {
            m_featuresByProductMap.put(product, Features.getFeaturesForProduct(product));
        }
    }
    
    public Product getTopMatchingProduct(Listing listing) {
        double bestScore = MIN_SCORE_THRESHOLD;
        double secondBestScore = 0;
        double gapToTop = 0;
        Product bestProduct = null;
        Product secondBestProduct = null;
        Features listingFeatures = Features.getFeaturesForListing(listing);
        for (Product product : m_featuresByProductMap.keySet()) {
            Features features = m_featuresByProductMap.get(product);
            double score = features.getMatchingScore(listingFeatures);
            if (score > bestScore) {
                gapToTop = score - bestScore;
                secondBestScore = bestScore;
                secondBestProduct = bestProduct;
                bestScore = score;
                bestProduct = product;
            }
        }
        if (bestProduct != null) {
            log.fine(bestScore + " " + bestProduct.getProduct_name());
            if (secondBestProduct != null) {
                log.fine(secondBestScore + " " + secondBestProduct.getProduct_name());
            }
        }
        // If there's a number of results near the top, we aren't confident
        if (gapToTop < MIN_GAP_FOR_TOP) {
            // Favour precision over recall
            bestProduct = null;
        }
        
        return bestProduct;
    }
}
