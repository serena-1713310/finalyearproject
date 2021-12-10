package com.recsys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.recsys.service.ALSModelService;

@Controller
@RequestMapping("/als")
public class ALSModelController {
	
	@Autowired
	private ALSModelService service;
	
	public ALSModelController(ALSModelService service) {
		this.service = service;
	}
	
	@RequestMapping(value="/",method=RequestMethod.GET)
	public String showPage() {
		return "recommender/als";
	}
	
	@RequestMapping(value="/train",method=RequestMethod.POST)
	public String trainModel() {
		service.trainModel();
		return "recommender/train_success";
	}
}
	