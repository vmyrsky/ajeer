package com.example.providers;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

/* If no Application (or ResourceConfig) subclass is present, Jersey will dynamically add a Jersey container Servlet and set its name to javax.ws.rs.core.Application. The web application path will be scanned and all the root resource classes (the classes annotated with @Path annotation) as well as any providers that are annotated with @Provider annotation packaged with the application will be automatically registered in the JAX-RS application */

/**
 * This class is copied directly from: https://jersey.java.net/documentation/latest/media.html. It is not mandatory to
 * have this class in order for the phonebook app to work, but you can provide general MOXy specific configuration here.
 */
@Provider
public class JsonMoxyConfigurationContextResolver implements ContextResolver<MoxyJsonConfig> {

	private final MoxyJsonConfig config;

	public JsonMoxyConfigurationContextResolver() {
		final Map<String, String> namespacePrefixMapper = new HashMap<String, String>();
		namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");

		config = new MoxyJsonConfig().setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
	}

	@Override
	public MoxyJsonConfig getContext(Class<?> objectType) {
		return config;
	}
}
