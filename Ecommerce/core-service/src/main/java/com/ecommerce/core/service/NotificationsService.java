package com.ecommerce.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.core.entities.Notifications;


@Service
public interface NotificationsService extends IRootService<Notifications>{
	
	List<Notifications> findNotifyByUsername(String username);
	void seenAll(String username);
}
