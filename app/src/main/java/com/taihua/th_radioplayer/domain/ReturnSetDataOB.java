package com.taihua.th_radioplayer.domain;

import java.util.List;

public class ReturnSetDataOB {
	private List<ReturnSetDataCarouselOB> carousel;
	private List<ReturnSetDataBroadcastOB> broadcast;

	public List<ReturnSetDataCarouselOB> getCarousel() {
		return carousel;
	}

	public void setCarousel(List<ReturnSetDataCarouselOB> carousel) {
		this.carousel = carousel;
	}

	public List<ReturnSetDataBroadcastOB> getBroadcast() {
		return broadcast;
	}

	public void setBroadcast(List<ReturnSetDataBroadcastOB> broadcast) {
		this.broadcast = broadcast;
	}

	@Override
	public String toString() {
		return "ReturnSetDataOB [carousel=" + carousel + ", broadcast="
				+ broadcast + "]";
	}

}
