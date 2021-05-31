package com.sope;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.sope.configuration.ApplicationConfiguration;
import com.sope.configuration.DatabaseConfiguration;
import com.sope.configuration.WebApplicationConfiguration;
import com.sope.configuration.WebSecurityConfiguration;

public class WebApplicationServletInitalizer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { WebSecurityConfiguration.class, DatabaseConfiguration.class, ApplicationConfiguration.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { WebApplicationConfiguration.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected String getServletName() {
		return "Sope";
	}
}