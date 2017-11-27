package me.spirittalk.library.util;

import java.io.IOException;

/**
 * Created by spirit on 2017/11/27.
 */

public class GiveUpRetryException extends IOException {
    public GiveUpRetryException(String msg) {
        super(msg);
    }
}
