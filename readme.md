# Personalization Plugin for Elasticsearch

## Overview

This Elasticsearch plugin introduces a custom query type `personalized_query` that dynamically personalizes product search results based on user signals such as purchases and search conversions. It uses location-based aggregated data from a dedicated `signals` index to apply query-time boosting (e.g., preferring red chairs for BR users).

---

## How It Works

- **Custom Query Type:** Adds a new query type called `personalized_query` with parameters `query` and `location`.
- **Signal Boosting:** At query execution, it boosts relevant fields (e.g., `color`) based on preferences derived from user activity, stored in a `signals` index.
- **Scheduled Updates:** A background task pulls updated signal data using the Elasticsearch scroll API and caches it in memory.
- **Lucene Query Conversion:** Signals are translated into boosted clauses inside a `bool` query using Lucene.

---

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