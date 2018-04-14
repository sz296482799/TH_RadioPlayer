package com.taihua.th_radioplayer.domain;

import java.util.ArrayList;

public class BaseDataDataIB {

	private BaseDataDataBaseIB base;
	private ArrayList<BaseDataDataIteamIB> music;
	private ArrayList<BaseDataDataIteamIB> broadcast;
	private ArrayList<BaseDataDataIteamIB> carousel;

	public BaseDataDataBaseIB getBase() {
		return base;
	}

	public void setBase(BaseDataDataBaseIB base) {
		this.base = base;
	}

	public ArrayList<BaseDataDataIteamIB> getMusic() {
		return music;
	}

	public void setMusic(ArrayList<BaseDataDataIteamIB> music) {
		this.music = music;
	}

	public ArrayList<BaseDataDataIteamIB> getBroadcast() {
		return broadcast;
	}

	public void setBroadcast(ArrayList<BaseDataDataIteamIB> broadcast) {
		this.broadcast = broadcast;
	}

	public ArrayList<BaseDataDataIteamIB> getCarousel() {
		return carousel;
	}

	public void setCarousel(ArrayList<BaseDataDataIteamIB> carousel) {
		this.carousel = carousel;
	}

	@Override
	public String toString() {
		return "BaseDataDataInputBean [base=" + base + ", music=" + music
				+ ", broadcast=" + broadcast + ", carousel=" + carousel + "]";
	}

}
