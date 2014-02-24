package com.example.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A simple class for mapping data as key & value pairs. Can be used for example in generating simple lists to xml/json.
 */
@XmlRootElement
public class KeyValuePair {
	private String key = null;
	private String value = null;

	public KeyValuePair() {

	}

	public KeyValuePair(String key, String value) {
		this.setKey(key);
		this.setValue(value);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
