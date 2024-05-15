package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class checkboxes extends AppCompatActivity {
    Button confirm;
    ArrayList<String> myArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkboxes);

        confirm = findViewById(R.id.confirm_button);
        CheckBox otherCheckbox = findViewById(R.id.checkbox_other);
        CheckBox cheeseCheckbox = findViewById(R.id.checkbox_cheese);
        CheckBox depressionCheckbox = findViewById(R.id.checkbox_depression);
        CheckBox PTSDCheckbox = findViewById(R.id.checkbox_PTSD);
        CheckBox phobiasCheckbox = findViewById(R.id.checkbox_phobias);
        CheckBox anxietyCheckbox = findViewById(R.id.checkbox_anxiety);

        otherCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
        cheeseCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
        depressionCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
        PTSDCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
        phobiasCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
        anxietyCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String item : myArray) {
                    Log.d("array", item);
                }
                finish();//this is how i go back to previous activity
            }
        });
    }

    private final CheckBox.OnCheckedChangeListener checkBoxChangeListener =
            new CheckBox.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CheckBox checkBox = (CheckBox) buttonView;
                    if (isChecked) {
                        myArray.add(checkBox.getText().toString());
                    } else {
                        myArray.remove(checkBox.getText().toString());
                    }
                }
            };
}


