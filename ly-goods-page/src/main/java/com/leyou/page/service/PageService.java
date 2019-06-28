package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.clients.GoodsClient;
import com.leyou.page.clients.SpecClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;


    public Map<String,Object> loadData(Long spuId) {
        Map<String,Object> results=new HashMap<>();
        Spu spu = goodsClient.querySpuById(spuId);
        results.put("spu",spu);
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spuId);
        results.put("spuDetail",spuDetail);
        List<Sku> skus = goodsClient.querySkuBySpuId(spuId);
        results.put("skus",skus);
        List<SpecParam> specailSpecParams=specClient.querySpecParam(null,spu.getCid3(),null,false);
        Map<Long,Object> specParams=new HashMap<>();
        specailSpecParams.forEach(specParam -> {
            specParams.put(specParam.getId(),specParam.getName());
        });
        results.put("specParams",specParams);
        List<SpecGroup> specGroups = this.specClient.querySpecGroup(spu.getCid3());
        results.put("specGroups",specGroups);
        return results;
    }
}
