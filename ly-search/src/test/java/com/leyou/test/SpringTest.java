package com.leyou.test;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.search.clients.GoodsClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringTest {
    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void test1(){
        PageResult<SpuBo> spuBoPageResult = goodsClient.querySpuByPage(null, null, null, null);
        System.out.println(spuBoPageResult.getTotal());
    }
}
