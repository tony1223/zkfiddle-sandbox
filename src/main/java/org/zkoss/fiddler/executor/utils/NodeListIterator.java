package org.zkoss.fiddler.executor.utils;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class NodeListIterator implements Iterator<Element> {

	private NodeList list;

	private int index = 0;

	public NodeListIterator(NodeList list) {
		this.list = list;
	}
	
	public boolean hasNext() {
		return index < list.getLength();
	}

	public Element next() {
		index++;
		return (Element) list.item(index - 1);
	}

	public void remove() {
		throw new UnsupportedOperationException("not supported for removing item in node list");
	}
}
