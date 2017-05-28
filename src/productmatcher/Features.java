/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package productmatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Chad
 */
public class Features {

    public static final int MAX_NGRAM_SIZE = 3;
    public static final String[] stopWords = {"\\bin\\b","\\bof\\b","\\bthe\\b",
        "\\bor\\b","\\bfor\\b","\\bzoom\\b","\\bdigital\\b"};
    
    private final Set<Feature> m_featureSet;
    private final String m_manufacturer;
    
    private final Logger log;

    private Features(String manufacturer, Set<Feature> featureSet) {
        log = Logger.getLogger(Features.class.getName());
        m_manufacturer = manufacturer;
        m_featureSet = featureSet;
    }
    
    @Override
    public String toString() {
        return "[" + StringUtils.join(m_featureSet, ",") + "]";
    }
    
    public String getManufacturer() {
        return m_manufacturer;
    }
    
    public double getMatchingScore(Features otherFeatures) {
        double score = 0;
        double topScore = 0;
        double secondTopScore = 0;
        int matchedFeatures = 0;
        int unmatchedFeatures = 0;
        boolean allMatched = true;
        int longestFeature = 1;
        // If the manufacturer doesn't match, don't even try feature matching
        if (StringUtils.equals(m_manufacturer, otherFeatures.m_manufacturer)) {
            for (Feature feature : m_featureSet) {
                boolean matched = false;
                for (Feature otherFeature : otherFeatures.getFeatureSet()) {
                    double matchingScore = feature.getMatchingScore(otherFeature);
                    if (matchingScore > 0) {
                        matched = true;
                        if (matchingScore > topScore) {
                            secondTopScore = topScore;
                            topScore = matchingScore;
                        }
                        matchedFeatures++;
                    } else {
                        unmatchedFeatures++;
                    }
                }
                // Every atomic feature must be matched to get any score
                if (feature.size()==1) {
                    allMatched = allMatched && matched;
                } else {
                    // Track the length of the longest feature
                    if (feature.size() > longestFeature) {
                        longestFeature = feature.size();
                    }
                }
            }
            if (!allMatched) {
                score = 0;
            } else {
                double coverage = (double)(matchedFeatures + 1) / 
                        (double)(matchedFeatures + unmatchedFeatures + 1);
                double topAverage = ((topScore + secondTopScore) / 2);
                double topAverageOverMax = topAverage / 
                        (double) Math.pow(longestFeature, 2);
                // Score = the average top two scores over the maximum possible
                //         score multiplied by the percentage of features matched
                score =  topAverageOverMax * coverage;
            }
        }
        return score;
    }

    public Set<Feature> getFeatureSet() {
        return m_featureSet;
    }
    
    static public Features combineFeatures(Features f1, Features f2) {
        Set<Feature> combinedFeatureSet = new HashSet<>();
        combinedFeatureSet.addAll(f1.getFeatureSet());
        combinedFeatureSet.addAll(f2.getFeatureSet());
        return new Features(f1.getManufacturer(), combinedFeatureSet);
    }
    
    static public Features getFeaturesForProduct(Product product) {
        Features features = getFeaturesForString(product.getManufacturer(),
                product.getProduct_name());
        features = combineFeatures(features, 
                getFeaturesForString(product.getManufacturer(),
                        product.getModel()));
        features = combineFeatures(features,
                getFeaturesForString(product.getManufacturer(),
                        product.getManufacturer()));
        return features;
    }

    static public Features getFeaturesForListing(Listing listing) {
        Features features = getFeaturesForString(listing.getManufacturer(),
                listing.getTitle());
        features = combineFeatures(features, 
                getFeaturesForString(listing.getManufacturer(),
                        listing.getManufacturer()));
        return features;
    }
    
    static public Features getFeaturesForString(String manufacturer, String s) {
        Set<Feature> featureSet = new HashSet<>();
        String normalizedString = normalizeString(s);
        if (!normalizedString.isEmpty()) {
            String[] splitString = StringUtils.split(normalizedString);
            // Each n-gram will be a feature
            int start = 0;
            int end = 0;
            // Walk the counters along the length of the splitString to make ngrams
            while (start < splitString.length) {
                featureSet.add(new Feature(
                        getArrayElementsAsList(splitString, start, end)));
                if (end < splitString.length - 1) {
                    end++;
                } else {
                    start++;
                }
            }
        }
        return new Features(manufacturer, featureSet);
    }

    static public List<String> getArrayElementsAsList(String[] stringArray, int start, int end) {
        List<String> stringList = new ArrayList<>(end-start+1);
        for (int i = start; i <= end; i++) {
            stringList.add(stringArray[i]);
        }
        return stringList;
    }
    
    public static List<String> splitStringToList(String s) {
        String[] splitString = StringUtils.split(s);
        return Arrays.asList(splitString);
    }
    
    public static String normalizeString(String s) {
        String normalString = StringUtils.lowerCase(s);
        normalString = normalString.replaceAll("\\p{P}", " ");
        for (String stopWord : stopWords) {
            normalString = normalString.replaceAll(stopWord, " ");
        }
        return normalString.trim();
    }
    
}
