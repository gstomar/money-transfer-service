package com.revolut.retail.ob.controller;

import com.revolut.retail.ob.Application;
import org.junit.rules.ExternalResource;

public class TestServer extends ExternalResource {

    @Override
    protected void before() throws Exception {
        Application.initServer();
        Application.server.start();
    }

    @Override
    protected void after() {
        if (Application.server.isRunning()) {
            try {
                Application.server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

