package com.bingo.common.String;

import java.util.*;

/**
 * 字符抽取处理
 * 1.实现简单的字符串抽取，用于HTML中特定字符抽取
 * Created by huangtw on 2018-04-06.
 */
public class StringExtract {
    private String template;

    public StringExtract(String template){
        this.template = template;
    }


    /**
     * 按行抽取，完整的字符按行划分,如果某行同时包含某些字符串，则返回整行.
     * 只返回一行
     * @param includeStr
     * @return
     */
    public String extractLine(String... includeStr){
        return extractLine(template,includeStr);
    }

    public String extractLine(int start,String... includeStr){
        if(start<0){
            start=0;
        }
        return extractLine(template.substring(start),includeStr);
    }

    private String extractLine(String src,String... includeStr){
        if(src==null){
            return null;
        }
        Scanner sc = new Scanner(src);
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            if(line==null){
                continue;
            }
            boolean has = true;
            for(String temp:includeStr){
                if(line.indexOf(temp)==-1){
                    has = false;
                    break;
                }
            }
            if(has){
                return line;
            }
        }
        return null;
    }

    /**
     * 通过开始字符串，结束字符串，抽取开始-结束字符之间的字符串
     * @param start 开始字符串
     * @param end   结束字符串（从开始字符串之后出现的开始计算）,如果传入空，则直接抽取到最后
     * @return
     */
    public String extractString(String start,String end){
        if(template==null){
            return null;
        }
        int s = template.indexOf(start);
        if(s==-1){
            return null;
        }
        s = s+start.length();
        if(end==null){
            return template.substring(s);
        }
        int e = template.indexOf(end,s);
        if(e==-1){
            return null;
        }
        String ret = template.substring(s,e);
        return ret;
    }

    /**
     * 抽取字符串，同时返回抽取后的字符串
     * @param start
     * @param end
     * @return
     */
    private String[] extractStringAndEnd(String src,String start,String end){
        if(src==null){
            return null;
        }
        int s = src.indexOf(start);
        if(s==-1){
            return null;
        }
        s = s+start.length();
        if(end==null){
            return new String[]{src.substring(s)};
        }
        int e = template.indexOf(end,s);
        if(e==-1){
            return null;
        }
        String ret = src.substring(s,e);
        String _end = src.substring(e);
        return new String[]{ret,_end};
    }

    /**
     * 抽取很多块(重复出现的块)
     * @param start
     * @param end
     * @return
     */
    public String[] extractStringList(String start,String end){
        List<String> list = new ArrayList<String>();
        String src[] = extractStringAndEnd(template,start,end);
        while(src!=null && src.length==2){
            list.add(src[0]);
            src = extractStringAndEnd(src[1],start,end);
        }
        return list.toArray(new String[0]);
    }



    /**
     * 判断某个字符串在整个字符串中出现的次数
     *
     * @param idxText
     * @return
     */
    public int indexNumber(String idxText){
        int count = 0;
        int index = 0;
        while ((index = template.indexOf(idxText, index)) != -1) {
            index = index + idxText.length();
            count++;
        }
        return count;
    }



    public static void main(String args[]) throws Exception {
        String t = "xxxx1${xx}222${xx1}12312${xx}12312";

        StringExtract sr = new StringExtract(t);

        System.out.println(sr.extractStringList("xx1","xx")[1]);
    }
}
