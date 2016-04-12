package org.wcong.ants.aware;

import org.wcong.ants.index.DocumentReader;

/**
 * set document reader
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/11
 */
public interface DocumentReaderAware {

	void setDocumentReader(DocumentReader documentReader);

}
