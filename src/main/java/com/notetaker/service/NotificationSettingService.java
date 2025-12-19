package com.notetaker.service;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

public interface NotificationSettingService {

	public Object getSettingByCategory(String category, HttpServletRequest request);

	public Object updateSettingByCategory(String category, Map<String, Boolean> setting, HttpServletRequest request);
	
	public Map<String,Boolean> getSettingByeKeyAndCategoryForUser(String category,String userId,List<String> keys);
}
