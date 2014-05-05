package org.literacybridge.acm.io;

import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;

public class LocalFS {
  public static OutputStream getOutputStream(Context context, String fileName)
      throws IOException {
    return context.openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
  }
}
