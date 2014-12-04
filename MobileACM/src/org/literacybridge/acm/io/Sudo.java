package org.literacybridge.acm.io;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class Sudo {
  public static final class Output {
    private Output(int returnCode, String stdout, String stderr) {
      this.returnCode = returnCode;
      this.stdout = stdout;
      this.stderr = stderr;
    }

    public final int returnCode;
    public final String stdout;
    public final String stderr;
  }

  public static Output sudo(String... cmds) throws IOException {
    Process p = Runtime.getRuntime().exec("su");
    ProcessOutputConsumer stdout = new ProcessOutputConsumer(p, false);
    ProcessOutputConsumer stderr = new ProcessOutputConsumer(p, true);
    stdout.start();
    stderr.start();

    DataOutputStream os = new DataOutputStream(p.getOutputStream());
    for (String tmpCmd : cmds) {
      Log.d("ROOT", tmpCmd);
      os.writeBytes(tmpCmd + "\n");
    }
    os.writeBytes("exit\n");
    os.flush();
    try {
      int returnCode = p.waitFor();
      stdout.join();
      stderr.join();
      Log.d("ROOT return", "" + returnCode);
      Log.d("ROOT stdout", stdout.output);
      Log.d("ROOT stderr", stderr.output);
      return new Output(p.waitFor(), stdout.output, stderr.output);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException(e);
    }
  }

  public static File[] ls(File directory) throws IOException {
    Output out = Sudo.sudo("ls " + directory.getAbsolutePath());
    if (out.returnCode == 0) {
      File[] files = null;
      String[] lines = out.stdout.split("\n");
      if (lines != null && lines.length > 0) {
        files = new File[lines.length];
        for (int i = 0; i < lines.length; i++) {
          files[i] = new File(directory, lines[i]);
        }
      }
      return files;
    } else {
      throw new IOException(out.stderr);
    }
  }

  public static String cat(File file) throws IOException {
    Output out = Sudo.sudo("cat " + file.getAbsolutePath());
    if (out.returnCode == 0) {
      return out.stdout;
    } else {
      throw new IOException(out.stderr);
    }
  }

  private static final class ProcessOutputConsumer extends Thread {
    private Process proc;
    private boolean stderr;
    private String output;

    private ProcessOutputConsumer(Process proc, boolean stderr) {
      this.proc = proc;
      this.stderr = stderr;
    }

    @Override public void run() {
      BufferedReader br = null;
      try {
        InputStream input = stderr ? proc.getErrorStream() : proc
            .getInputStream();
        br = new BufferedReader(new InputStreamReader(input));
        String line = null;
        StringBuilder builder = new StringBuilder();

        while ((line = br.readLine()) != null) {
          builder.append(line);
          builder.append("\n");
        }

        output = builder.toString();
      } catch (Exception e) {
        Log.e("Sudo", "Error while consuming " + (stderr ? "stderr" : "stdout"), e);
      } finally {
        if (br != null) {
          try {
            br.close();
          } catch (Exception e) {
            // ignore
          }
        }
      }
    }
  }

}
