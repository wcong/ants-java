package org.wcong.ants.util;

import org.lionsoul.jcseg.extractor.impl.TextRankKeywordsExtractor;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.tokenizer.core.SegmentFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by hzwangcong on 2016/9/8.
 */
public class TextRankKeywordsUtil {

    private static JcsegTaskConfig config = new JcsegTaskConfig(true);
    private static ADictionary dic;

    static {
        config.setClearStopwords(true);     //设置过滤停止词
        config.setAppendCJKSyn(false);      //设置关闭同义词追加
        config.setKeepUnregWords(false);    //设置去除不识别的词条
        dic = DictionaryFactory.createSingletonDictionary(config);
    }

    public static List<String> keywords(String text) {
        try {
            ISegment seg = SegmentFactory.createJcseg(JcsegTaskConfig.COMPLEX_MODE, new Object[]{config, dic});

            TextRankKeywordsExtractor extractor = new TextRankKeywordsExtractor(seg);
            extractor.setMaxIterateNum(100);        //设置pagerank算法最大迭代次数，非必须，使用默认即可
            extractor.setWindowSize(5);             //设置textRank计算窗口大小，非必须，使用默认即可
            extractor.setKeywordsNum(10);
            return extractor.getKeywordsFromString(text);
        } catch (JcsegException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }


}
