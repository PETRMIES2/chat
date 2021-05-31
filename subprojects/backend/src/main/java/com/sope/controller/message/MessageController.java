
package com.sope.controller.message;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sope.SopeCache;
import com.sope.controller.ApiStructure;
import com.sope.domain.SopeTransactionExecutor;
import com.sope.domain.SopeTransactionExecutor.TransactionConsumer;
import com.sope.domain.chat.ChatService;
import com.sope.domain.firebase.MessageService;
import com.sope.domain.firebase.MessageToChat;

@RestController
@RequestMapping(ApiStructure.MESSAGES)
public class MessageController {

	private SopeTransactionExecutor transactionExecutor;
	private MessageService messageService;
	private ChatService chatService;

	@Inject
	public MessageController(SopeTransactionExecutor transactionExecutor, MessageService messageService,
			ChatService chatService) {
		this.transactionExecutor = transactionExecutor;
		this.messageService = messageService;
		this.chatService = chatService;
	}

	@RequestMapping(method = RequestMethod.POST)
	public void sendMessageToChat(@RequestBody final MessageToChat message) {
		transactionExecutor.write(new TransactionConsumer() {

			@Override
			public void consumeTransaction() {
				if (!SopeCache.SYSTEM_USER.equalsIgnoreCase(message.getAuthor())) {
					SopeCache.putChatMessageToCache(message);
					chatService.saveMessage(message);
				}
				messageService.sendMessageToChat(message);
			}

		});
	}

}
