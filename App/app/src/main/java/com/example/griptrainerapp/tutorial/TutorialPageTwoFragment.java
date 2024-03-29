package com.example.griptrainerapp.tutorial;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.griptrainerapp.R;

public class TutorialPageTwoFragment extends Fragment {

    private OnTutorialPageInteractionListener mListener;

    public TutorialPageTwoFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_tutorial_page_two, container, false);

        Button nextButton = view.findViewById(R.id.nextButton);
        Button previousButton = view.findViewById(R.id.previousButton);
        Button skipButton = view.findViewById(R.id.skipTutorialButton);

        nextButton.setOnClickListener(v -> mListener.onNextPage());
        skipButton.setOnClickListener(v -> mListener.onSkipTutorial());
        previousButton.setOnClickListener(v -> mListener.onPreviousPage());

        return view;
    }
}
