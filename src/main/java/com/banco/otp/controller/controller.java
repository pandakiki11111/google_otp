package com.banco.otp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.banco.otp.service.serviceImpl;

@Controller
public class controller {

	@Autowired
	serviceImpl service;
	
	@RequestMapping(value="/getCode", method = RequestMethod.POST)
	public ModelAndView getCode(Model model, @RequestBody Map<String, Object> param) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsonView");
		
		System.out.println("^0^");
		Map<String, Object> result = service.getCode(param);
		
		mv.addObject("result", result);
		
		return mv;
	}
	
	@RequestMapping(value="/getResult", method = RequestMethod.POST)
	public ModelAndView getResult(Model model, @RequestBody Map<String, Object> param) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsonView");
		
		System.out.println("^0^");
		Map<String, Object> result = service.getResult(param);
		
		mv.addObject("result", result);
		return mv;
	}
}
