package com.acamica.social.rest;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.hal.ResourcesMixin;
import org.springframework.web.util.UriComponentsBuilder;

import static java.util.stream.Collectors.toList;

public class PageResource<R> extends ResourcesMixin<R> {
    private final List<R> content;

    private PageResource(List<R> content) {
        this.content = content;
    }

    @Override
    public Collection<R> getContent() {
        return content;
    }

    public static class PageResourceBuilder<T, R> {
        private final Function<T, R> factory;

        public PageResourceBuilder(Function<T, R> factory) {
            this.factory = factory;
        }

        public PageResource<R> toResource(Page<T> page) {
            List<R> content = page.getContent().stream().map(factory).collect(toList());
            return new PageResource<>(content);
        }

        public PageResource<R> toResource(Page<T> page, Pageable pageable, UriComponentsBuilder uriBuilder) {
            PageResource<R> resource = toResource(page);
            return addPaginationLinks(page, pageable, resource, uriBuilder);
        }

        private PageResource<R> addPaginationLinks(Page<T> page, Pageable pageable, PageResource<R> resource, UriComponentsBuilder builder) {
            builder = builder.cloneBuilder()
                             .replaceQueryParam("page", pageable.getPageNumber())
                             .replaceQueryParam("size", pageable.getPageSize());

            resource.add(new Link(builder.toUriString(), "self"));
            if (page.hasNext()) {
                resource.add(new Link(
                    builder.cloneBuilder()
                           .replaceQueryParam("page", pageable.next().getPageNumber())
                           .toUriString()
                 , "next"));
            }

            if (page.hasPrevious()) {
                resource.add(new Link(
                    builder.cloneBuilder()
                           .replaceQueryParam("page", pageable.previousOrFirst().getPageNumber())
                           .toUriString()
                  ,"previous"));
            }

            return resource;
        }
    }

    public static <T, R> PageResourceBuilder builder(Function<T, R> factory) {
        return new PageResourceBuilder<>(factory);
    }
}
