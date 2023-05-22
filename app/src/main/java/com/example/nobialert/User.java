package com.example.nobialert;

import java.io.*;
import java.util.*;

public class User implements Serializable {
	private ArrayList<Coin> coins;
	
	public User() {
		coins = new ArrayList<>();
	}

	public ArrayList<Coin> getCoins() {
		return coins;
	}

	public void addCoin(Coin coin) {
		coins.add(coin);
	}

	public void removeCoin(int position) {
		coins.remove(position);
	}

	public void clear() {
		coins.clear();
	}
}
