package com.acamica.social.rest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import com.acamica.social.Application;
import com.acamica.social.domain.Message;
import com.acamica.social.domain.MessageRepository;
import com.acamica.social.domain.User;
import com.acamica.social.domain.UserRepository;
import com.querydsl.core.types.Predicate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
public class MessageEndpointsTest {
    @Autowired private MockMvc mockMvc;
    @MockBean  private MessageRepository repository;
    @MockBean  private UserRepository userRepository;

    @Test
    public void shouldGetMessage() throws Exception {
        final Message message = new User("pepe", "email@email.com").publish("Hello world");

        when(repository.findOne(any(Predicate.class))).thenReturn(message);

        final String RESOURCE_LOCATION = format("/v1/messages/%s", message.getId());

        mockMvc
           .perform(get(RESOURCE_LOCATION))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(content().json(jsonForMessage(message).toString()));
    }

    @Test
    public void filterMessages() throws Exception {
        final User user = new User("pepe", "email@email.com");
        final PageRequest pageable = new PageRequest(0, 1);
        final List<Message> results = asList(user.publish("m1"), user.publish("m2"));
        final Page<Message> dummyValues = new PageImpl<>(results, pageable, results.size());

        when(repository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(dummyValues);

        final String RESOURCE_LOCATION = format("/v1/messages?authorName=%s&page=%d&size=%d",
                                                user.getUsername(),
                                                pageable.getPageNumber(),
                                                pageable.getPageSize());
        mockMvc
          .perform(get(RESOURCE_LOCATION))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().json(jsonForPage(dummyValues, MessageEndpointsTest::jsonForMessage)));
    }

    @Test
    public void shouldReply() throws Exception {
        final User user = spy(new User("pepe", "email@email.com"));
        final Message message = user.publish("m1");
        final Message reply = user.publish("This is my reply");

        when(user.reply(reply.getBody(), message)).thenReturn(reply);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(message));

        final String RESOURCE_LOCATION = format("/v1/messages/%s/replies", message.getId());

        mockMvc
                .perform(
                   post(RESOURCE_LOCATION)
                    .contentType(APPLICATION_JSON)
                    .content(new JSONObject().put(
                       "content", reply.getBody()).put(
                       "authorId", user.getId())
                       .toString()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(valueOf(jsonForMessage(reply))));
    }

    private static <T> String jsonForPage(Page<T> page, Function<T, JSONObject> mapper) throws JSONException {
        List<JSONObject> messages = page.getContent()
                                        .stream()
                                        .map(mapper)
                                        .collect(toList());

        String self = format("http://localhost/v1/messages?authorName=pepe&page=%d&size=%d", page.getNumber(), page.getSize());
        String next = format("http://localhost/v1/messages?authorName=pepe&size=%d&page=%d", page.getSize(), page.getNumber() + 1);

        return new JSONObject().put(
            "_links", new JSONObject().put(
                "self", new JSONObject().put(
                   "href", self)).put(
                "next", new JSONObject().put(
                   "href", next))).put(
            "_embedded", new JSONObject().put(
                "messageResourceList", new JSONArray(messages))
        ).toString();
    }

    private static JSONObject jsonForMessage(Message message) {
        try {
            return new JSONObject().put(
                "body", message.getBody()).put(
                "_links", new JSONObject().put(
                    "self", new JSONObject().put(
                       "href", format("http://localhost/v1/messages/%s", message.getId()))).put(
                    "author", new JSONObject().put(
                       "href", format("http://localhost/v1/users/%s", message.getAuthor().getId()))));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static JSONObject jsonForLink(String rel, String href) {
        try {
            return new JSONObject().put("rel", rel).put("href", href);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
