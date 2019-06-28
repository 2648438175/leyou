package com.leyou.page.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@Service
public class FileService {
    @Value("${ly.thymeleaf.destPath}")
    private  String destPath;

    @Autowired
    private PageService pageService;

    @Autowired
    private TemplateEngine templateEngine;

    private File createPath(Long id){
        if(id==null){
            return  null;
        }
        File dest=new File(this.destPath);
        if(!dest.exists()){
            dest.mkdirs();
        }
        return  new File(dest,id+".html");
    }


    public boolean exists(Long id){
        return this.createPath(id).exists();
    }

    public void syncCreateHtml(Long spuId) {
        createHtml(spuId);
    }

    public void createHtml(Long spuId){
        Context context = new Context();
        context.setVariables(pageService.loadData(spuId));
        File file=new File(destPath);
        if(!file.exists()){
            file.mkdirs();
        }
        File filePath=new File(file,spuId+".html");
        PrintWriter printWriter= null;
        try {
            printWriter = new PrintWriter(filePath,"UTF-8");
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void deleteHtml(Long id) {
       File file= new File(destPath,id+".html");
       if(file.exists()){
           file.delete();
       }
    }
}
