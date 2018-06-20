package com.bingo.common.http;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * @description   <操作xml的工具类>
 * @about   
 * @author   李丽全
 * @since   2016年11月14日 08:28:17
 */
public class XmlUtils {
	private Document document = null;
	
	/**
	 * @description   <获取节点列表>
	 * @param tagName
	 * @return
	 */
	public NodeList getNodeList(String tagName) {
		return document.getElementsByTagName(tagName);
	}
	
	/**
	 * @description   <获取节点长度>
	 * @param nodeList
	 * @return
	 */
	public int getNodeLength(NodeList nodeList) {
		return nodeList.getLength();
	}
	
	/**
	 * @description   <获取节点列表中的某个节点>
	 * @param nodeList
	 * @param index
	 * @return
	 */
	public Element getNode(NodeList nodeList, int index) {
		return (Element) nodeList.item(index);
	}
	
	/**
	 * @description   <获取指定节点下面的某个节点的值>
	 * @param node
	 * @param tagName
	 * @return
	 */
	public String getNodeValue(Element node, String tagName) {
		Node n = node.getElementsByTagName(tagName).item(0);
		if(n == null) {
			//找不到节点
			return null;
		}
		return n.getFirstChild().getNodeValue();
	}
	
	/**
	 * @description   <获取文档中某个节点的值>
	 * @param tagName
	 * @return
	 */
	public String getNodeValue(String tagName) {
		NodeList list=document.getElementsByTagName(tagName);
		if(list!=null&&list.getLength()>0){
			Node node=list.item(0).getFirstChild();
			if(node!=null){
				return node.getNodeValue();
			}
		}
		return null;
	}
	
	//————————————————–我是萌萌哒分界线————————————————
	
	/**
	 * @description   <创建文档（必须先调用）>
	 */
	public void createDocument() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
			document.setXmlVersion("1.0");
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @description   <创建文档（必须先调用）>
	 * @about   把xml字符串转换成文档
	 * @param xmlContent
	 */
	public void createDocument(String xmlContent) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new StringReader(xmlContent)));
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @description   <创建文档（必须先调用）>
	 * @about   把输入流转换成文档
	 * @param in
	 */
	public void createDocument(InputStream in) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(in);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @description   <创建文档（必须先调用）>
	 * @about   把一个文件转换成文档
	 * @param xmlFile
	 */
	public void createDocument(File xmlFile) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @description   <创建根节点>
	 * @param tagName
	 */
	public Element createRoot(String tagName) {
		return createElement(tagName, null);
	}
	
	/**
	 * @description   <设置根节点（警告：只能调用一次）>
	 * @about   如果多次调用，会报错“HIERARCHY_REQUEST_ERR: 尝试在不允许的位置插入节点。”。
	 * HIERARCHY_REQUEST_ERR说明发生了要把节点放在文档树层次中的不合法位置的操作。
	 * @param root
	 */
	public void setRootNode(Element root) {
		document.appendChild(root);
	}
	
	/**
	 * @description   <创建一个节点>
	 * @about   例如：<name></name>
	 * @param tagName
	 * @return
	 */
	public Element createElement(String tagName) {
		return createElement(tagName, null);
	}
	
	/**
	 * @description   <创建一个节点>
	 * @about   例如：<name>test</name>
	 * @param tagName
	 * @param textContent
	 * @return
	 */
	public Element createElement(String tagName, String textContent) {
		Element element = document.createElement(tagName);
		if(textContent != null) {
			element.setTextContent(textContent);
		}
		return element;
	}
	
	/**
	 * @description   <生成格式美化（缩进和换行）的xml文件>
	 * @param xmlFile
	 * @return
	 */
	public boolean writeDocument(String xmlFile) {
		Writer out = null;
		try {
			File file = new File(xmlFile);
			if(!file.exists()) {
				file.createNewFile();
			}
			/**
			 * transformer.setOutputProperty(OutputKeys.INDENT, "yes");是不能格式美化的，已经验证了。
			 */
			OutputFormat format = new OutputFormat(document);
			format.setIndenting(true);
			format.setIndent(2);
			out = new BufferedWriter(new FileWriter(file));
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * @description   <根据xml字符串生成格式美化的xml文件>
	 * @param xmlFile
	 * @param xmlContent
	 * @return
	 */
	public boolean writeFile(String xmlFile, String xmlContent) {
		createDocument(xmlContent);
		return writeDocument(xmlFile);
	}
}