package com.playshogi.website.gwt.server.servlets;

import com.google.gson.JsonObject;

public class Utils {
    public static String getAsStringOrNull(JsonObject json, String member) {
        return json.has(member) && !json.get(member).isJsonNull()
                ? json.get(member).getAsString()
                : null;
    }
}
