package com.acamica.social.jpa;

import java.time.LocalDateTime;
import java.util.UUID;

import com.acamica.social.domain.Message;
import com.acamica.social.domain.Message_;
import com.acamica.social.domain.User;
import com.acamica.social.domain.User_;
import org.springframework.data.jpa.domain.Specification;

import static java.util.Objects.requireNonNull;

public class MessageQuerySpecifications {
    public static Specification<Message> publishedAfter(LocalDateTime when) {
        requireNonNull(when, "null date is not a valid search criteria");
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(Message_.publishTime), when);
    }

    public static Specification<Message> publishedBefore(LocalDateTime when) {
        requireNonNull(when, "null date is not a valid search criteria");
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(Message_.publishTime), when);
    }

    public static Specification<Message> authoredBy(String author) {
        requireNonNull(author, "null author is not a valid search criteria");
        return (root, query, cb) -> cb.equal(root.get(Message_.author).get(User_.username), author);
    }

    public static Specification<Message> authoredBy(UUID author) {
        requireNonNull(author, "null author id is not a valid search criteria");
        return (root, query, cb) -> cb.equal(root.get(Message_.author).get(User_.id), author);
    }

    public static Specification<Message> authoredBy(User author) {
        requireNonNull(author, "null author is not a valid search criteria");
        return (root, query, cb) -> cb.equal(root.get(Message_.author), author);
    }

    public static Specification<Message> contains(String text) {
        requireNonNull(text, "null body is not a valid search criteria");
        return (root, query, cb) -> cb.like(cb.lower(root.get(Message_.body)), "%" + text.toLowerCase() + "%");
    }

    public static Specification<Message>messageId(UUID uuid) {
        requireNonNull(uuid, "null uuid is not a valid search criteria");
        return (root, query, cb) -> cb.equal(root.get(Message_.id), uuid);
    }
}
