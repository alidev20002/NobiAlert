package com.example.nobialert;

import java.io.*;

public class Coin implements Serializable{
	String name;
	String limit;
	long price;
	int icon;

	public Coin(String name, String limit, long price) {
		this.name = name;
		this.limit = limit;
		this.price = price;
	}

	public Coin(String name, String limit, long price, int icon) {
		this.name = name;
		this.limit = limit;
		this.price = price;
		this.icon = icon;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getLimit() {
		return limit;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public long getPrice() {
		return price;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getIcon() {
		return icon;
	}
}
