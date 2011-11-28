package com.tomakehurst.wiremock.mapping;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public abstract class AbstractRequestHandler implements RequestHandler {

	protected List<RequestListener> listeners = newArrayList();

	@Override
	public void addRequestListener(RequestListener requestListener) {
		listeners.add(requestListener);
	}

	@Override
	public Response handle(Request request) {
		Response response = handleRequest(request);
		for (RequestListener listener: listeners) {
			listener.requestReceived(request, response);
		}
		response.setOriginalRequest(request);
		return response;
	}
	
	protected abstract Response handleRequest(Request request);
}
