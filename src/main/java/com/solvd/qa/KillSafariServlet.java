package com.solvd.qa;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.selenium.json.Json;

public class KillSafariServlet extends HttpServlet {

	private static final long serialVersionUID = -5201901954780053450L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
		String command = "ps -ef | grep /Applications/Safari.app/Contents/MacOS/Safari | grep -v grep | awk '{print $2}' | xargs kill -9";
		String[] cmd = { "/bin/sh", "-c", command };
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(cmd);
		builder.directory(new File(System.getProperty("user.home")));
		Process process = builder.start();
		int exitCode;
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try {
			exitCode = process.waitFor();
			assert exitCode == 0;
		} catch (InterruptedException e) {
			response.setStatus(400);
			Map<String, String> map = new HashMap<>();
			map.put("status", "error");
			map.put("description", e.getMessage());
			response.getWriter().append(new Json().toJson(map));
		}
		response.setStatus(200);
		Map<String, String> map = new HashMap<>();
		map.put("status", "done");
		response.getWriter().append(new Json().toJson(map));
	}

}