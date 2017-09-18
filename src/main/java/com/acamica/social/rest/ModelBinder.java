package com.acamica.social.rest;

import java.util.Map;

import com.acamica.social.domain.Query;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ModelBinder implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @ModelAttribute
    public void registerQueries(Model model) {
        Map<String, Query> beans = applicationContext.getBeansOfType(Query.class);
        beans.entrySet().forEach(entry -> model.addAttribute(entry.getKey(), entry.getValue()));
    }

    @ModelAttribute
    public CommandValidator validator() {
        return applicationContext.getBean(CommandValidator.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
