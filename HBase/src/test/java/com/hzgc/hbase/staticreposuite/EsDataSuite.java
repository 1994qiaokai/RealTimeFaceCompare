package com.hzgc.hbase.staticreposuite;

import com.hzgc.dubbo.staticrepo.ObjectInfoHandler;
import com.hzgc.dubbo.staticrepo.ObjectInfoTable;
import com.hzgc.dubbo.staticrepo.ObjectSearchResult;
import com.hzgc.hbase.staticrepo.ElasticSearchHelper;
import com.hzgc.hbase.staticrepo.ObjectInfoHandlerImpl;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class EsDataSuite {
    @Test
    public void testPutFloatArrayToEs() throws IOException {
        Client client = ElasticSearchHelper.getEsClient();
        IndexResponse response = client.prepareIndex("twitter", "tweet", "1000")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("user" ,"enhaye")
                        .field("postDate", new float[]{1.23411321341f, 1.09f, 2.3454f, 1.0939309f})
                        .endObject()).get();

        String _index = response.getIndex();
        String _type = response.getType();
        String _id = response.getId();
        long _version = response.getVersion();
        RestStatus status = response.status();
        System.out.println("_index: " + _index  + ", _type: " + _type + ", _id： "
                + _id + "_version: " + _version  + "status: " + status );
    }

    @Test
    public void testScrollsSearch(){
        Client client = ElasticSearchHelper.getEsClient();
        SearchRequestBuilder builder = client.prepareSearch(ObjectInfoTable.TABLE_NAME)
                .setTypes(ObjectInfoTable.PERSON_COLF)
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(300000))
                .setExplain(true).setSize(2000);
        QueryBuilder qb = QueryBuilders.matchQuery("name", "花");
        builder.setQuery(qb);
        SearchResponse response = builder.get();
        do {
            System.out.println("Search  total " + response.getHits().getTotalHits());
            response = client.prepareSearchScroll(response.getScrollId())
                    .setScroll(new TimeValue(300000))
                    .execute()
                    .actionGet();
        }while (response.getHits().getHits().length != 0);
    }

    @Test
    public void testGetResultOfDynamicRepo(){
        QueryBuilder qb = QueryBuilders.matchAllQuery();
        Client client = ElasticSearchHelper.getEsClient();
        SearchRequestBuilder builder = client.prepareSearch("dynamic")
                .setTypes("person")
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setQuery(qb)
                .setScroll(new TimeValue(6000))
                .setExplain(true).setSize(2000);
        SearchResponse response = builder.get();
        long total_time = 0;
        do {
            long start_time = System.currentTimeMillis();
            System.out.println("Search  total " + response.getHits().getHits().length);
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit:hits){
                String time = (String) hit.getSource().get("t");
                String sj = (String) hit.getSource().get("sj");
                if (time == null){
                    System.out.println(time + ": " + hit.getId());
                }
            }
            long end_time = System.currentTimeMillis();
            total_time += (end_time - start_time);
            response = client.prepareSearchScroll(response.getScrollId())
                    .setScroll(new TimeValue(6000))
                    .execute()
                    .actionGet();
        }while (response.getHits().getHits().length != 0);
        System.out.println(total_time);

    }

    @Test
    public void testGetObjcetInfo(){
        ObjectInfoHandlerImpl handler = new ObjectInfoHandlerImpl();
        ObjectSearchResult objectSearchResult = handler.getAllObjectInfo();
        List<java.util.Map<String, Object>> persons = objectSearchResult.getResults();
        for (Map<String, Object> person:objectSearchResult.getResults()){
            System.out.println("updatetime: " + person.get("updatetime"));
        }
    }
}
