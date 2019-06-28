package com.leyou.test;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.search.clients.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.IndexService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexCreateTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private IndexService indexService;

    @Test
    public void createIndexAnaPutMapping(){
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
    }


    @Test
    public void loadData(){
        int page=1;
        while(true){
            PageResult<SpuBo> spuBoPageResult = goodsClient.querySpuByPage(null, null, page, 50);
            if(spuBoPageResult==null){
                break;
            }
            List<SpuBo> items = spuBoPageResult.getItems();
            page++;
            List<Goods> goods = new ArrayList<>();
            items.forEach(spuBo -> {
                Goods good=indexService.buildGoods(spuBo);
                goods.add(good);
            });
            goodsRepository.saveAll(goods);
        }
    }
}
