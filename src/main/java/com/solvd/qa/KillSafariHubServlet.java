package com.solvd.qa;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;
import org.openqa.selenium.json.Json;

public class KillSafariHubServlet extends RegistryBasedServlet {

	private static final long serialVersionUID = -689052184632863461L;

	public KillSafariHubServlet() {
    this(null);
  }

	public KillSafariHubServlet(GridRegistry registry) {
    super(registry);
  }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
		ProxySet proxySet = getRegistry().getAllProxies();
		Iterator<RemoteProxy> iterator = proxySet.iterator();
		Iterable<RemoteProxy> iterable = () -> iterator;
		
		List<RemoteProxy> filteredNodes = StreamSupport.stream(iterable.spliterator(), false)
				.filter(p -> p.getConfig().servlets.contains("com.solvd.qa.KillSafariServlet"))
				.collect(Collectors.toList());
		
		if (filteredNodes.size() != 1) {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(400);
			Map<String, String> map = new HashMap<>();
			map.put("status", "No nodes with servlet 'com.solvd.qa.KillSafariServlet' were found");
			response.getWriter().append(new Json().toJson(map));
		} else {
			HttpClient client = HttpClient.newHttpClient();
			URL nodeURL = filteredNodes.get(0).getRemoteHost();
			String url = String.format("%s://%s:%d/extra/KillSafariServlet", nodeURL.getProtocol(), nodeURL.getHost(),
					nodeURL.getPort());
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
			client.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body)
					.thenAccept(System.out::println).join();

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(200);
			Map<String, String> map = new HashMap<>();
			map.put("status", "safari killing was triggered");
			response.getWriter().append(new Json().toJson(map));
		}
	}
}