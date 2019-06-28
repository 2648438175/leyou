package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> pageQuery(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "10") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",required = false) Boolean desc,
            @RequestParam(value = "key",required = false) String key){
        PageResult<Brand> brandPageResult= brandService.pageQuery(page,rows,sortBy,desc,key);
        if(brandPageResult!=null&&brandPageResult.getItems().size()>0){
            return  ResponseEntity.ok(brandPageResult);
        }
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PostMapping()
    public ResponseEntity<Void> addBrand(Brand brand,@RequestParam("cids") List<Long> cid){
        this.brandService.addBrand(brand,cid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



    @PutMapping
    public ResponseEntity<Void> uploadBrand(Brand brand,@RequestParam("cids") List<Long> cid){
        brandService.uploadBrand(brand,cid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("cid/{id}")
    public ResponseEntity<List<Brand>> queryBrandByCategory(@PathVariable("id") Long cid){
        List<Brand> brandList=brandService.queryBrandByCategory(cid);
        if(brandList!=null&&brandList.size()>0){
            return  ResponseEntity.ok(brandList);
        }
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping("bid/{bid}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("bid") Long bid){
        Brand brand=brandService.queryBrandById(bid);
        if(brand!=null){
            return  ResponseEntity.ok(brand);
        }
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
