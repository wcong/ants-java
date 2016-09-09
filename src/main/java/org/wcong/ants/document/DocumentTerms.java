package org.wcong.ants.document;

import lombok.Data;

import java.util.List;

/**
 * terms sum for document
 * Created by wcong on 2016/8/12.
 */
@Data
public class DocumentTerms {

    private List<DocumentTerm> terms;

    @Data
    public static class DocumentTerm {
        private String term;

        private long count;
    }

}
