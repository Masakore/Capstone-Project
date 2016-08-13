package com.masakorelab.medicreamtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MainActivity extends AppCompatActivity {
  Tracker mTracker;

  FloatingActionButton fab;
  FloatingActionButton fab1;
  FloatingActionButton fab2;

  private boolean FAB_Status = false;

  //Animations
  Animation show_fab_1;
  Animation hide_fab_1;
  Animation show_fab_2;
  Animation hide_fab_2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    AnalyticsApplication application = (AnalyticsApplication) getApplication();
    mTracker = application.getDefaultTracker();

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    //https://github.com/sitepoint-editors/FloatingActionButton_Animation_Project/blob/master/FloatingActionButtonProject/app/src/main/java/com/valdio/valdioveliu/floatingactionbuttonproject/MainActivity.java
    fab = (FloatingActionButton) findViewById(R.id.fab);
    fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
    fab2 = (FloatingActionButton) findViewById(R.id.fab_2);

    show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
    hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
    show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
    hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);


    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (FAB_Status == false) {
          expandFAB();
          FAB_Status = true;
        } else {
          closeFAB();
          FAB_Status = false;
        }
      }
    });

    fab1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, RecordActivity.class));
      }
    });

    fab2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
      }
    });

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //How to call another activity from menu
    //ref: https://developer.android.com/training/basics/firstapp/starting-activity.html
    if (id == R.id.register_activity) {
      mTracker.send(new HitBuilders.EventBuilder()
          .setCategory(getString(R.string.category_1))
          .setAction(getString(R.string.action_type_1))
          .build());
      startActivity(new Intent(this, RegisterActivity.class));
      return true;
    } else if (id == R.id.record_activity) {
      startActivity(new Intent(this, RecordActivity.class));
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void expandFAB() {
    //Floating Action Button 1
    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
    layoutParams.rightMargin += (int) (fab1.getWidth() * 1.7);
    layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.25);
    fab1.setLayoutParams(layoutParams);
    fab1.startAnimation(show_fab_1);
    fab1.setClickable(true);

//    Floating Action Button 2
    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
    layoutParams2.rightMargin += (int) (fab2.getWidth() * 1.5);
    layoutParams2.bottomMargin += (int) (fab2.getHeight() * 1.5);
    fab2.setLayoutParams(layoutParams2);
    fab2.startAnimation(show_fab_2);
    fab2.setClickable(true);
  }

  private void closeFAB() {
    //Floating Action Button 1
    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
    layoutParams.rightMargin -= (int) (fab1.getWidth() * 1.7);
    layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.25);
    fab1.setLayoutParams(layoutParams);
    fab1.startAnimation(hide_fab_1);
    fab1.setClickable(false);

    //Floating Action Button 2
    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
    layoutParams2.rightMargin -= (int) (fab2.getWidth() * 1.5);
    layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 1.5);
    fab2.setLayoutParams(layoutParams2);
    fab2.startAnimation(hide_fab_2);
    fab2.setClickable(false);
  }
}
