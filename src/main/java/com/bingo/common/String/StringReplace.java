package com.bingo.common.String;

import java.util.HashMap;
import java.util.Map;

/**
 * 字符替换处理
 * 1.实现简单的字符替换处理
 * 2.参考freemarker实现
 * 3.比freemarker实现更简单，更高效
 * 4.支持简单的${xx}替换
 * Created by huangtw on 2018-04-06.
 */
public class StringReplace {
    private Map<String,String> model;
    private String template;

    public StringReplace(String template){
        this.template = template;
        this.model = new HashMap<>();
    }

    public StringReplace(String template,Map<String,String> model){
        this.template = template;
        this.model = model;
    }

    public void put(String key,String value){
        model.put(key,value);
    }

    public String toMarkString() throws Exception {
        for(String key:model.keySet()){
            template = template.replace("${"+key+"}",model.get(key));
        }
        return template;
    }

    private void replace(String target,String replaceStr){
        template = template.replace(target,replaceStr);
        if(template.indexOf(target)!=-1){
            this.replace(target,replaceStr);
        }
    }


    public static void main(String args[]) throws Exception {
        String t = "xxxx1${xx}222${xx}12312${xx}12312";

        StringReplace sr = new StringReplace(t);
        sr.put("xx","yy");
        System.out.println(sr.toMarkString());
    }
}
