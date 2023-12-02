package com.example.griptrainerapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class TutorialActivity extends AppCompatActivity implements OnTutorialPageInteractionListener {

    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager2 = findViewById(R.id.tutorialViewPager); // Make sure the ID matches
        viewPager2.setAdapter(new TutorialAdapter(this));
    }

    @Override
    public void onNextPage() {
        int currentItem = viewPager2.getCurrentItem();
        if (currentItem < viewPager2.getAdapter().getItemCount() - 1) {
            viewPager2.setCurrentItem(currentItem + 1);
        }
    }

    @Override
    public void onPreviousPage() {
        int currentItem = viewPager2.getCurrentItem();
        if (currentItem > 0) {
            viewPager2.setCurrentItem(currentItem - 1);
        }
    }

    @Override
    public void onSkipTutorial() {
        finishTutorial();
    }

    @Override
    public void onFinishTutorial() {
        finishTutorial();
    }

    private void finishTutorial() {
        // Logic to handle the completion of the tutorial
        // Example: Navigate to the main part of your app or save the state in SharedPreferences
        //Intent mainActivityIntent = new Intent(this, MainActivity.class);
        //startActivity(mainActivityIntent);
        //finish();
    }
}
