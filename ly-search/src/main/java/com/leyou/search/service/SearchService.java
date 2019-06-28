package com.leyou.search.service;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecParam;
import com.leyou.search.clients.BrandClient;
import com.leyou.search.clients.CategoryClient;
import com.leyou.search.clients.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.utils.SearchRequest;
import com.leyou.search.utils.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecClient specClient;

    public SearchResult page(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if(!StringUtils.isNotBlank(key)){
            return  null;
        }
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        QueryBuilder query=buildBasicQueryWithFilter(searchRequest);
        queryBuilder.withQuery(query);
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1,searchRequest.getSize()));
        String categoryAggName="category";
        String brandAggName="brand";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        List<Category> categories=new ArrayList<>();
        List<Brand> brands=new ArrayList<>();
        AggregatedPage<Goods> goodsPage=(AggregatedPage<Goods>)goodsRepository.search(queryBuilder.build());
        LongTerms categoryTerms=(LongTerms)goodsPage.getAggregation(categoryAggName);
        List<LongTerms.Bucket> buckets = categoryTerms.getBuckets();
        List<Long> cids=new ArrayList<>();
        buckets.forEach(bucket ->cids.add(bucket.getKeyAsNumber().longValue()));
        List<String> names=categoryClient.queryNamesByIds(cids);
        for(int i=0;i<cids.size();i++){
            Category category=new Category();
            category.setId(cids.get(i));
            category.setName(names.get(i));
            categories.add(category);
        }
        List<Long> brandIds = new ArrayList<>();
        LongTerms brandTerms=(LongTerms) goodsPage.getAggregation(brandAggName);
        brandTerms.getBuckets().forEach(bucket ->brandIds.add(bucket.getKeyAsNumber().longValue()));
        brandIds.forEach(brandId->{
            Brand brand = brandClient.queryBrandById(brandId);
            brands.add(brand);
        });
        List<Map<String,Object>> specs=null;
        if(categories.size()==1){
            specs=getSpecs(categories.get(0).getId(),query);
        }
        return  new SearchResult(goodsPage.getTotalElements(),new Long(goodsPage.getTotalPages()),goodsPage.getContent(),categories,brands,specs);
    }

    private List<Map<String, Object>> getSpecs(Long id, QueryBuilder query) {
        List<Map<String, Object>> specList=new ArrayList<>();
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(query);
        List<SpecParam> searchingSpecParams = specClient.querySpecParam(null, id, true, null);
        searchingSpecParams.forEach(specParam -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));
        });
        AggregatedPage<Goods> page=(AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        searchingSpecParams.forEach(specParam -> {
            String name=specParam.getName();
            StringTerms stringTerms=(StringTerms)page.getAggregation(name);
            List<String> values=new ArrayList<>();
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
            buckets.forEach(bucket->values.add(bucket.getKeyAsString()));
            Map<String,Object> specMap=new HashMap<>();
            specMap.put("k",name);
            specMap.put("options",values);
            specList.add(specMap);
        });
        return specList;
    }



    private QueryBuilder buildBasicQueryWithFilter(SearchRequest searchRequest) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));
        BoolQueryBuilder filterQueryBuilder=QueryBuilders.boolQuery();
        Map<String, String> filter = searchRequest.getFilter();
        for(Map.Entry<String,String> entry:filter.entrySet()){
            String key=entry.getKey();
            String value=entry.getValue();
            if(key!="cd3"&&key!="brandId"){
                key="specs."+key+".keyword";
                filterQueryBuilder.must(QueryBuilders.termQuery(key,value));
            }
        }
        queryBuilder.filter(filterQueryBuilder);
        return queryBuilder;
    }
}
