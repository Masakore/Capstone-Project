package com.masakorelab.medicreamtracker;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

  public MainActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_main, container, false);

    //ref: https://developer.android.com/guide/topics/ui/controls/spinner.html
//    Spinner spinner = (Spinner) root.findViewById(R.id.registered_item_spinner);
    // Create an ArrayAdapter using the string array and a default spinner layout
//    CursorAdapter adapter =
    // Specify the layout to use when the list of choices appears
//    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
//    spinner.setAdapter(adapter);

    return root;
  }
}
