package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> pageQuery(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        PageHelper.startPage(page,rows);
        Example example = new Example(Brand.class);
        if(StringUtils.isNotBlank(key)){
            Example.Criteria criteria = example.createCriteria();
            criteria.andLike("name","%"+key+"%");
        }
        if(StringUtils.isNoneBlank(sortBy)){
            example.setOrderByClause(sortBy+(desc ? " DESC":" ASC"));
        }
        Page<Brand> brandPage =( Page<Brand>) brandMapper.selectByExample(example);
        return  new PageResult<>(brandPage.getTotal(),new Long(brandPage.getPages()),brandPage);
    }

    @Transactional
    public void addBrand(Brand brand, List<Long> cids) {
        brandMapper.insertSelective(brand);
        cids.forEach(cid->{
            brandMapper.insertBrandCategory(cid,brand.getId());
        });
    }

    @Transactional
    public void uploadBrand(Brand brand, List<Long> cids) {
        this.brandMapper.updateByPrimaryKeySelective(brand);
        this.brandMapper.deleteBrandCategory(brand.getId());
        cids.forEach(cid->{
            brandMapper.insertBrandCategory(cid,brand.getId());
        });
    }



    public Brand queryBrandById(Long brandId) {
       return brandMapper.selectByPrimaryKey(brandId);
    }



    public List<Brand> queryBrandByCategory(Long cid) {
        return brandMapper.queryBrandByCategory(cid);
    }
}
