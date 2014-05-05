package org.literacybridge.acm.mobile;

import java.util.List;

import org.literacybridge.acm.mobile.DownloadController.MultipleChoiceQuestion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import android.app.FragmentTransaction;

public class LibraryFragment extends Fragment {

  // final static String QUESTION = null;
  ListView listView;
  TextView txtView;
  Button resetButton;
  int questionNr;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_library, container,
        false);

    // Get listView from XML for answers
    listView = (ListView) rootView.findViewById(R.id.libListView);

    // Get textView from XML for question
    txtView = (TextView) rootView.findViewById(R.id.libTextView);

    // Instantiate downloadController singleton
    final DownloadController downloadController = DownloadController
        .getInstance();

    // determine questions nr
    Bundle args = getArguments();
    if (args != null && args.containsKey("questionNumber"))
      questionNr = args.getInt("questionNumber");
    else
      // Start with the first question
      questionNr = 0;

    Log.d("bruno", "Question number was set to:" + questionNr);

    // Get button from XML for question
    resetButton = (Button) rootView.findViewById(R.id.libButton);
    resetButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        downloadController.reset();

        // Instantiate new fragment
        LibraryFragment libFrag = new LibraryFragment();

        // Pass current question number to new fragment
        final Bundle args = new Bundle();
        args.putInt("questionNumber", 0);
        libFrag.setArguments(args);

        Log.d("bruno", "Passing argument to new fragment:" + args.toString());

        // Switch to new fragment
        FragmentTransaction transaction = getActivity()
            .getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, libFrag);
        transaction.addToBackStack(null);
        transaction.commit();

      }
    });

    // Get answers for first question

    MultipleChoiceQuestion question = downloadController
        .getQuestion(questionNr);
    final List<String> answerList = question.getAnswers();
    txtView.setText(question.getQuestion());

    String[] array = answerList.toArray(new String[answerList.size()]);

    ArrayAdapter<String> answerAdapter = new ArrayAdapter<String>(
        this.getActivity(), R.layout.simple_list_item, R.id.label, array);

    listView.setAdapter(answerAdapter);

    // Create onItemClickListener for listview
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        // Toast.makeText(
        // getActivity().getApplicationContext(),
        // answerList.get(position).toString() ,1).show();

        // Set selected answer in DownloadController
        downloadController.setAnswer(questionNr, position);

        int totalNumberOfQuestions = downloadController.getNumberOfQuestions();

        if (questionNr == totalNumberOfQuestions - 1) {
          // Reached latest question!

          // TODO: Initialize copying...

          Toast.makeText(getActivity().getApplicationContext(),
              "Reached final question", 1).show();

        } else {

          // Instantiate new fragment
          LibraryFragment libFrag = new LibraryFragment();

          // Pass current question number to new fragment
          final Bundle args = new Bundle();
          args.putInt("questionNumber", questionNr + 1);
          libFrag.setArguments(args);

          Log.d("bruno", "Passing argument to new fragment:" + args.toString());

          // Switch to new fragment
          FragmentTransaction transaction = getActivity()
              .getSupportFragmentManager().beginTransaction();
          transaction.replace(R.id.fragment_container, libFrag);
          transaction.addToBackStack(null);
          transaction.commit();

        }

      }
    });

    return rootView;

  }

}
