package com.acamica.social.rest;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.acamica.social.domain.Message;
import com.acamica.social.domain.User;
import com.acamica.social.domain.UserRepository;

public class PublishMessageCommand {
    private final UserRepository userRepository;

    @NotNull(message = "content.empty.message")
    @Size(min = 1, max = 140, message = "content.size.invalid.message")
    private String message;

    @NotNull(message = "publish.user.null.message")
    private UUID userId;

    public PublishMessageCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Message execute() throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return user.publish(message);
    }
}
