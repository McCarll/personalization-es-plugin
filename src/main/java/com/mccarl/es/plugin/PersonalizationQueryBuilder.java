package com.mccarl.es.plugin;

import org.apache.lucene.search.Query;
import org.elasticsearch.TransportVersion;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SearchExecutionContext;
import org.elasticsearch.logging.LogManager;
import org.elasticsearch.logging.Logger;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentParser;

import java.io.IOException;
import java.util.Map;

public class PersonalizationQueryBuilder extends AbstractQueryBuilder<PersonalizationQueryBuilder> {

    private static final Logger logger = LogManager.getLogger(PersonalizationQueryBuilder.class);
    public static final String NAME = "personalized_query";

    private final String queryText;
    private final String location;

    public PersonalizationQueryBuilder(String queryText, String location) {
        this.queryText = queryText;
        this.location = location;
    }

    public PersonalizationQueryBuilder(StreamInput in) throws IOException {
        super(in);
        this.queryText = in.readString();
        this.location = in.readString();
    }


    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {
        out.writeString(queryText);
        out.writeString(location);
    }

    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(NAME);
        builder.field("query", queryText);
        builder.field("location", location);
        builder.endObject();
    }

//    @Override
//    protected Query doToQuery(SearchExecutionContext searchExecutionContext) throws IOException {
//        logger.info("=== Converting to Lucene query - query: '{}', location: '{}' ===", queryText, location);
//
//        var bool = QueryBuilders.boolQuery()
//                .must(QueryBuilders.matchQuery("name", queryText));
//
//        Map<String, Float> colorBoosts = switch (location) {
//            case "BR" -> Map.of("red", 2.0f, "black", 1.5f);
//            case "EU" -> Map.of("blue", 2.0f, "gray", 1.2f);
//            case "US" -> Map.of("black", 2.0f, "brown", 1.4f);
//            default -> Map.of();
//        };
//
//        logger.info("Applying color boosts for location '{}': {}", location, colorBoosts);
//
//        for (var entry : colorBoosts.entrySet()) {
//            bool.should(QueryBuilders.termQuery("color", entry.getKey()).boost(entry.getValue()));
//        }
//
//        Query luceneQuery = bool.toQuery(searchExecutionContext);
//        logger.info("Generated Lucene query: {}", luceneQuery);
//
//        return luceneQuery;
//    }

    @Override
    protected Query doToQuery(SearchExecutionContext context) throws IOException {
        var bool = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("name", queryText));

        Map<String, Float> colorBoosts = PersonalizationPlugin.getSignalService().getBoostsForLocation(location);
        logger.info("Applying color boosts for location '{}': {}", location, colorBoosts);

        for (var entry : colorBoosts.entrySet()) {
            bool.should(QueryBuilders.termQuery("color", entry.getKey()).boost(entry.getValue()));
        }

        return bool.toQuery(context);
    }


    @Override
    public String getWriteableName() {
        return NAME;
    }

    @Override
    public TransportVersion getMinimalSupportedVersion() {
        return null;
    }

    public static PersonalizationQueryBuilder fromXContent(XContentParser parser) throws IOException {
        logger.info("=== Starting to parse PersonalizationQueryBuilder ===");

        String query = null;
        String location = null;

        XContentParser.Token token;
        String currentFieldName = null;

        // Log the initial state
        logger.info("Initial parser token: {}", parser.currentToken());
        logger.info("Initial parser name: {}", parser.currentName());

        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            logger.debug("Current token: {}, Current name: {}", token, parser.currentName());

            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
                logger.info("Found field name: {}", currentFieldName);
            } else if (token.isValue()) {
                String value = parser.text();
                logger.info("Found value for field '{}': '{}'", currentFieldName, value);

                if ("query".equals(currentFieldName)) {
                    query = value;
                } else if ("location".equals(currentFieldName)) {
                    location = value;
                } else {
                    logger.warn("Unknown field in personalized_query: {}", currentFieldName);
                }
            }
        }

        logger.info("=== Parsed values - query: '{}', location: '{}' ===", query, location);

        if (query == null || location == null) {
            logger.error("Missing required fields - query: {}, location: {}", query, location);
            throw new IllegalArgumentException("Both 'query' and 'location' fields are required");
        }

        return new PersonalizationQueryBuilder(query, location);
    }

    @Override
    protected boolean doEquals(PersonalizationQueryBuilder other) {
        return this.queryText.equals(other.queryText) && this.location.equals(other.location);
    }

    @Override
    protected int doHashCode() {
        return 31 * queryText.hashCode() + location.hashCode();
    }
}

