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
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

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
