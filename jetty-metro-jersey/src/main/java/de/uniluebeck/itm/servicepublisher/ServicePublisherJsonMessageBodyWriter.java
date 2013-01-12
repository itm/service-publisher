package de.uniluebeck.itm.servicepublisher;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("application/json")
public class ServicePublisherJsonMessageBodyWriter implements MessageBodyWriter {

	@Override
	public long getSize(Object obj, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class type, Type genericType, Annotation annotations[], MediaType mediaType) {
		return true;
	}

	@Override
	public void writeTo(Object target, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
						MultivaluedMap httpHeaders, OutputStream outputStream) throws IOException {
		new ObjectMapper().writeValue(outputStream, target);
	}
}