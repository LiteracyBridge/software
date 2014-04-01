package org.literacybridge.acm.mobile.dummy;

import org.literacybridge.acm.mobile.DownloadController;

public class DownloadControllerDemo {
	public static void main(String[] args) throws Exception {
		DownloadController controller = DownloadController.getInstance();
		
		printState();
		controller.setAnswer(0, 0);
		printState();
		controller.setAnswer(1, 4);
		printState();
		controller.setAnswer(0, 2);
		printState();
		controller.setAnswer(1, 1);
		printState();
	}
	
	private static void printState() {
		DownloadController controller = DownloadController.getInstance();
		for (int i = 0; i < controller.getNumberOfQuestions(); i++) {
			System.out.println("Question " + (i + 1) + (controller.getQuestion(i).isAnswered() ? " (answered)" : " (unanswered)"));
			System.out.println(controller.getQuestion(i));
			System.out.println("=====================================================================\n");
		}
	}
}
