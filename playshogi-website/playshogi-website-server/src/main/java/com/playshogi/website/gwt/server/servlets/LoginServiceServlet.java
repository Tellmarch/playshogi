package com.playshogi.website.gwt.server.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.playshogi.website.gwt.server.services.LoginServiceImpl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class LoginServiceServlet extends HttpServlet {

    private final LoginServiceImpl loginService = new LoginServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json; charset=UTF-8");
        Reader reader = req.getReader();
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

        String action = json.get("action").getAsString();
        Object result;

        try {
            switch (action) {
                case "login":
                    result = loginService.login(
                            json.get("username").getAsString(),
                            json.get("password").getAsString()
                    );
                    break;

                case "checkSession":
                    result = loginService.checkSession(
                            json.get("sessionId").getAsString()
                    );
                    break;

                case "logout":
                    result = loginService.logout(
                            json.get("sessionId").getAsString()
                    );
                    break;

                case "register":
                    result = loginService.register(
                            json.get("username").getAsString(),
                            json.get("password").getAsString()
                    );
                    break;

                case "getVersion":
                    result = loginService.getVersion();
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    gson.toJson("Unknown action: " + action, resp.getWriter());
                    return;
            }

            gson.toJson(result, resp.getWriter());

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            gson.toJson(error, resp.getWriter());
        }
    }
}
