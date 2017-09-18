package com.acamica.social.rest;

import java.util.UUID;

import com.acamica.social.domain.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import static java.util.Objects.requireNonNull;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class UserResource extends ResourceSupport {
    private final String username;
    private final String email;

    public UserResource(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public static UserResourceBuilder builder() {
        return new UserResourceBuilder();
    }

    public static class UserResourceBuilder {
        private UserResourceBuilder() {}

        public UserResource toResource(User entity) {
            requireNonNull(entity, "Can't create a valid resource from a null user");

            UUID userId = entity.getId();
            UserResource resource = createResourceWithId(userId, entity);

            return resource;
        }

        private UserResource createResourceWithId(UUID userId, User entity) {
            requireNonNull(entity, "Can't create a resource with no id");

            UserResource resource = new UserResource(entity.getUsername(), entity.getEmail());
            resource.add(creteSelfLink(userId));
            resource.add(createInboxLink(userId));
            resource.add(createPublishLink(userId));
            resource.add(createFollowers(userId));

            return resource;
        }

        public Link createFollowers(UUID userId) {
            try {
                return linkTo(methodOn(UserEndpoints.class).follow(userId, null, null))
                       .withRel("followers");
            } catch (UserNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public Link creteSelfLink(UUID userId) {
            return linkTo(UserEndpoints.class).slash(userId).withSelfRel();
        }

        public Link createPublishLink(UUID userId) {
            try {
                return linkTo(methodOn(UserEndpoints.class).publishMessage(userId, null, null))
                       .withRel("user-messages");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Link createInboxLink(UUID uuid) {
            Link link = linkTo(methodOn(UserEndpoints.class).inbox(null, new PageRequest(0, 20), null))
                    .withRel("inbox");
           return uuid != null? link.expand(uuid) : link;
        }

        public Link createUsersLink(String rel) {
            return linkTo(UserEndpoints.class).withRel(rel);
        }
    }
}
