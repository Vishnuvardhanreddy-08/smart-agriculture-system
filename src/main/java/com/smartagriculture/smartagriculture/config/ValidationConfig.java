package com.smartagriculture.smartagriculture.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * ValidationConfig makes the validator (the thing that checks @NotBlank, @Size...)
 * read its messages from our message files (English and Telugu).
 */
// @Configuration tells Spring this class contains settings.
@Configuration
public class ValidationConfig {

    // @Bean means: Spring creates this object once and uses it as THE validator.
    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }
}