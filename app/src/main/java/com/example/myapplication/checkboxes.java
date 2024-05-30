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
        setContentView(R.layout.problems_dialog);

        confirm = findViewById(R.id.confirm_button);

//        CheckBox otherCheckbox = findViewById(R.id.checkbox_bpd);
//        CheckBox paranoiaCheckbox = findViewById(R.id.checkbox_insomnia);
//        CheckBox depressionCheckbox = findViewById(R.id.checkbox_depression);
//        CheckBox PTSDCheckbox = findViewById(R.id.checkbox_PTSD);
//        CheckBox phobiasCheckbox = findViewById(R.id.checkbox_addiction);
//        CheckBox anxietyCheckbox = findViewById(R.id.checkbox_anxiety);
//        CheckBox addictionCheckbox = findViewById(R.id.checkbox_paranoia);
//        CheckBox insomniaCheckbox = findViewById(R.id.checkbox_bodyDismorphia);

        CheckBox cbxDepression = findViewById(R.id.checkbox_depression);
        CheckBox cbxAnxiety = findViewById(R.id.checkbox_anxiety);
        CheckBox cbxPTSD = findViewById(R.id.checkbox_PTSD);
        CheckBox cbxAddiction = findViewById(R.id.checkbox_addiction);
        CheckBox cbxParanoia = findViewById(R.id.checkbox_paranoia);
        CheckBox cbxInsomnia = findViewById(R.id.checkbox_insomnia);
        CheckBox cbxBodyDysmorphia = findViewById(R.id.checkbox_bodyDysmorphia);
        CheckBox cbxBPD = findViewById(R.id.checkbox_bpd);
        CheckBox cbxSchizo = findViewById(R.id.checkbox_schizo);

//        otherCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
//        anxietyCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
//        depressionCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
//        PTSDCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
//        phobiasCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
//        addictionCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
//        paranoiaCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
//        insomniaCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);

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


