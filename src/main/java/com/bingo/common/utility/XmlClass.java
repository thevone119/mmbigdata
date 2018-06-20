package com.bingo.common.utility;

import org.dom4j.*;

import java.lang.reflect.Field;


/**
 * Created by Administrator on 2017/5/26.
 */
public class XmlClass {
    public static Document toXML(String str) {
        try {
            return DocumentHelper.parseText(str);
        } catch (DocumentException e) {
            return null;
        }
    }

    public static Document toXML(String str,boolean clearNamespace) {
        Document doc=toXML(str);
        if(clearNamespace){
            doc.selectNodes("//*").forEach((d) -> {

                if (d instanceof Element) {
                    Element elm = (Element) d;
                    QName qName = new QName(elm.getName(), null);
                    elm.setQName(qName);
                }
            });
        }
        return doc;
    }

    public static String toStr(Document document) {
        return document.asXML();
    }

    /**
     * 把XML的值注入到对象的属性中
     * @param o
     * @param xml
     * @throws Exception
     */
    public static void  SetObjectField(Object o,String xml) throws Exception {
        Document doc = DocumentHelper.parseText(xml);
        Element ele = doc.getRootElement();
        Field[] fds = o.getClass().getDeclaredFields();//取所有的属性
        for(Field fd:fds){
            fd.setAccessible(true);
            String fname = fd.getName();

            Node node = ele.selectSingleNode("//"+fname);
            if(node!=null){
                try{
                    if(fd.getType().toString().indexOf("String")!=-1){
                        fd.set(o,node.getText());
                        continue;
                    }
                    if(fd.getType().toString().indexOf("Long")!=-1){
                        fd.setLong(o,new Long(node.getText()));
                        continue;
                    }
                    if(fd.getType().toString().indexOf("Integer")!=-1){
                        fd.setInt(o,new Integer(node.getText()));
                        continue;
                    }
                    if(fd.getType().toString().indexOf("int")!=-1){
                        fd.setInt(o,new Integer(node.getText()));
                        continue;
                    }
                    if(fd.getType().toString().indexOf("Double")!=-1){
                        fd.setDouble(o,new Double(node.getText()));
                        continue;
                    }
                }catch(Exception e){
                }
            }
        }
    }

}
