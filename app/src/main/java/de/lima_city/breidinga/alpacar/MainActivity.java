package de.lima_city.breidinga.alpacar;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;

import de.lima_city.breidinga.alpacar.data.FahrtContract;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.M;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {
    int buttonId;
    String abfahrtsOrt;
    String ankunftsOrt;
    String datumAb;
    String datumRu;
    boolean login;
    boolean fahrer = true;
    int fahrerId;
    SharedPreferences preferences;

   ////// EditText sitze = (EditText) findViewById(R.id.sitze);
   // String abfahrt;


    String ankunft;

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putBoolean("login", ((Alpacar) getApplication()).getLoginState());
        editor.putInt("fahrerId", ((Alpacar) getApplication()).getFahrerId());
        editor.clear().apply();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        login = preferences.getBoolean("login", false);
        ((Alpacar) getApplication()).setLoginState(preferences.getBoolean("login", false));
        ((Alpacar) getApplication()).setFahrerId(preferences.getInt("fahrerId", -1));
        try{
            login = ((Alpacar) getApplication()).getLoginState();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        if (!login){
            startLoginActivity();
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FrameLayout radioFahrer = (FrameLayout) findViewById(R.id.frame_fahrer);
        FrameLayout radioMitfahrer = (FrameLayout) findViewById(R.id.frame_mitfahrer);
        FrameLayout hinfahrt = (FrameLayout) findViewById(R.id.frame_datum_hinfahrt);
        FrameLayout rueckfahrt = (FrameLayout) findViewById(R.id.frame_datum_rueckfahrt);
        NumberPicker picker = (NumberPicker) findViewById(R.id.np);
        picker.setMinValue(1);
        picker.setMaxValue(9);

        // Defining Floating Action Button

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
                Toast toast =  Toast.makeText(getBaseContext(), "Enter", Toast.LENGTH_LONG);
                toast.show();
                //TODO: Feed database
            }
        });

        //Handling Clicks on Button and Radiobutton
//TODO: Alpenhörner soundeffekte

        hinfahrt.setOnClickListener(this);
       rueckfahrt.setOnClickListener(this);
           radioFahrer.setOnClickListener(this);
            radioMitfahrer.setOnClickListener(this);

        // PlaceAutoCompleteFragment


        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        PlaceAutocompleteFragment places2= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment2);
        PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setFilter(typeFilter);
        places2.setFilter(typeFilter);
        places.setHint(getResources().getString(R.string.abfahrt_ort_hint));
        places2.setHint(getResources().getString(R.string.ziel_ort_hint));
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                abfahrtsOrt = place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        places2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                ankunftsOrt = place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Log.d("FahrerId", String.valueOf(fahrerId));
    }
    //Vollständigkeit der Daten überprüfen
    private void validateData(){
        try {
        if ((!formatDate().isEmpty() || formatDate() != null) && (!abfahrtsOrt.isEmpty() || abfahrtsOrt != null) && (!ankunftsOrt.isEmpty() || ankunftsOrt != null)){
            if (fahrer){
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("breidinga.lima-city.de")
                        .appendPath("Datenbank")
                        .appendPath("fahrten.php");
                Uri baseUri = builder.build();
                Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("mode", "insert");
                uriBuilder.appendQueryParameter("Datum", formatDate());
                uriBuilder.appendQueryParameter("AbfahrtOrt", abfahrtsOrt);
                uriBuilder.appendQueryParameter("AnkunftOrt", ankunftsOrt);
                uriBuilder.appendQueryParameter("Fahrer", String.valueOf(((Alpacar) getApplication()).getFahrerId()));
                Uri uri = uriBuilder.build();
                Log.d("uri", uri.toString());
                asyncTaskInsert(uri);
            }else{
                //TODO: Fahrten heraussuchen

            }
        }}catch (NullPointerException e){
            Toast.makeText(getBaseContext(), "Bitte füllen Sie alles aus", Toast.LENGTH_LONG).show();
        }
    }
    private void asyncTaskInsert(final Uri uri){
        AsyncTask<Uri, Void,Void> asyncTask = new AsyncTask<Uri, Void, Void>() {
            @Override
            protected Void doInBackground(Uri... uris) {
                URL url;
                url = null;
                try{
                url = new URL(uri.toString());}
                catch (MalformedURLException e){
                    e.printStackTrace();
                }
                if(url == null){
                    Log.e("MainActivity.java", "Error creating URL");
                    return null;
                }
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try{
                    client.newCall(request).execute();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        asyncTask.execute(uri);
    }

    private String formatDate() {
        TextView DatumHinfahrtText = (TextView) findViewById(R.id.frame_datum_hinfahrt_text);
        String datumHinfahrt = DatumHinfahrtText.getText().toString();
        String oldFormat = "dd/MM/yyyy";
        String newFormat = "yyyy-MM-dd";
        String formattedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        java.util.Date myDate = null;
        try {
            myDate = dateFormat.parse(datumHinfahrt);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formattedDate = timeFormat.format(myDate);
        return formattedDate;
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

        if (id == R.id.nav_new_fahrt) {

        } else if (id == R.id.nav_meine_fahrten) {
            Intent intent = new Intent(MainActivity.this, FahrtenActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.logout){
            ((Alpacar) getApplication()).setFahrerId(-1);
            ((Alpacar) getApplication()).setLoginState(false);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.putBoolean("login", false);
            editor.putInt("fahrerId", -1);
            editor.clear().apply();
            startLoginActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startLoginActivity(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
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
                fahrer = true;
                radioFahrer.setBackgroundColor(getResources().getColor(R.color.colorOneEingeloggt));
                fahrerText.setTextColor(getResources().getColor(R.color.colorTwoEingeloggt));
                radioMitfahrer.setBackgroundColor(getResources().getColor(R.color.colorTwoEingeloggt));
                mitfahrerText.setTextColor(getResources().getColor(R.color.colorOneEingeloggt));
                break;
            case R.id.frame_mitfahrer:
                fahrer = false;
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
                datumRu = i + "-" + (i1 + 1) + "-" + i2;
                button1.setTextColor(getResources().getColor(R.color.colorTwoEingeloggt));
                layout.setBackgroundColor(getResources().getColor(R.color.colorOneEingeloggt));
                break;
        }
    }
}
//TODO: CREDITS FUER ICONS flaticon.com
