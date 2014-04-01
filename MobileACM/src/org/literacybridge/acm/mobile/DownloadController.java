package org.literacybridge.acm.mobile;

import java.util.ArrayList;
import java.util.List;

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
		
		@Override public String toString() {
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
		// only temporary until this is hooked up to the dropbox API
		createSampleData(-1);
	}
	
	private List<MultipleChoiceQuestion> questions;
	
	public int getNumberOfQuestions() {
		return questions.size();
	}
	
	public MultipleChoiceQuestion getQuestion(int questionIndex) {
		return questions.get(questionIndex);
	}
	
	public void reset() {
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
		createSampleData(questionIndex);
	}
	
	
	
	
	// only temporary until this is hooked up to the dropbox API
	private void createSampleData(int questionIndex) {
		if (questionIndex == -1) {
			questions.add(new MultipleChoiceQuestion("", new ArrayList<String>()));
			questions.add(new MultipleChoiceQuestion("", new ArrayList<String>()));
			questions.add(new MultipleChoiceQuestion("", new ArrayList<String>()));
		}
		
		if (questionIndex < 0) {
			List<String> answers = new ArrayList<String>();
			answers.add("ALL");
			answers.add("Duori");
			answers.add("Hain");
			answers.add("Jirapa");
			answers.add("Sabuli");
			answers.add("Tuggo");
			answers.add("Ullo");
			answers.add("Wa-Municipal");
			answers.add("Yagha");
			questions.set(0, new MultipleChoiceQuestion("Please select district", answers));
		}
		
		if (questionIndex < 1) {
			boolean all = true;
			
			if (questions.get(0).isAnswered()) {
				if (questions.get(0).getSelectedAnswerIndex() != 0) {
					all = false;
				}
			}

			List<String> answers = new ArrayList<String>();
			answers.add("Village 1");
			answers.add("Village 2");
			answers.add("Village 3");
			if (all) {
				answers.add("Village 4");
				answers.add("Village 5");
			}
			
			questions.set(1, new MultipleChoiceQuestion("Please select village", answers));
		}
		
		if (questionIndex < 2) {
			List<String> answers = new ArrayList<String>();
			answers.add("Default");
			questions.set(2, new MultipleChoiceQuestion("Please select image", answers));
		}
	}
}
