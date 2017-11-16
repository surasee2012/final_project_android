package kmitl.project.surasee2012.eatrightnow.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Random;

import kmitl.project.surasee2012.eatrightnow.model.FoodsListItems;
import kmitl.project.surasee2012.eatrightnow.view.ProfileFragment;
import kmitl.project.surasee2012.eatrightnow.R;
import kmitl.project.surasee2012.eatrightnow.view.RandomFragment;
import kmitl.project.surasee2012.eatrightnow.model.FoodDbAdapter;
import kmitl.project.surasee2012.eatrightnow.view.FoodListFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button randomFragBtn = findViewById(R.id.randomFragBtn);
        Button foodListFragBtn = findViewById(R.id.foodListFragBtn);
        Button profileFragBtn = findViewById(R.id.profileFragBtn);

        if(savedInstanceState == null) {
            initialFragment();
        }

        randomFragBtn.setOnClickListener(this);
        foodListFragBtn.setOnClickListener(this);
        profileFragBtn.setOnClickListener(this);
    }

    private void initialFragment() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, new RandomFragment()).commit();
    }

    @Override
    public void onClick(View view) {
        if (R.id.randomFragBtn == view.getId()) {
            viewFragment(new RandomFragment());
        } else if (R.id.foodListFragBtn == view.getId()) {
            viewFragment(new FoodListFragment());
        } else if (R.id.profileFragBtn == view.getId()) {
            viewFragment(new ProfileFragment());
        }
    }

    private void viewFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
