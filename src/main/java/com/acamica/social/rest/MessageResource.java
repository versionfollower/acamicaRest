package com.acamica.social.rest;

import java.util.UUID;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;

import com.acamica.social.domain.Message;
import com.acamica.social.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.util.UriComponentsBuilder;

import static java.util.Objects.requireNonNull;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class MessageResource extends ResourceSupport {
    private final String body;

    public MessageResource(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public static MessageResourceBuilder builder() {
        return new MessageResourceBuilder();
    }

    public static class MessageResourceBuilder {
        private MessageResourceBuilder() {
        }

        public MessageResource toResource(Message message) {
            requireNonNull(message, "Can't build a valid resource from a null message");

            MessageResource resource = new MessageResource(message.getBody());
            resource.add(createAuthorLink(message.getAuthor()));
            resource.add(createSelfLink(message));
            resource.add(createRepliesLink(message));

            return resource;
        }

        public Link createAuthorLink(User author) {
            UserResource authorResource = UserResource.builder().toResource(author);
            return new Link(authorResource.getLink("self").getHref(), "author");
        }

        public Link createSelfLink(Message message) {
            return linkTo(MessageEndpoints.class).slash(message.getId()).withSelfRel();
        }

        public Link createRepliesLink(Message message) {
            try {
                UUID messageId = message != null? message.getId() : null;
                return linkTo(methodOn(MessageEndpoints.class).reply(messageId, null, null)).withRel("replies");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Link createMessagesLink(String rel) {
            return linkTo(MessageEndpoints.class).withRel(rel) ;
        }

        public PageResource<MessageResource> toResource(Page<Message> page, HttpServletRequest request, Pageable pageable) {
            requireNonNull(page, "Can't build a valid resource from a null page");
            requireNonNull(pageable, "Can't build a valid resource from a null pageable");

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString())
                                                                  .query(request.getQueryString());

            Function<Message, MessageResource> factory = builder()::toResource;
            PageResource.PageResourceBuilder<Message, MessageResource> builder = PageResource.builder(factory);

            return builder.toResource(page, pageable, uriBuilder);
        }
    }
}
