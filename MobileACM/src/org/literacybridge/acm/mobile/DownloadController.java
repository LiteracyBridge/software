package org.literacybridge.acm.mobile;

import java.util.ArrayList;
import java.util.List;

import org.literacybridge.acm.mobile.ACMDatabaseInfo.DeploymentPackage;

import android.util.Log;

public class DownloadController {
  private static volatile DownloadController singleton;

  public static synchronized DownloadController getInstance() {
    if (singleton == null) {
      singleton = new DownloadController();
    }
    return singleton;
  }

  public static class MultipleChoiceQuestion {
    public static final int UNANSWERED = -1;

    private final String question;
    private final List<String> answers;
    private int answerIndex;

    private MultipleChoiceQuestion(String question, List<String> answers) {
      this.question = question;
      this.answers = answers;
      reset();
    }

    public int getNumberOfChoices() {
      return answers.size();
    }

    public String getQuestion() {
      return question;
    }

    public List<String> getAnswers() {
      return answers;
    }

    public int getSelectedAnswerIndex() {
      return answerIndex;
    }

    public boolean isAnswered() {
      return answerIndex != UNANSWERED;
    }

    private void reset() {
      // if there is only a single possible answer we auto-select it
      // and pretent this question was already answered. The UI can choose to
      // skip this question in that case.
      this.answerIndex = getNumberOfChoices() == 1 ? 0 : UNANSWERED;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(question);
      builder.append("\n");

      for (int i = 0; i < answers.size(); i++) {
        builder.append("\t");
        builder.append(answers.get(i));
        if (i == getSelectedAnswerIndex()) {
          builder.append("  <--");
        }

        builder.append("\n");
      }

      return builder.toString();
    }
  }

  private DownloadController() {
    this.questions = new ArrayList<DownloadController.MultipleChoiceQuestion>();
  }

  private List<MultipleChoiceQuestion> questions;

  public int getNumberOfQuestions() {
    return questions.size();
  }

  public MultipleChoiceQuestion getQuestion(int questionIndex) {
    return questions.get(questionIndex);
  }

  public void reset() {
    createData(-1);
    for (MultipleChoiceQuestion question : questions) {
      question.reset();
    }
  }

  public void setAnswer(int questionIndex, int answerIndex) {
    questions.get(questionIndex).answerIndex = answerIndex;
    maybeUpdateQuestions(questionIndex);
  }

  private void maybeUpdateQuestions(int questionIndex) {
    // only temporary until this is hooked up to the dropbox API
    createData(questionIndex);
  }

  // only temporary until this is hooked up to the dropbox API
  private void createData(int questionIndex) {
    if (questionIndex == -1) {
      questions.clear();
      questions.add(new MultipleChoiceQuestion("", new ArrayList<String>()));
      questions.add(new MultipleChoiceQuestion("", new ArrayList<String>()));
      questions.add(new MultipleChoiceQuestion("", new ArrayList<String>()));
    }

    IOHandler handler = IOHandler.getInstance();
    if (handler == null) {
      return;
    }
    List<ACMDatabaseInfo> dbs = handler.getDatabaseInfos();

    if (questionIndex == -1) {
      List<String> answers = new ArrayList<String>();
      for (ACMDatabaseInfo db : dbs) {
        answers.add(db.getName());
      }
      questions.set(0, new MultipleChoiceQuestion("Please select ACM",
          answers));
    }


    switch (questionIndex) {
      case 0 :
        int dbIndex = -1;

        if (questions.get(0).isAnswered()) {
          dbIndex = questions.get(0).getSelectedAnswerIndex();
        }

        Log.d("QUESTIONS", "dbIndex = " + dbIndex);

        if (dbIndex != -1) {
          List<String> answers = new ArrayList<String>();
          List<DeploymentPackage> packages = dbs.get(dbIndex).getDeviceImages();
          Log.d("QUESTIONS", "packages = " + packages);
          for (DeploymentPackage p : packages) {
            answers.add(p.getName());
          }

          questions.set(1, new MultipleChoiceQuestion("Please select deployment package",
              answers));
        }
        break;
      case 1 :
        dbIndex = -1;
        int packageIndex = -1;

        if (questions.get(0).isAnswered()) {
          dbIndex = questions.get(0).getSelectedAnswerIndex();
        }

        if (questions.get(1).isAnswered()) {
          packageIndex = questions.get(1).getSelectedAnswerIndex();
        }

        Log.d("QUESTIONS", "dbIndex = " + dbIndex);
        Log.d("QUESTIONS", "packageIndex = " + packageIndex);

        if (dbIndex != -1 && packageIndex != -1) {
          List<String> answers = new ArrayList<String>();
          Log.d("QUESTIONS", "packages = " + dbs.get(dbIndex).getDeviceImages());
          answers.addAll(dbs.get(dbIndex).getDeviceImages().get(packageIndex).getCommunities());

          questions.set(2, new MultipleChoiceQuestion("Please select village",
              answers));
        }
        break;
    }
  }
}
