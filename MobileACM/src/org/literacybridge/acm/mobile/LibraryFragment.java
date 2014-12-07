package org.literacybridge.acm.mobile;

import java.util.List;

import org.literacybridge.acm.mobile.DownloadController.MultipleChoiceQuestion;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
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
  private ListView listView;
  private TextView txtView;
  private Button resetButton;
  private Button backButton;
  private int questionNr;
  private Tracker tracker;

  private DownloadController downloadController;
 
  
  public class FragmentReceiver1 extends BroadcastReceiver {
      @Override
      public void onReceive(Context context, Intent intent) {
    	 
    	  // Set answers
    	  reloadData();
      }    
}

  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_library, container,
        false);
    
    // Register receiver to get notified about tab clicks
    getActivity().registerReceiver(new FragmentReceiver1(), new IntentFilter("librarytabclicked"));

    // Get listView from XML for answers
    listView = (ListView) rootView.findViewById(R.id.libListView);

    // Get textView from XML for question
    txtView = (TextView) rootView.findViewById(R.id.libTextView);

    
    // Instantiate downloadController singleton
     downloadController = DownloadController.getInstance();

    // determine questions nr
    Bundle args = getArguments();
    if (args != null && args.containsKey("questionNumber"))
      questionNr = args.getInt("questionNumber");
    else
      // Start with the first question
      questionNr = 0;

    // Set up Back button
    backButton = (Button) rootView.findViewById(R.id.libBack);
    backButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
    	  back();
      }

    });

    
    // Set up Reset button
    resetButton = (Button) rootView.findViewById(R.id.libReset);
    resetButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        reset();
      }
    });

    if (questionNr == 0) {
      downloadController.reset();
     this.setData(questionNr);
    
    }
    
  

    
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
        	
        /*
          Toast.makeText(getActivity().getApplicationContext(),
              "Reached final question", Toast.LENGTH_LONG).show();
		*/
        	
        } else {

        	
        	questionNr = questionNr + 1;
        	setData(questionNr);
        	

        }

      }
    });

    // Report analytics
    this.tracker = EasyTracker.getInstance(getActivity());
    
    return rootView;
  }
  
  private void setData(int questionsNr) {
	  // Get answers for first question
	    MultipleChoiceQuestion question = downloadController.getQuestion(questionsNr);
	    
	    txtView.setText(question.getQuestion());
	    
	    List<String> answerList = question.getAnswers();    
	    String[] array = answerList.toArray(new String[answerList.size()]);
	    
	    Log.d("LibraryFragment", "Number of answers:" + Integer.toString(array.length));
	    
	    ArrayAdapter<String> answerAdapter = new ArrayAdapter<String>(
	        this.getActivity(), R.layout.simple_list_item, R.id.label, array);

	    listView.setAdapter(answerAdapter);
  }
 
  public void reset() {
	  
    DownloadController.getInstance().reset();
    questionNr = 0;
    this.setData(questionNr);
    
  }
  
	private void back() {
		
		if (questionNr > 0) {
			questionNr = questionNr - 1;
			this.setData(questionNr);
		}
		
	}

  private void reloadData() {
	  MultipleChoiceQuestion question = downloadController.getQuestion(questionNr);
	   if (question.getAnswers().size() == 0) {
		   reset();
	   }
  }


  @Override
  public void onResume() {

      super.onResume();
      
      this.tracker.set(Fields.SCREEN_NAME, "Library_Screen");
      this.tracker.send( MapBuilder.createAppView().build() );
      
  }
  
  
}
