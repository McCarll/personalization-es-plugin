package com.mccarl.es.plugin;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.logging.LogManager;
import org.elasticsearch.logging.Logger;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class UserSignalService {

    private static final Logger logger = LogManager.getLogger(UserSignalService.class);

    private static UserSignalService INSTANCE;

    private final Map<String, Map<String, Float>> boostsByLocation = new ConcurrentHashMap<>();

    public UserSignalService() {
        INSTANCE = this;
    }

    public static UserSignalService getInstance() {
        return INSTANCE;
    }

    public void updateSignals(Client client) {
        logger.info("Starting updateSignals...");

        SearchRequest searchRequest = new SearchRequest("signals");
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.rangeQuery("last_updated").gte("now-3M/M"))
                )
                .sort("last_updated", SortOrder.DESC)
                .size(500);

        logger.info("Search source: {}", sourceBuilder);

        searchRequest.source(sourceBuilder);

        client.search(searchRequest, new ActionListener<>() {
            @Override
            public void onResponse(SearchResponse response) {
                logger.info("Received {} hits in first scroll batch", response.getHits().getHits().length);
                String scrollId = response.getScrollId();
                processHits(response.getHits().getHits());

                if (response.getHits().getHits().length > 0) {
                    logger.info("Scrolling for more hits...");
                    SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                    scrollRequest.scroll(TimeValue.timeValueMinutes(1L));
                    client.searchScroll(scrollRequest, this);
                } else {
                    logger.info("No more hits. Clearing scroll context.");
                    ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                    clearScrollRequest.addScrollId(scrollId);
                    client.clearScroll(clearScrollRequest, ActionListener.noop());
                }
            }

            @Override
            public void onFailure(Exception e) {
                logger.error("Failed to update signals", e);
            }

            private void processHits(SearchHit[] hits) {
                for (SearchHit hit : hits) {
                    Map<String, Object> src = hit.getSourceAsMap();
                    logger.info("Processing hit: {}", src);

                    String location = (String) src.get("location");
                    String color = (String) src.get("color");
                    Object boostObj = src.get("boost");

                    if (location == null || color == null || boostObj == null) {
                        logger.warn("Missing required fields in signal: {}", src);
                        continue;
                    }

                    float boost = ((Number) boostObj).floatValue();
                    boostsByLocation
                            .computeIfAbsent(location, l -> new ConcurrentHashMap<>())
                            .put(color, boost * 1000f);
                }
            }
        });
    }



    public Map<String, Float> getBoostsForLocation(String location) {
        return boostsByLocation.getOrDefault(location, Collections.emptyMap());
    }
}

