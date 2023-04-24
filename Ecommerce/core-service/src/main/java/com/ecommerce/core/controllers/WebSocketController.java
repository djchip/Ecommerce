package com.ecommerce.core.controllers;

import com.ecommerce.core.models.NotifyTopic;
import com.ecommerce.core.models.NotifyUser;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    
	@MessageMapping("/notify-to-handler")
	@SendTo("/topic/notify-to-handler")
	public NotifyTopic greeting(NotifyUser user) throws Exception {
	    return new NotifyTopic(user.getMessage(), user.getUsername());
	}
}
