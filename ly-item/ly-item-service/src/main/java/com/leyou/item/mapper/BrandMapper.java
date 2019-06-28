package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {
    @Insert("insert into tb_category_brand value(#{cid},#{id})")
    void insertBrandCategory(Long cid, Long id);

    @Delete("delete from tb_category_brand where brand_id=#{id}")
    void deleteBrandCategory(Long id);

    @Select("select * from tb_brand tb inner join tb_category_brand cb on tb.id=cb.brand_id where cb.category_id=#{cid}")
    List<Brand> queryBrandByCategory(Long cid);
}
