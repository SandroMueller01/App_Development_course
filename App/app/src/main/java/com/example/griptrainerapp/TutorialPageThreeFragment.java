package com.example.griptrainerapp;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TutorialPageThreeFragment extends Fragment {

    private OnTutorialPageInteractionListener mListener;

    public TutorialPageThreeFragment() {
        // Required empty public constructor
    }

    // Interface for communication with the activity
    public interface OnTutorialPageInteractionListener {
        void onfinishPage();
        void onpreviousPage();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTutorialPageInteractionListener) {
            mListener = (OnTutorialPageInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTutorialPageInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_page_three, container, false);

        // Initialize your buttons here
        Button finishButton = view.findViewById(R.id.finishButton);
        Button previousButton = view.findViewById(R.id.previousButton);

        // Set up click listeners for the buttons
        finishButton.setOnClickListener(v -> mListener.onfinishPage());
        previousButton.setOnClickListener(v -> mListener.onpreviousPage());

        return view;
    }
}
