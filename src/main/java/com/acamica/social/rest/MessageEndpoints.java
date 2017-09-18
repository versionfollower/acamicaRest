package com.acamica.social.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

import com.acamica.social.domain.FindMessageQuery;
import com.acamica.social.domain.GetMessageByIdQuery;
import com.acamica.social.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(path = ApiEndpoints.V1 + "/messages")
public class MessageEndpoints {

    @Transactional(readOnly = true)
    @RequestMapping(method = GET, path = "/{messageId}")
    public ResponseEntity<MessageResource> get(@ModelAttribute GetMessageByIdQuery query) throws MessageNotFoundException {

        Message message = query.findOne().orElseThrow(MessageNotFoundException::new);
        MessageResource resource = MessageResource.builder().toResource(message);

        return ok(resource);
    }

    @Transactional(readOnly = true)
    @RequestMapping(method = GET)
    public ResponseEntity<PageResource<MessageResource>> getMessages(
            @ModelAttribute FindMessageQuery query,
            HttpServletRequest request,
            Pageable maybePageable) {
        Pageable pageable = Optional.ofNullable(maybePageable).orElse(new PageRequest(0, 20));

        Page<Message> page = query.findAll(pageable);
        PageResource<MessageResource> pageResource = MessageResource.builder().toResource(page, request, pageable);

        return ok(pageResource);
    }

    @Transactional
    @RequestMapping(path = "/{messageId}/replies", method = POST)
    public ResponseEntity<MessageResource> reply(
            @PathVariable UUID messageId,
            @RequestBody ReplyMessageCommand command,
            CommandValidator validator)
            throws MessageNotFoundException, UserNotFoundException, URISyntaxException {
        command.setMessageId(messageId);
        validator.validateOrFail(command);

        Message execute = command.execute();
        MessageResource resource = MessageResource.builder().toResource(execute);

        return created(new URI(resource.getId().getHref())).body(resource);
    }
}
