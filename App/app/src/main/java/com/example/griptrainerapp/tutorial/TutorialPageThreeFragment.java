package com.example.griptrainerapp.tutorial;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.griptrainerapp.R;

public class TutorialPageThreeFragment extends Fragment {

    private OnTutorialPageInteractionListener mListener;

    public TutorialPageThreeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_tutorial_page_three, container, false);

        Button finishButton = view.findViewById(R.id.finishButton);
        Button previousButton = view.findViewById(R.id.previousButton);

        finishButton.setOnClickListener(v -> mListener.onFinishTutorial());
        previousButton.setOnClickListener(v -> mListener.onPreviousPage());

        return view;
    }
}
