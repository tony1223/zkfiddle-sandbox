package org.zkoss.fiddler.executor.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.filters.StringInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class XMLParserUtil {

	public static String findFirstTagContent(Element ele, String tag) {
		return getFirstElement(ele,tag).getTextContent();
	}

	public static String findFirstTagContent(String response, String tag) {
		return getFirstElement(response,tag).getTextContent();
	}

	public static Element getFirstElement(String response,String tag) {
		return getFirstElement(buildDocument(response),tag);
	}
	
	public static Element getFirstElement(Document doc,String tag) {
		NodeList nl = doc.getElementsByTagName(tag);
		if(nl.getLength() >0 ){
			return (Element) nl.item(0);
		}
		return null;
	}

	public static Element getLastElement(Document dom,String tag){
		NodeList nl = dom.getElementsByTagName(tag);

		int size = nl.getLength();
		if (size == 0) {
			return null;
		}

		return (Element) nl.item(size - 1);
	}

	public static Iterator<Element> getElementsByTag(String xml,String tag){
		return getElementsByTag(buildDocument(xml),tag);
	}

	public static Iterator<Element> getElementsByTag(Element ele,String tag){
		return new NodeListIterator(ele.getElementsByTagName(tag));
	}

	public static Iterator<Element> getElementsByTag(Document dom,String tag){
		return new NodeListIterator(dom.getElementsByTagName(tag));
	}

	public static Element getFirstElement(Element parent,String tag) {
		NodeList nl = parent.getElementsByTagName(tag);
		if(nl.getLength() >0 ){
			return (Element) nl.item(0);
		}
		return null;
	}

	/**
	 * Note this is only for first level child.
	 * @param parent
	 * @param tag
	 * @return
	 */
	public static List<Element> getChildElement(Element parent,String tag) {
		if(parent == null ){
			throw new IllegalArgumentException("parent element can't be null");
		}
		if(tag == null ){
			throw new IllegalArgumentException("tag can't be null");
		}

		Iterator<Element> ir = new NodeListIterator(parent.getChildNodes());

		List<Element> list = new ArrayList<Element>();
		while(ir.hasNext()){
			Element ele = ir.next();
			if(tag.equals(ele.getTagName())){
				list.add(ele);
			}
		}

		return list;

	}
	
	public static List<Element> getChildElement(Document parent,String tag) {
		if(parent == null ){
			throw new IllegalArgumentException("parent element can't be null");
		}
		if(tag == null ){
			throw new IllegalArgumentException("tag can't be null");
		}

		Iterator<Element> ir = new NodeListIterator(parent.getChildNodes());

		List<Element> list = new ArrayList<Element>();
		while(ir.hasNext()){
			Element ele = ir.next();
			if(tag.equals(ele.getTagName())){
				list.add(ele);
			}
		}

		return list;

	}

	/**
	 * Note this is only for first level child
	 * @param parent
	 * @param tag
	 * @return
	 */
	public static String getFirstChildElementText(Element parent,String tag) {
		
		
		NodeList nli =parent.getChildNodes();
		
		for(int i=0;i<nli.getLength();++i){
			Node ele = nli.item(i);
			if(ele instanceof Element){
				if(tag.equals(((Element)ele).getTagName())){
					return ele.getTextContent();
				}			
			}
		}
		return null;
	}
	
	public static String getFirstChildElementText(Document parent,String tag) {
		NodeListIterator nli =new NodeListIterator(parent.getChildNodes());
		while(nli.hasNext()){
			Element ele = nli.next();
			if(tag.equals(ele.getTagName())){
				return ele.getTextContent();
			}
		}
		return null;
	}	

	public static String getFirstElementText(Document parent,String tag) {
		NodeList nl = parent.getElementsByTagName(tag);
		if(nl.getLength() >0 ){
			return ((Element) nl.item(0)).getTextContent();
		}
		return null;
	}
	
	public static String getFirstElementText(Element parent,String tag) {
		NodeList nl = parent.getElementsByTagName(tag);
		if(nl.getLength() >0 ){
			return ((Element) nl.item(0)).getTextContent();
		}
		return null;
	}


	public static String getFirstElementAttr(Element parent,String tag,String attr) {
		NodeList nl = parent.getElementsByTagName(tag);
		if(nl.getLength() >0 ){
			return ((Element) nl.item(0)).getAttribute(attr);
		}
		return null;
	}

	public static Document buildDocument(String response) {
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(new StringInputStream(response));
			return dom;

		} catch (ParserConfigurationException pce) {
			throw new IllegalArgumentException("Invalid xml input[" + response
					+ "]", pce);
		} catch (SAXException se) {
			throw new IllegalArgumentException("Invalid xml input[" + response
					+ "]", se);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Invalid xml input[" + response
					+ "]", ioe);
		}
	}

	public static String findTagAttribute(String response, String tag) {

		Pattern pattern = Pattern.compile("<" + tag + "(.*?)>",
				Pattern.MULTILINE | Pattern.DOTALL);
		Matcher m = pattern.matcher(response);
		if (m.find()) {
			return m.group(1);
		}
		return "";

	}
}
