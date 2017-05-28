/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package productmatcher;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Chad
 */
public class ProductMatcher {

    private final Gson m_gson;
    private final Set<Product> m_productSet;
    private final List<Listing> m_listings;
    private final ProductFinder m_productFinder;
    private final Logger log;
    
    public ProductMatcher(String productFilename, String listingsFilename) throws IOException {
        log = Logger.getLogger(ProductMatcher.class.getName());
        m_gson = new Gson();
        m_productSet = readProductsFromJsonFile(productFilename);
        m_listings = readListingsFromJsonFile(listingsFilename);
        m_productFinder = new ProductFinder(m_productSet);
    }
    
    /**
     * @param args the command line arguments
     * @throws org.apache.commons.cli.ParseException
     */
    public static void main(String[] args) throws ParseException, IOException {
        Options options = ProductMatcher.getOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (!ProductMatcher.validateCommandLine(cmd)) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp("Usage: java -jar productMatcher.jar [options]", options);
        } else {
            ProductMatcher instance = new ProductMatcher(
                cmd.getOptionValue('p'), cmd.getOptionValue('l'));
            List<Result> resultList = instance.getMatchedListings();
            instance.writeResultsToJsonFile(resultList, cmd.getOptionValue('r'));
        }
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("h", false, "this help");
        options.addOption("l", true, "listings file");
        options.addOption("p", true, "products file");
        options.addOption("r", true, "results file");
        return options;
    }

    private static boolean validateCommandLine(CommandLine cmd) {
        boolean okay = true;
        if (cmd.hasOption('h') || cmd.hasOption('?')) {
            okay = false;
        } else {
            String[] mandatoryParameters = {"p", "l", "r"};
            for (String parameter : mandatoryParameters) {
                if (!cmd.hasOption(parameter)) {
                    System.err.println("ERROR: Missing parameter " + parameter
                        + ", " + getOptions().getOption(parameter).getDescription());
                    okay = false;
                }
            }
        }
        return okay;
    }
    
    private Set<Product> readProductsFromJsonFile(String filename) 
            throws FileNotFoundException, IOException 
    {
        Set<Product> productSet = new HashSet<>();
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            Product product = m_gson.fromJson(line, Product.class);
            productSet.add(product);
            log.fine("loaded " + product.getProduct_name());
        }
        log.info(productSet.size() + " products loaded");
        return productSet;
    }

    private List<Listing> readListingsFromJsonFile(String filename) 
            throws FileNotFoundException, IOException 
    {
        List<Listing> listings = new ArrayList<>();
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            Listing listing = m_gson.fromJson(line, Listing.class);
            listings.add(listing);
            log.fine("loaded " + listing.getTitle());
        }
        log.info(listings.size() + " listings loaded");
        return listings;
    }
    
    private void writeResultsToJsonFile(List<Result> results, String filename) 
            throws IOException 
    {
        FileWriter fileWriter = new FileWriter(filename);
        for (Result result : results) {
            String jsonResult = m_gson.toJson(result);
            fileWriter.append(jsonResult.concat("\n"));
        }
        fileWriter.close();
    }
    
    public Product matchListing(Listing listing) {
        log.fine("matching:"+listing.getTitle());
        return m_productFinder.getTopMatchingProduct(listing);
    }

    public Map<Product, List<Listing>> getMatchedListingsByProductMap() {
        Map<Product, List<Listing>> matchedListingByProductMap = new HashMap<>();
        System.out.println("Progress:");
        int count = 0;
        NumberFormat format = DecimalFormat.getPercentInstance();
        for (Listing listing : m_listings) {
            Product product = matchListing(listing);
            if (product != null) {
                List<Listing> matchedListings = matchedListingByProductMap.get(product);
                if (matchedListings == null) {
                    matchedListings = new ArrayList<>();
                    matchedListingByProductMap.put(product, matchedListings);
                }
                log.fine("Matched: " + product.getProduct_name() + " to " + listing.getTitle());
                matchedListings.add(listing);
            } else {
                log.fine("No product matches:" + listing.getTitle());
            }
            System.out.print(format.format((double)(++count) / 
                    (double) m_listings.size()) + " \r");
        }
        return matchedListingByProductMap;
    }
    
    public List<Result> getMatchedListings() {
        List<Result> resultList = new ArrayList<>();
        Map<Product, List<Listing>> matchedListingsByProductMap = getMatchedListingsByProductMap();
        for (Product product : matchedListingsByProductMap.keySet()) {
            List<Listing> matchedListings = matchedListingsByProductMap.get(product);
            Result result = new Result();
            result.setProduct_name(product.getProduct_name());
            result.setListings(matchedListings.toArray(new Listing[matchedListings.size()]));
            resultList.add(result);
        }
        log.info(resultList.size() + " products matched to listings");
        return resultList;
    }
}
