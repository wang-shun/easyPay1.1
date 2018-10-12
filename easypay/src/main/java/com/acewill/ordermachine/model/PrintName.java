package com.acewill.ordermachine.model;

/**
 * Author：Anch
 * Date：2017/12/7 16:47
 * Desc：
 */
public class PrintName {
	String name;

	public PrintName(String name, boolean isSelect) {
		this.name = name;
		this.isSelect = isSelect;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	boolean isSelect;
}