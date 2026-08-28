package scyang.mutilboard.global.common;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtil {

    private static MessageSource messageSource;

    @Autowired
    private void setMessageSource(MessageSource messageSource){
        MessageUtil.messageSource = messageSource;
    }

    public static String getMessage(String code){
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, locale);
    }

    public static String getMessage(String code, Object... args){
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }
}
