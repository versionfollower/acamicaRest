package com.acamica.social.querydsl;

import java.time.LocalDateTime;
import java.util.Optional;

import com.acamica.social.domain.FindMessageQuery;
import com.acamica.social.domain.Message;
import com.acamica.social.domain.MessageRepository;
import com.acamica.social.domain.User;

public class FindMessageQueryDslPredicate extends ComposableQueryDslPredicate<Message> implements FindMessageQuery {
    public FindMessageQueryDslPredicate(MessageRepository repository) {
        super(repository);
    }

    @Override
    public void setAuthorName(Optional<String> author) {
        super.addFilter(author.map(MessageQueryFilters::authoredBy));
    }

    @Override
    public void setAuthor(Optional<User> author) {
        super.addFilter(author.map(MessageQueryFilters::authoredBy));
    }

    @Override
    public void setPublishedBefore(Optional<LocalDateTime> localDateTime) {
        super.addFilter(localDateTime.map(MessageQueryFilters::publishedBefore));
    }

    @Override
    public void setPublishedAfter(Optional<LocalDateTime> localDateTime) {
        super.addFilter(localDateTime.map(MessageQueryFilters::publishedAfter));
    }

    public void setContains(Optional<String> text) {
        super.addFilter(text.map(MessageQueryFilters::contains));
    }
}
