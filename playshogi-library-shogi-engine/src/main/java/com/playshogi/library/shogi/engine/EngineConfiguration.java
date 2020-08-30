package com.playshogi.library.shogi.engine;

import java.io.File;

public class EngineConfiguration {

    public static final EngineConfiguration NORMAL_ENGINE = new EngineConfiguration("/home/jean/shogi/engines" +
            "/YaneuraOu/source/", "./YaneuraOu-by-gcc");
    public static final EngineConfiguration TSUME_ENGINE = new EngineConfiguration("/home/jean/shogi/engines" +
            "/YaneuraOu_Tsume/YaneuraOu/source/", "./YaneuraOu-by-gcc");

    private final File path;
    private final String command;

    public EngineConfiguration(String path, String command) {
        this.path = new File(path);
        this.command = command;
    }

    public File getPath() {
        return path;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "EngineConfiguration{" +
                "path='" + path + '\'' +
                ", command='" + command + '\'' +
                '}';
    }
}
