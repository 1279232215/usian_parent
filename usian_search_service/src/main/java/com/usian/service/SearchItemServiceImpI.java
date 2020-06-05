package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.usian.mapper.SearchItemMapper;
import com.usian.pojo.SearchItem;
import jdk.nashorn.internal.objects.annotations.Where;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import com.bjsxt.utils.JsonUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SearchItemServiceImpI implements SearchItemService {

    @Autowired
    private SearchItemMapper searchItemMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${ES_INDEX_NAME}")
    private String ES_INDEX_NAME;

    @Value("${ES_TYPE_NAME}")
    private String ES_TYPE_NAME;

    @Override
    public boolean importAll() {
        try {
            if(!isExistsIndex()){
                createIndex();
            }
            int page = 1;
            while (true){
                PageHelper.startPage(page,1000);
                List<SearchItem> itemList = searchItemMapper.getItemList();
                if(itemList==null || itemList.size()==0){
                    break;
                }
                BulkRequest bulkRequest = new BulkRequest();
                for (SearchItem searchItem : itemList) {
                    bulkRequest.add(new IndexRequest(ES_INDEX_NAME,ES_TYPE_NAME,searchItem.getId()).source(JsonUtils.objectToJson(searchItem),XContentType.JSON));
                }
                restHighLevelClient.bulk(bulkRequest,RequestOptions.DEFAULT);
                page++;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 分页查询名字、类别、描述、卖点包含q的商品
     * @param q
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<SearchItem> selectByQ(String q, Integer page, Integer pageSize) {
        try{
            //前台传过来条件，根据条件对标题，分类名,描述，和卖点
            //创建查询请求对象，将索引名传过去
            SearchRequest searchRequest = new SearchRequest(ES_INDEX_NAME);
            //传类型名字
            searchRequest.types(ES_TYPE_NAME);
            // 搜索源构建对象
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //用multiMatch，用什么查，查那些字段
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(q,new String[]{"item_title","item_desc","item_sell_point","item_category_name"}));
            /*
            *   设置分页
            *   第一次 0 20
            *   第二次 20 20
            *   第三次 40 20
            *   第四次 60 20
            *   得出
            *   (page-1)*20
            * */
            searchSourceBuilder.from((page-1)*20);
            searchSourceBuilder.size(pageSize);
            //设置高亮构建对象
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.postTags("<font color=red>");
            highlightBuilder.preTags("</font>");
            highlightBuilder.field("item_title");
            //给搜索源对象赋值
            searchSourceBuilder.highlighter(highlightBuilder);
            //设置搜索源
            searchRequest.source(searchSourceBuilder);
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<SearchItem> searchItemList = new ArrayList<>();
            SearchHit[] hits = response.getHits().getHits();
            for (int i = 0; i < hits.length; i++) {
                SearchHit hit = hits[i];
                SearchItem searchItem = JsonUtils.jsonToPojo(hit.getSourceAsString(), SearchItem.class);
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField item_title = highlightFields.get("item_title");
                if(item_title!=null){
                    searchItem.setItem_title(item_title.getFragments()[0].toString());
                }
                searchItemList.add(searchItem);
            }
            return searchItemList;
            //进行分页从第0条开始每页展示20条
            //设置查询出来的高亮
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    //当商品添加时会通过mq发送过来商品的id做索引同步
    @Override
    public boolean insertDocument(String msg) {
        try {
            //根据新添商品id查询3表联合数据
            SearchItem searchItem = searchItemMapper.selectSearchItemByItemId(msg);
            //同步索引
            IndexRequest indexRequest = new IndexRequest(ES_INDEX_NAME,ES_TYPE_NAME,searchItem.getId());
            indexRequest.source(JsonUtils.objectToJson(searchItem),XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            int failed = response.getShardInfo().getFailed();//错误的条数
            if(failed==0){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 索引库是否存在
     * @return
     * @throws IOException
     */
    public boolean isExistsIndex()  {
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest();
            getIndexRequest.indices(ES_INDEX_NAME);
            boolean s = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //如果上面判断不存在，则创建索引
    public boolean createIndex() {
        try {
            //创建 创建索引对象，并设置索引名称
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(ES_INDEX_NAME);
            //设置索引参数 主分片为2个 备用1个
            createIndexRequest.settings(Settings.builder().put("number_of_shards",2).put("number_of_replicas",1));
            //设置映射参数，类型名和执行语句,以Json格式
            createIndexRequest.mapping(ES_TYPE_NAME,"{\n" +
                    "  \"_source\": {\n" +
                    "    \"excludes\":[\"item_desc\"]\n" +
                    "  }, \n" +
                    "  \"properties\": {\n" +
                    "    \"item_title\":{\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"analyzer\": \"ik_max_word\",\n" +
                    "      \"search_analyzer\": \"ik_smart\"\n" +
                    "    },\n" +
                    "    \"item_sell_point\":{\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"analyzer\": \"ik_max_word\",\n" +
                    "      \"search_analyzer\": \"ik_smart\"\n" +
                    "    },\n" +
                    "    \"item_price\":{\n" +
                    "      \"type\": \"float\"\n" +
                    "    },\n" +
                    "    \"item_image\":{\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"index\": false\n" +
                    "    },\n" +
                    "    \"item_category_name\":{\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"analyzer\": \"ik_max_word\",\n" +
                    "      \"search_analyzer\": \"ik_smart\"\n" +
                    "    },\n" +
                    "    \"item_desc\":{\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"analyzer\": \"ik_max_word\",\n" +
                    "      \"search_analyzer\": \"ik_smart\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}",XContentType.JSON);
            //先获得索引对象然后发送create请求
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            //返回响应结果
            return createIndexResponse.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
