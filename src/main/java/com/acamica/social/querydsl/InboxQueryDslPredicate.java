package com.acamica.social.querydsl;

import java.util.UUID;

import com.acamica.social.domain.InboxQuery;
import com.acamica.social.domain.Message;
import com.acamica.social.domain.MessageRepository;
import com.acamica.social.domain.QMessage;
import com.acamica.social.domain.QUser;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import static com.querydsl.jpa.JPAExpressions.select;
import static java.util.Objects.requireNonNull;

public class InboxQueryDslPredicate extends QueryDslPredicate<Message> implements InboxQuery {
    private final QueryDslPredicateExecutor<Message> repository;

    private UUID userId;

    public InboxQueryDslPredicate(MessageRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public Iterable<Message> findAll() {
        Sort sort = buildSort();
        return this.findAll(sort);
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), buildSort());
        return repository.findAll(buildPredicate(), pageRequest);
    }

    private Sort buildSort() {
        return new Sort(Sort.Direction.DESC, QMessage.message.publishTime.getMetadata().getName());
    }

    @Override
    protected Predicate buildPredicate() {
        requireNonNull(userId, "null user is not a valid search criteria");

        return QMessage.message.author.in(
                   select(QUser.user)
                  .from(QUser.user)
                  .where(
                    QUser.user.followers.contains(
                       select(QUser.user)
                      .from(QUser.user)
                      .where(QUser.user.id.eq(userId)))))
                  .or(
                    QMessage.message.author.id.eq(userId)
                  );
    }
}
