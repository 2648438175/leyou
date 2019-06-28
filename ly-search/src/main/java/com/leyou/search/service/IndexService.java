package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.search.clients.CategoryClient;
import com.leyou.search.clients.GoodsClient;
import com.leyou.search.clients.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexService {
    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private GoodsRepository goodsRepository;

    public Goods buildGoods(SpuBo spuBo) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(spuBo,goods);
        List<String> names = categoryClient.queryNamesByIds(Arrays.asList(spuBo.getCid1(), spuBo.getCid2(), spuBo.getCid3()));
        String all=spuBo.getTitle()+" "+StringUtils.join(names,",");
        goods.setAll(all);
        List<Sku> skus= goodsClient.querySkuBySpuId(spuBo.getId());
        List<Map<String,Object>> skuMapList = new ArrayList<>();
        List<Long> prices=new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String,Object> skuMap=new HashMap();
            skuMap.put("id",sku.getId());
            skuMap.put("title",sku.getTitle());
            skuMap.put("price",sku.getPrice());
            skuMap.put("image",StringUtils.isBlank(sku.getImages())?"":sku.getImages().split(",")[0]);
            skuMapList.add(skuMap);
        });
        goods.setSkus(JsonUtils.serialize(skuMapList));
        goods.setPrice(prices);
        Long cid3=spuBo.getCid3();
        List<SpecParam> specParamList = specClient.querySpecParam(null, cid3, true, null);
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spuBo.getId());
        Map<Long,Object> genericMap=JsonUtils.nativeRead(spuDetail.getGenericSpec(), new TypeReference<Map<Long,Object>>() {});
        Map<Long,Object> specialMap=JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long,Object>>() {});
        Map<String,Object> specs = new HashMap<>();
        specParamList.forEach(specParam -> {
            Long id=specParam.getId();
            String name = specParam.getName();
            Object value=null;
            if(specParam.getGeneric()){
                value=genericMap.get(id);
                if(value!=null&&specParam.getNumeric()){
                    value=this.chooseSegment(value.toString(),specParam);
                }
            }else{
                value=specialMap.get(id);
            }
            if(value==null){
                value="其他";
            }
            specs.put(name,value);
        });
        goods.setSpecs(specs);
        return goods;
    }


    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }


    public void createIndex(Long id) {
        Spu spu = goodsClient.querySpuById(id);
        SpuBo spuBo = new SpuBo();
        BeanUtils.copyProperties(spu,spuBo);
        Goods goods = this.buildGoods(spuBo);
        this.goodsRepository.save(goods);

    }

    public void deleteIndex(Long id) {
        goodsRepository.deleteById(id);
    }
}
