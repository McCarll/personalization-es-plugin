# Personalization Plugin for Elasticsearch


This Elasticsearch plugin introduces a custom query type `personalized_query` that dynamically personalizes product search results based on user signals such as past purchases and search conversions. It leverages location-based behavioral data from a dedicated `signals` index to apply query-time boosts (e.g., preferring red chairs for BR users or blue for EU).

---

## 🔍 How It Works

- **Custom Query Type:**  
  Adds a new query type called `personalized_query` that takes parameters like `query` (user input) and `location` (e.g., "US", "EU", "BR").

- **Signal Boosting:**  
  Each search execution consults an in-memory cache built from the `signals` index. Boost values are based on user interactions (e.g., purchases and conversions in the past 3 months), boosting fields like `color` or `brand`.

- **Scheduled Signal Updates:**  
  A background task runs periodically (via Elasticsearch’s thread pool) to fetch the latest signals using the scroll API. It filters only documents updated in the last 3 months and rebuilds the boost map for each location.

- **Lucene Query Conversion:**  
  The plugin translates signal data into boosted Lucene `bool` queries, combining the user's search text with weighted field clauses.

---

## Why This Approach is Best

This plugin-based solution integrates natively into Elasticsearch, ensuring minimal external dependencies and real-time scoring enhancements. It avoids fragile client-side personalization and centralizes logic for consistency and control. Boosts are applied dynamically at query time, allowing immediate response to changing behavior without index reprocessing.

---

## Advantages

- **Real-time Personalization:** Boosts reflect the latest user behavior using periodic cache updates.
- **Seamless Integration:** Uses native Elasticsearch plugin APIs and works out-of-the-box with any query pipeline.
- **Low Latency:** No joins or reindexing; uses in-memory lookup for boosting.
- **Extensible Design:** Can easily be expanded to include other dimensions (e.g., brand, price sensitivity).

---

## Limitations

- **Coarse Granularity:** Signals are currently aggregated at the location level, not per-user.
- **Cold Start:** New locations without signals receive no boosts.
- **Boost Logic is Static:** Signal weights are not automatically tuned (e.g., via ML).

---

## Next Steps

- **Per-User Personalization:** Extend the plugin to consider per-user signals instead of only location-level aggregates.
- **Time-Decay Weighting:** Add recency-based decay to signals (e.g., newer purchases have higher weight).
- **Boost Tuning:** Use offline analysis or A/B testing to fine-tune boost multipliers.
- **Support for Multi-Dimensional Boosts:** Extend logic to boost based on category, brand, price range, and user segment.

---

> This plugin provides a scalable and extensible foundation for real-time behavioral personalization in search, bridging the gap between static relevance and dynamic user preferences.


## Example Query
### possible locations: BR, EU, ES
```json
POST /products/_search
{
  "query": {
    "personalized_query": {
      "query": "chair",
      "location": "BR"
    }
  }
}
```

## output example
### location BR
```json

"hits": [
            {
                "_index": "products",
                "_id": "chair_004",
                "_score": 1858.1317,
                "_source": {
                    "product_id": "chair_004",
                    "name": "Gaming Racing Chair",
                    "description": "High-back racing style gaming chair with RGB lighting and massage function",
                    "category": "furniture",
                    "subcategory": "gaming_chairs",
                    "brand": "GamePro",
                    "color": "red",
                    "material": "pu_leather",
                    "price": 449.99,
                    "original_price": 599.99,
                    "discount_percentage": 25.0,
                    "in_stock": true,
                    "stock_quantity": 65,
                    "attributes": {
                        "weight": 28.0,
                        "dimensions": "27x27x52 inches",
                        "assembly_required": true,
                        "warranty_years": 2
                    },
                    "tags": [
                        "gaming",
                        "racing",
                        "rgb",
                        "massage"
                    ],
                    "rating": 4.7,
                    "review_count": 892,
                    "created_date": "2023-02-28",
                    "updated_date": "2024-12-05",
                    "popularity_score": 0.95
                }
            },
```
### location US
```json

"hits": [
{
"_index": "products",
"_id": "chair_008",
"_score": 1199.8677,
"_source": {
"product_id": "chair_008",
"name": "Outdoor Rattan Lounge Chair",
"description": "Weather-resistant rattan lounge chair with cushions",
"category": "furniture",
"subcategory": "outdoor_chairs",
"brand": "PatioLife",
"color": "brown",
"material": "rattan",
"price": 399.99,
"original_price": 549.99,
"discount_percentage": 27.3,
"in_stock": true,
"stock_quantity": 55,
"attributes": {
"weight": 35.0,
"dimensions": "32x35x38 inches",
"assembly_required": false,
"warranty_years": 2
},
"tags": [
"outdoor",
"rattan",
"lounge",
"weather-resistant"
],
"rating": 4.5,
"review_count": 156,
"created_date": "2023-08-05",
"updated_date": "2024-11-22",
"popularity_score": 0.70
}
}]

```

### location EU
```json
"hits": [
{
"_index": "products",
"_id": "chair_007",
"_score": 1622.4615,
"_source": {
"product_id": "chair_007",
"name": "Kids Study Chair",
"description": "Colorful adjustable chair designed for children's study rooms",
"category": "furniture",
"subcategory": "kids_chairs",
"brand": "KidsFirst",
"color": "blue",
"material": "plastic",
"price": 89.99,
"original_price": 119.99,
"discount_percentage": 25.0,
"in_stock": true,
"stock_quantity": 180,
"attributes": {
"weight": 8.5,
"dimensions": "16x16x28-34 inches",
"assembly_required": false,
"warranty_years": 1
},
"tags": [
"kids",
"study",
"adjustable",
"colorful"
],
"rating": 4.6,
"review_count": 223,
"created_date": "2023-07-10",
"updated_date": "2024-12-02",
"popularity_score": 0.72
}
},

```