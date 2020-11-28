package com.playshogi.library.shogi.engine;

import java.io.File;
import java.util.Arrays;

public class EngineConfiguration {

    public static final EngineConfiguration NORMAL_ENGINE = new EngineConfiguration("/home/jean/shogi/engines" +
            "/YaneuraOu/source/", "./YaneuraOu-by-gcc", new String[]{});
    public static final EngineConfiguration TSUME_ENGINE = new EngineConfiguration("/home/jean/shogi/engines" +
            "/YaneuraOu_Tsume/YaneuraOu/source/", "./YaneuraOu-by-gcc",
            new String[]{"setoption name MorePreciseMatePv value false"});

    private final File path;
    private final String command;
    private final String[] options;

    public EngineConfiguration(final String path, final String command, final String[] options) {
        this.path = new File(path);
        this.command = command;
        this.options = options;
    }

    public File getPath() {
        return path;
    }

    public String getCommand() {
        return command;
    }

    public String[] getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return "EngineConfiguration{" +
                "path=" + path +
                ", command='" + command + '\'' +
                ", options=" + Arrays.toString(options) +
                '}';
    }
}
