package de.lima_city.breidinga.alpacar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;

import static android.R.attr.button;

public class Content extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener{
    int buttonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        switch (buttonId){
            case R.id.frame_datum_hinfahrt:
                TextView hinfahrt = (TextView) findViewById(R.id.frame_datum_hinfahrt_text);
                hinfahrt.setText("Datum Hinfahrt \n"+ datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear());
                break;

        }
    }

    @Override
    public void onClick(View view) {
        buttonId = view.getId();
        Log.d("Id", "Id is: " + buttonId);
        DatePickerDialog dialog = new DatePickerDialog(this, null, 2017, 12, 24);
        dialog.show();
    }

}

