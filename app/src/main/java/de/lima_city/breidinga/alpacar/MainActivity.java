package de.lima_city.breidinga.alpacar;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.w3c.dom.Text;

import de.lima_city.breidinga.alpacar.data.FahrtContract;

import static android.R.attr.button;
import static android.R.attr.name;
import static de.lima_city.breidinga.alpacar.R.id.fab;
import static de.lima_city.breidinga.alpacar.R.id.sitze;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {
    int buttonId;

   ////// EditText sitze = (EditText) findViewById(R.id.sitze);
   // String abfahrt;


    String ankunft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FrameLayout radioFahrer = (FrameLayout) findViewById(R.id.frame_fahrer);
        FrameLayout radioMitfahrer = (FrameLayout) findViewById(R.id.frame_mitfahrer);
        FrameLayout hinfahrt = (FrameLayout) findViewById(R.id.frame_datum_hinfahrt);
        FrameLayout rueckfahrt = (FrameLayout) findViewById(R.id.frame_datum_rueckfahrt);
        NumberPicker picker = (NumberPicker) findViewById(R.id.np);
        picker.setMinValue(1);
        picker.setMaxValue(9);
        SeekBar bar = (SeekBar) findViewById(R.id.seekbar);
        bar.setMax(8);
        // Defining Floating Action Button

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Feed database
                //if(radioFahrer.isChecked()){
                //   saveData();
                //}

            }
        });

        //Handling Clicks on Button and Radiobutton
//TODO: Alpenhörner soundeffekte

        hinfahrt.setOnClickListener(this);
       rueckfahrt.setOnClickListener(this);
           radioFahrer.setOnClickListener(this);
            radioMitfahrer.setOnClickListener(this);

        // PlaceAutoCompleteFragment

        PlaceAutocompleteFragment places2= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment2);
        PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        places.setFilter(typeFilter);
        places2.setFilter(typeFilter);
        places.setHint(getResources().getString(R.string.abfahrt_ort_hint));
        places2.setHint(getResources().getString(R.string.ziel_ort_hint));
        PlaceSelectionListener listener = new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Toast.makeText(getApplicationContext(),place.getName(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();
            }
        };
        places.setOnPlaceSelectedListener(listener);
        places2.setOnPlaceSelectedListener(listener);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void saveData() {
        ContentValues values = new ContentValues();
        EditText sitze = (EditText) findViewById(R.id.sitze);
        int freieSitze = Integer.parseInt(sitze.getText().toString()) ;
            //TODO: ausführen
            values.put(FahrtContract.FahrtEntry.COLUMN_FREIE_PLAETZE, freieSitze);
          //  getContentResolver().insert(currentUri, values);
        }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View view) {
        buttonId = view.getId();
        DatePickerDialog dialog = new DatePickerDialog(this, this, 2017, 12, 24);
        TextView text = (TextView) findViewById(R.id.edit_text_hint);
        TextView fahrerText = (TextView) findViewById(R.id.frame_fahrer_text);
        TextView mitfahrerText = (TextView) findViewById(R.id.frame_mitfahrer_text);
        FrameLayout radioFahrer = (FrameLayout) findViewById(R.id.frame_fahrer);
        FrameLayout radioMitfahrer = (FrameLayout) findViewById(R.id.frame_mitfahrer);
        switch (buttonId){
            case R.id.frame_datum_hinfahrt:
                dialog.show();
                break;
            case R.id.frame_datum_rueckfahrt:
                dialog.show();
                break;
            case R.id.frame_fahrer:
                text.setText(R.string.freie_plaetze);
                radioFahrer.setBackgroundColor(getResources().getColor(R.color.colorOneEingeloggt));
                fahrerText.setTextColor(getResources().getColor(R.color.colorTwoEingeloggt));
                radioMitfahrer.setBackgroundColor(getResources().getColor(R.color.colorTwoEingeloggt));
                mitfahrerText.setTextColor(getResources().getColor(R.color.colorOneEingeloggt));
                break;
            case R.id.frame_mitfahrer:
                text.setText(R.string.anzahl_mitfahrer);
                radioFahrer.setBackgroundColor(getResources().getColor(R.color.colorTwoEingeloggt));
                fahrerText.setTextColor(getResources().getColor(R.color.colorOneEingeloggt));
                radioMitfahrer.setBackgroundColor(getResources().getColor(R.color.colorOneEingeloggt));
                mitfahrerText.setTextColor(getResources().getColor(R.color.colorTwoEingeloggt));
                break;
        }

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        FrameLayout layout = (FrameLayout) findViewById(buttonId);
        switch (buttonId){
            case R.id.frame_datum_hinfahrt:
                TextView button = (TextView) findViewById(R.id.frame_datum_hinfahrt_text);
                button.setText(i2 + "/" + (i1 + 1) + "/" + i);
                button.setTextColor(getResources().getColor(R.color.colorTwoEingeloggt));
                layout.setBackgroundColor(getResources().getColor(R.color.colorOneEingeloggt));
                break;
            case R.id.frame_datum_rueckfahrt:
                TextView button1 = (TextView) findViewById(R.id.frame_datum_rueckfahrt_text);
                button1.setText(i2 + "/" + (i1 + 1) + "/" + i);
                button1.setTextColor(getResources().getColor(R.color.colorTwoEingeloggt));
                layout.setBackgroundColor(getResources().getColor(R.color.colorOneEingeloggt));
                break;
        }
    }
}
