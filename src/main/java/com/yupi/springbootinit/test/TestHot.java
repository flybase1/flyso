package com.yupi.springbootinit.test;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class TestHot {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String hot = getHot(scanner.next());
            System.out.println(hot);
        }
    }


    public static String getHot(String context) {
        // 创建Elasticsearch客户端
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        // 定义热搜索引名称
        String index = "hot_search";

        // 获取当前时间戳
        long timestamp = System.currentTimeMillis();

        // 获取用户输入的搜索词

        // 创建文档
        Map<String, Object> doc = new HashMap<>();
        doc.put("query", context);
        doc.put("timestamp", timestamp);
        doc.put("count", 1);

        // 创建索引请求
        IndexRequest request = new IndexRequest(index).source(doc);

        // 发送请求并处理响应
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        // 创建聚合请求，统计搜索次数
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("popular_search")
                .field("query.keyword")
                .order(BucketOrder.aggregation("count", false))
                .size(10)
                .subAggregation(AggregationBuilders.sum("count").field("count"));

        SearchRequest searchRequest = new SearchRequest(index)
                .source(new SearchSourceBuilder().aggregation(aggregationBuilder));

// 发送聚合请求并处理响应
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get("popular_search");
            Terms.Bucket bucket = terms.getBuckets().get(0);
            return bucket.getKeyAsString();
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void test() {
        // 创建Elasticsearch客户端
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        // 定义热搜索引名称
        String index = "hot_search";

        // 获取当前时间戳
        long timestamp = System.currentTimeMillis();

        // 获取用户输入的搜索词
        String query = "hello";

        // 创建文档
        Map<String, Object> doc = new HashMap<>();
        doc.put("query", query);
        doc.put("timestamp", timestamp);
        doc.put("count", 1);

        // 创建索引请求
        IndexRequest request = new IndexRequest(index).source(doc);

        // 发送请求并处理响应
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        // 创建聚合请求，统计搜索次数
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("popular_search")
                .field("query.keyword")
                .order(BucketOrder.aggregation("count", false))
                .size(10)
                .subAggregation(AggregationBuilders.sum("count").field("count"));

        SearchRequest searchRequest = new SearchRequest(index)
                .source(new SearchSourceBuilder().aggregation(aggregationBuilder));

// 发送聚合请求并处理响应
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get("popular_search");
            for (Terms.Bucket bucket : terms.getBuckets()) {
                String term = bucket.getKeyAsString();
                long count = bucket.getDocCount();
                Sum sum = bucket.getAggregations().get("count");
                double score = sum.getValue();
                System.out.println("Term: " + term + ", Count: " + count + ", Score: " + score);
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
