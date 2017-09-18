package com.acamica.social.rest;

import java.net.URISyntaxException;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public class ApiResource extends ResourceSupport {
    public ApiResource() {
        try {
            this.add(publishLink());
            this.add(inboxLink());
            this.add(usersLink());
            this.add(messagesLink());
            this.add(replyLink());
            this.add(followLink());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Link followLink() {
        return UserResource.builder().createFollowers(null);
    }

    private Link replyLink() {
        return MessageResource.builder().createRepliesLink(null);
    }

    private Link messagesLink() {
        return MessageResource.builder().createMessagesLink("messages");
    }

    private Link publishLink() throws URISyntaxException, UserNotFoundException {
        return UserResource.builder().createPublishLink(null);
    }

    private Link inboxLink() {
        return UserResource.builder().createInboxLink(null);
    }

    private Link usersLink() {
        String rel = "users";
        return UserResource.builder().createUsersLink(rel);
    }
}
