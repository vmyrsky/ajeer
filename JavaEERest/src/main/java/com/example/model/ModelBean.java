package com.example.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ModelBean {

	public int id;
	public String content;
	public String details;

	public ModelBean() {
		// JAXB needs this
	}

	public ModelBean(int id, String content, String details) {
		this.setId(id);
		this.setContent(content);
		this.setDetails(details);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}