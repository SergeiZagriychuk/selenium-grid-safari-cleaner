package com.solvd.qa;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

public class KillSafariProxy extends DefaultRemoteProxy {

    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public KillSafariProxy(RegistrationRequest request, GridRegistry registry) {
        super(request, registry);
    }

    @Override
    public void beforeSession(TestSession session) {
        if ("safari".equals(session.getRequestedCapabilities().get("browserName"))) {
            LOGGER.info("Killing of existent safari process will be executed before session");
            String command = "ps -ef | grep /Applications/Safari.app/Contents/MacOS/Safari | grep -v grep | awk '{print $2}' | xargs kill -9";
            String[] cmd = { "/bin/sh", "-c", command };
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(cmd);
            builder.directory(new File(System.getProperty("user.home")));
            int exitCode;
            try {
                Process process = builder.start();
                exitCode = process.waitFor();
                assert exitCode == 0;
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        super.beforeSession(session);
    }

}