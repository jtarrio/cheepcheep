package org.tarrio.cheepcheep.http;

import java.io.IOException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class OAuthHttpClient implements HttpClient {

	private HttpClient httpClient;
	private OAuthConsumer consumer;
	private OAuthProvider provider;

	volatile private AbortableHttpRequest currentRequest;

	public OAuthHttpClient(OAuthConsumer consumer, OAuthProvider provider) {
		super();
		this.consumer = consumer;
		this.provider = provider;
		this.httpClient = new DefaultHttpClient();
		this.currentRequest = null;
	}

	public OAuthConsumer getConsumer() {
		return consumer;
	}

	public OAuthProvider getProvider() {
		return provider;
	}

	public synchronized void abortCurrentRequest() {
		if (currentRequest != null)
			currentRequest.abort();
	}

	public HttpResponse execute(HttpHost target, HttpRequest request,
			HttpContext context) throws IOException, ClientProtocolException {
		try {
			setCurrentRequest(request);
			sign(request);
			return httpClient.execute(target, request, context);
		} finally {
			setCurrentRequest(null);
		}
	}

	public <T> T execute(HttpHost arg0, HttpRequest request,
			ResponseHandler<? extends T> arg2, HttpContext arg3)
			throws IOException, ClientProtocolException {
		try {
			setCurrentRequest(request);
			sign(request);
			return httpClient.execute(arg0, request, arg2, arg3);
		} finally {
			setCurrentRequest(null);
		}
	}

	public <T> T execute(HttpHost arg0, HttpRequest request,
			ResponseHandler<? extends T> arg2) throws IOException,
			ClientProtocolException {
		try {
			setCurrentRequest(request);
			sign(request);
			return httpClient.execute(arg0, request, arg2);
		} finally {
			setCurrentRequest(null);
		}
	}

	public HttpResponse execute(HttpHost target, HttpRequest request)
			throws IOException, ClientProtocolException {
		try {
			setCurrentRequest(request);
			sign(request);
			return httpClient.execute(target, request);
		} finally {
			setCurrentRequest(null);
		}
	}

	public HttpResponse execute(HttpUriRequest request, HttpContext context)
			throws IOException, ClientProtocolException {
		try {
			setCurrentRequest(request);
			sign(request);
			return httpClient.execute(request, context);
		} finally {
			setCurrentRequest(null);
		}
	}

	public <T> T execute(HttpUriRequest request,
			ResponseHandler<? extends T> arg1, HttpContext arg2)
			throws IOException, ClientProtocolException {
		try {
			setCurrentRequest(request);
			sign(request);
			return httpClient.execute(request, arg1, arg2);
		} finally {
			setCurrentRequest(null);
		}
	}

	public <T> T execute(HttpUriRequest request,
			ResponseHandler<? extends T> arg1) throws IOException,
			ClientProtocolException {
		try {
			setCurrentRequest(request);
			sign(request);
			return httpClient.execute(request, arg1);
		} finally {
			setCurrentRequest(null);
		}
	}

	public HttpResponse execute(HttpUriRequest request) throws IOException,
			ClientProtocolException {
		try {
			setCurrentRequest(request);
			sign(request);
			return httpClient.execute(request);
		} finally {
			setCurrentRequest(null);
		}
	}

	public ClientConnectionManager getConnectionManager() {
		return httpClient.getConnectionManager();
	}

	public HttpParams getParams() {
		return httpClient.getParams();
	}

	private void sign(HttpRequest request) throws ClientProtocolException {
		try {
			consumer.sign(request);
		} catch (OAuthException e) {
			throw new ClientProtocolException(
					"Error signing the request with OAuth", e);
		}
	}

	private void setCurrentRequest(HttpRequest request) {
		if (request instanceof AbortableHttpRequest)
			synchronized (this) {
				currentRequest = (AbortableHttpRequest) request;
			}
	}
}
