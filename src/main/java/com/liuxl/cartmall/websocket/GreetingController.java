package com.liuxl.cartmall.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Controller
public class GreetingController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	// @MessageMapping注解和我们之前使用的@RequestMapping类似
	// @SendTo注解表示当服务器有消息需要推送的时候，会对订阅了@SendTo中路径的浏览器发送消息。
	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(HelloMessage message) throws Exception {
		System.out.println("-------------hello---------");
		Thread.sleep(1000); // simulated delay
		return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
	}
	
	@RequestMapping("/send")
	@ResponseBody
	public void send(){
		System.out.println("-------------send---------");
		templateTest();
	}
	
	
	//客户端只要订阅了/topic/subscribeTest主题，调用这个方法即可
    public void templateTest() {
        messagingTemplate.convertAndSend("/topic/greetings", new Greeting("服务器主动推的数据"));
    }




    /*********************************************************************************************/
	/**
	 * 广播
	 * 发给所有在线用户
	 *
	 * @param msg
	 */
	public void sendMsg(HelloMessage msg) {
		messagingTemplate.convertAndSend("/topic/greetings", msg);
	}

	/**
	 * 发送给指定用户
	 * @param users
	 * @param msg
	 */
	public void send2Users(List<String> users, HelloMessage msg) {
		users.forEach(userName -> {
			messagingTemplate.convertAndSendToUser(userName, "/msg", msg);
		});
	}
}
