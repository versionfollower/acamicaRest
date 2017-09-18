package com.acamica.social.rest;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.acamica.social.domain.Message;
import com.acamica.social.domain.MessageRepository;
import com.acamica.social.domain.User;
import com.acamica.social.domain.UserRepository;

public class ReplyMessageCommand {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @NotNull(message = "reply.user.null.message")
    private UUID authorId;

    @NotNull(message = "reply.original.null.message")
    private UUID messageId;

    @NotNull(message = "content.empty.message")
    @Size(min = 1, max = 140, message = "content.size.invalid.message")
    private String content;


    public ReplyMessageCommand(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public Message execute() throws MessageNotFoundException, UserNotFoundException {
        Message message = messageRepository.findById(messageId).orElseThrow(MessageNotFoundException::new);
        User user = userRepository.findById(authorId).orElseThrow(UserNotFoundException::new);

        return user.reply(content, message);
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
