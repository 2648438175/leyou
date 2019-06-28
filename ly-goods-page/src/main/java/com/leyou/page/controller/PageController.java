package com.leyou.page.controller;

import com.leyou.page.service.FileService;
import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    @Autowired
    private FileService fileService;

    @GetMapping("item/{spuId}.html")
    public String toPage(@PathVariable("spuId") Long spuId, Model model){
        Map<String, Object> map = pageService.loadData(spuId);
        model.addAllAttributes(map);
        if(!this.fileService.exists(spuId)){
            fileService.syncCreateHtml(spuId);
        }
        return "item";
    }
}
