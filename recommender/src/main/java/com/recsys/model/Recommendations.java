package com.recsys.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Recommendations {
	
	private List<RecommendedMovie> recs;
	
	@XmlElement
	public List<RecommendedMovie> getRecList(){
		if(recs == null) {
			recs = new ArrayList<>();
		}
		return recs;
	}

	public int size() {
		return recs.size();
	}
}
