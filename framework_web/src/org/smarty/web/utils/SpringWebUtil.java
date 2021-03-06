package org.smarty.web.utils;

import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smarty.core.common.BaseConstant;
import org.springframework.context.MessageSource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * spring mvc工具
 * Created Date 2015/04/09
 *
 * @author quliang
 * @version 1.0
 */
public final class SpringWebUtil {
	private final static Log logger = LogFactory.getLog(SpringWebUtil.class);
	private static ServletContext servletContext;
	private static MessageSource messageSource;

	private SpringWebUtil() {
	}

	public static void setServletContext(ServletContext servletContext) {
		if (SpringWebUtil.servletContext != null) {
			throw new IllegalStateException("ServletContextHolder already holded 'servletContext'.");
		}
		SpringWebUtil.servletContext = servletContext;
		logger.info("holded servletContext,displayName:" + servletContext.getServletContextName());
	}

	public static void setMessageSource(MessageSource messageSource) {
		if (SpringWebUtil.messageSource != null) {
			throw new IllegalStateException("ServletContextHolder already holded 'messageSource'.");
		}
		SpringWebUtil.messageSource = messageSource;
		logger.info("holded messageSource");
	}

	public static ServletContext getServletContext() {
		if (servletContext == null) {
			throw new IllegalStateException("'servletContext' property is null,ServletContextHolder not yet init.");
		}
		return servletContext;
	}

	public static RequestAttributes getRequestAttributes() {
		return RequestContextHolder.getRequestAttributes();
	}

	public static HttpServletRequest getServletRequest() {
		ServletRequestAttributes sra = (ServletRequestAttributes) getRequestAttributes();
		return sra.getRequest();
	}

	public static HttpServletResponse getServletResponse() {
		ServletRequestAttributes sra = (ServletRequestAttributes) getRequestAttributes();
		return sra.getResponse();
	}

	public static String getSessionId() {
		ServletRequestAttributes sra = (ServletRequestAttributes) getRequestAttributes();
		return sra.getSessionId();
	}

	public static String getMessage(String key, Object... values) {
		return messageSource.getMessage(key, values, BaseConstant.DEF_LOCALE);
	}

	public static String getMessage(Locale locale, String key, Object... values) {
		return messageSource.getMessage(key, values, locale == null ? BaseConstant.DEF_LOCALE : locale);
	}
}
