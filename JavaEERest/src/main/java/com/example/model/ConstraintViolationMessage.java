package com.example.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
/**
 * This class will provide mappign of error message to the form it can be displayed in UI.
 * @author ethereal
 *
 */
public class ConstraintViolationMessage {

	private String itemName = null;
	private String errorMessage = null;
	private String errorValue = null;
	
	public ConstraintViolationMessage() {
		
	}

	public ConstraintViolationMessage(String itemName, String errorValue, String errorMessage) {
		this.setItemName(itemName);
		this.setErrorValue(errorValue);
		this.setErrorMessage(errorMessage);
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorValue() {
		return errorValue;
	}

	public void setErrorValue(String errorValue) {
		this.errorValue = errorValue;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(this.getItemName());
		sb.append("=").append(this.getErrorValue()).append("]");
		sb.append(": ").append(this.getErrorMessage());
		return sb.toString();
	}
}
