package uk.ac.brighton.uni.ab607.mmorpg.common;

import com.almasb.common.util.Out;

public class Sys {

    public static void logExceptionAndExit(Exception e) {
        Out.i("A critical exception has occurred. Please see the trace below. The system will now shut down");
        Out.e(e);
        System.exit(0);
    }
}
