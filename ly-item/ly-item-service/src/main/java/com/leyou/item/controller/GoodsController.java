package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping
    @RequestMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "10") Integer rows){
        PageResult<SpuBo> spuBoPageResult=goodsService.querySpuByPage(key,saleable,page,rows);
        if(spuBoPageResult!=null&&spuBoPageResult.getItems().size()>0){
            return  ResponseEntity.ok(spuBoPageResult);
        }
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId")Long spuId){
        SpuDetail spuDetail=goodsService.querySpuDetailBySpuId(spuId);
        if(spuDetail!=null){
            return ResponseEntity.ok(spuDetail);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long spuId){
        List<Sku> skuList=goodsService.querySkuBySpuId(spuId);
        if(skuList!=null&&skuList.size()>0){
            return  ResponseEntity.ok(skuList);
        }
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



    @PutMapping("goods")
    public  ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){

        goodsService.updateGoods(spuBo);
        return  ResponseEntity.status(HttpStatus.CREATED).build();
    }




    @GetMapping("spu/{id}")
    public ResponseEntity<Spu>querySpuById(@PathVariable("id") Long id){
       Spu spu=goodsService.querySpuById(id);
        if(spu!=null){
            return  ResponseEntity.ok(spu);
        }
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
