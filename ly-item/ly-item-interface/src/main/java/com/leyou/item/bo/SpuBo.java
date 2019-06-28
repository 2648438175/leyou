package com.leyou.item.bo;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;
@Data
public class SpuBo extends Spu {
    @Transient
    private String cname;
    @Transient
    private String bnane;
    @Transient
    private List<Sku> skus;
    @Transient
    private SpuDetail spuDetail;
}
