package com.tomakehurst.wiremock.mapping;

import static com.tomakehurst.wiremock.testsupport.WireMatchers.jsonEqualTo;
import static java.util.Collections.emptyList;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tomakehurst.wiremock.common.FileSource;
import com.tomakehurst.wiremock.http.RequestMethod;
import com.tomakehurst.wiremock.testsupport.MockRequestBuilder;

@RunWith(JMock.class)
public class MappingFileWriterListenerTest {

	private MappingFileWriterListener listener;
	private FileSource mappingsFileSource;
	private FileSource filesFileSource;
	
	private Mockery context;
	
	@Before
	public void init() {
		context = new Mockery();
		mappingsFileSource = context.mock(FileSource.class, "mappingsFileSource");
		filesFileSource = context.mock(FileSource.class, "filesFileSource");
		
		listener = new MappingFileWriterListener(mappingsFileSource, filesFileSource);
	}
	
	private static final String SAMPLE_REQUEST_MAPPING =
		"{ 													\n" +
		"	\"request\": {									\n" +
		"		\"method\": \"GET\",						\n" +
		"		\"url\": \"/recorded/content\"				\n" +
		"	},												\n" +
		"	\"response\": {									\n" +
		"		\"status\": 200,							\n" +
		"		\"bodyFileName\": \"recorded-body-1.json\"	\n" +
		"	}												\n" +
		"}													";
	
	@Test
	public void writesMappingFileAndCorrespondingBodyFileOnRequest() {
		context.checking(new Expectations() {{
			allowing(mappingsFileSource).list(); will(returnValue(emptyList()));
			allowing(filesFileSource).list(); will(returnValue(emptyList()));
			one(mappingsFileSource).writeTextFile(with(equal("recorded-mapping-1.json")), with(jsonEqualTo(SAMPLE_REQUEST_MAPPING)));
			one(filesFileSource).writeTextFile("recorded-body-1.json", "Recorded body content");
		}});
		
		Request request = new MockRequestBuilder(context)
			.withMethod(RequestMethod.GET)
			.withUrl("/recorded/content")
			.build();
		
		Response response = new Response(200, "Recorded body content");
		
		listener.requestReceived(request, response);
	}
}
