package org.wcong.ants.spiders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.crawler.Parser;
import org.wcong.ants.crawler.Result;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.Response;
import org.wcong.ants.spider.Spider;
import org.wcong.ants.util.JsonUtils;

import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 2018/7/24
 */
public class TranslateSpider extends Spider {

    private static Logger logger = LoggerFactory.getLogger(TranslateSpider.class);

    public TranslateSpider() {
        super("translate");
    }

    private static AtomicInteger atomicInteger = new AtomicInteger();

    private static Map<String, Map<String, List<String>>> wordMap = new HashMap<>();

    {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try (FileOutputStream fileOutputStream = new FileOutputStream("./english.1.json")) {
                fileOutputStream.write(JsonUtils.toJsonString(wordMap).getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(atomicInteger.get());
        }));
    }

    @Override
    public void init() {
        parserMap.put(DEFAULT_PARSE_NAME, new DefaultParser());
        parserMap.put("translate", new Translate());
    }

    @Override
    public List<Request> getFirstRequests() {
        return Collections.singletonList(
                Request.basicRequest("https://bruce-1257168554.cos.ap-shanghai.myqcloud.com/english.json"
                        , name, DEFAULT_PARSE_NAME)
        );
    }

    public class DefaultParser implements Parser {

        @Override
        public Result parse(Response response) {
            Map<String, Object> parseResult = JsonUtils.parseJsonMap(response.getBody());
            Result result = new Result();
            List<Request> requestList = new ArrayList<>();
            result.setRequestList(requestList);
            for (Map.Entry<String, Object> entry : parseResult.entrySet()) {
                Map<String, Object> innerMap = (Map) entry.getValue();
                for (Map.Entry<String, Object> innerEntry : innerMap.entrySet()) {
                    List<String> wordList = (List) innerEntry.getValue();
                    for (String word : wordList) {
                        Request request = Request.basicRequest("http://fanyi.baidu.com/sug", name, "translate");
                        requestList.add(request);
                        request.setMethod(Request.METHOD_POST);
                        Map<String, String> form = new HashMap<>();
                        form.put("kw", word);
                        request.setForm(form);
                        Map<String, Object> context = new HashMap<>();
                        context.put("parent", entry.getKey());
                        context.put("current", innerEntry.getKey());
                        request.setContext(context);
                    }
                }
            }
            return result;
        }
    }

    public class Translate implements Parser {

        @Override
        public Result parse(Response response) {
            logger.info("get body:" + response.getBody());
            Map<String, Object> parseResult = JsonUtils.parseJsonMap(response.getBody());
            List<Object> translateList = (List) parseResult.get("data");
            Map<String, String> firstTranslate = (Map) translateList.get(0);
            String word = firstTranslate.get("k");
            String translate = firstTranslate.get("v");
            String parent = response.getRequest().getContext().get("parent").toString();
            String current = response.getRequest().getContext().get("current").toString();
            Map<String, List<String>> currentMap = wordMap.get(parent);
            if (currentMap == null) {
                currentMap = new HashMap<>();
                wordMap.put(parent, currentMap);
            }
            List<String> wordList = currentMap.get(current);
            if (wordList == null) {
                wordList = new ArrayList<>();
                currentMap.put(current, wordList);
            }
            atomicInteger.getAndAdd(1);
            wordList.add(word + ":" + translate);
            return new Result();
        }
    }
}
