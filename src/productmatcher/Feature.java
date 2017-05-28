/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package productmatcher;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Chad
 */
public class Feature {
    
    private final List<String> m_ngram;
    
    public Feature(List<String> ngram) {
        m_ngram = ngram;
    }
    
    @Override
    public String toString() {
        return StringUtils.join(m_ngram, " ");
    }
    
    public boolean contains(String term) {
        return m_ngram.contains(term);
    }
    
    public boolean contains(List<String> otherNgram) {
       return Collections.indexOfSubList(m_ngram, otherNgram) != -1;
    }

    public int size() {
        return m_ngram.size();
    }
    
    public double getMatchingScore(Feature otherFeature) {
        double score = 0;
        
        if (otherFeature.contains(m_ngram)) {
            score = Math.pow(m_ngram.size(), 2);
        }

        return score;
    }
    
}
