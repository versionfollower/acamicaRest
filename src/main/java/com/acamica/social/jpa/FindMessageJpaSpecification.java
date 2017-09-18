package com.acamica.social.jpa;

import java.time.LocalDateTime;
import java.util.Optional;

import com.acamica.social.domain.FindMessageQuery;
import com.acamica.social.domain.Message;
import com.acamica.social.domain.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public class FindMessageJpaSpecification extends ComposableJpaSpecification<Message> implements FindMessageQuery {
    public FindMessageJpaSpecification(JpaSpecificationExecutor<Message> repository) {
        super(repository);
    }

    @Override
    public void setAuthorName(Optional<String> username) {
        super.addFilter(username.map(MessageQuerySpecifications::authoredBy));
    }

    @Override
    public void setAuthor(Optional<User> author) {
        super.addFilter(author.map(MessageQuerySpecifications::authoredBy));
    }

    @Override
    public void setPublishedBefore(Optional<LocalDateTime> localDateTime) {
        super.addFilter(localDateTime.map(MessageQuerySpecifications::publishedBefore));
    }

    @Override
    public void setPublishedAfter(Optional<LocalDateTime> localDateTime) {
        super.addFilter(localDateTime.map(MessageQuerySpecifications::publishedAfter));
    }

    @Override
    public void setContains(Optional<String> text) {
        super.addFilter(text.map(MessageQuerySpecifications::contains));
    }
}
