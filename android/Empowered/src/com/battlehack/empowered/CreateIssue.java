package com.battlehack.empowered;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class CreateIssue extends Fragment {
	
	private Button uploadImage;
	 private static final int PICK_IMAGE = 1;
	 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View view = inflater.inflate(R.layout.frag_create, container, false);
        
        Spinner spinner = (Spinner) view.findViewById(R.id.spPledgeAmount);
     // Spinner click listener
      //  spinner.setOnItemSelectedListener((OnItemSelectedListener) this);
 
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("1.00");
        categories.add("2.00");
        categories.add("5.00");
        categories.add("10.00");
        categories.add("15.00");
 
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( this.getActivity(), android.R.layout.simple_spinner_item, categories);
 
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        uploadImage = (Button) view.findViewById(R.id.button2);
        
        uploadImage.setOnClickListener(new OnClickListener() {

        	   @Override
        	   public void onClick(View v) {
        	    selectImageFromGallery();

        	   }

			private void selectImageFromGallery() {
				Intent intent = new Intent();
				  intent.setType("image/*");
				  intent.setAction(Intent.ACTION_GET_CONTENT);
				  startActivityForResult(Intent.createChooser(intent, "Select Picture"),
						    PICK_IMAGE);
				
			}
			
        	  });
        //((TextView)android.findViewById(R.id.textView)).setText("Android");
        return view;
}
	
	

}
