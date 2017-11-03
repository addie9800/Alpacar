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

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.SimpleDateFormat;

import de.lima_city.breidinga.alpacar.data.FahrtContract;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.M;
import static de.lima_city.breidinga.alpacar.R.id.fab;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {
    boolean update = false;
    Fahrt fahrt;
    int buttonId;
    String abfahrtsOrt;
    String ankunftsOrt;
    String ans;
    String datumAb;
    String datumRu;
    boolean login;
    boolean fahrer = true;
    int fahrerId;
    SharedPreferences preferences;
    double latAb;
    double latAn;
    double lonAb;
    double lonAn;

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
        if (getIntent().hasExtra("id")){
            update = true;
            fahrt = new Fahrt(getIntent().getIntExtra("id", -1), Date.valueOf(getIntent().getStringExtra("Datum")), getIntent().getStringExtra("Abfahrt"), getIntent().getStringExtra("Ankunft"), getIntent().getIntExtra("Plaetze", -1), getIntent().getIntExtra("FahrerId", -1));
            ((NumberPicker) findViewById(R.id.np)).setValue(fahrt.getPlaetze());
            ((TextView) findViewById(R.id.frame_datum_hinfahrt_text)).setText(fahrt.getDate().toString());
            places.setText(fahrt.getAbfahrtsOrt());
            places2.setText(fahrt.getAnkunftsOrt());
        }
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                abfahrtsOrt = place.getAddress().toString();
                latAb = place.getLatLng().latitude;
                lonAb =  place.getLatLng().longitude;
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        places2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                ankunftsOrt = place.getAddress().toString();
                latAn = place.getLatLng().latitude;
                lonAn =  place.getLatLng().longitude;
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
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("breidinga.lima-city.de")
                        .appendPath("Datenbank");
            if (fahrer){
                builder.appendPath("fahrten.php");
            }else{
                builder.appendPath("fahrtfinder.php");
            }
                Uri baseUri = builder.build();
                Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("mode", "insert");
                uriBuilder.appendQueryParameter("Datum", formatDate());
                uriBuilder.appendQueryParameter("AbfahrtOrt", abfahrtsOrt);
                uriBuilder.appendQueryParameter("latAb", String.valueOf(latAb));
                uriBuilder.appendQueryParameter("lonAb", String.valueOf(lonAb));
                uriBuilder.appendQueryParameter("AnkunftOrt", ankunftsOrt);
                uriBuilder.appendQueryParameter("latAn", String.valueOf(latAn));
                uriBuilder.appendQueryParameter("lonAn", String.valueOf(lonAn));
                uriBuilder.appendQueryParameter("Plaetze", String.valueOf(((NumberPicker) findViewById(R.id.np)).getValue()));
                uriBuilder.appendQueryParameter("Fahrer", String.valueOf(((Alpacar) getApplication()).getFahrerId()));
                Uri uri = uriBuilder.build();
                Log.d("uri", uri.toString());
                asyncTaskInsert(uri);
        }}catch (NullPointerException e){
            Toast.makeText(getBaseContext(), "Bitte füllen Sie alles aus", Toast.LENGTH_LONG).show();
        }
    }
    private void asyncTaskInsert(final Uri uri){
        AsyncTask<Uri, Void,Void> asyncTask = new AsyncTask<Uri, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent(MainActivity.this, FahrtenActivity.class);
                if (!fahrer){
                    Log.d("Mitfahrer", ans);
                    intent.putExtra("Result", ans);}
                finish();
                startActivity(intent);
            }

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
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setReadTimeout(10000 /* milliseconds */);
                    urlConnection.setConnectTimeout(15000 /* milliseconds */);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == 200) {
                        inputStream = urlConnection.getInputStream();
                        ans = readFromStream(inputStream);
                        Log.d("test", ans);
                    } else {
                        Log.e("QueryUtils", "Error response code: " + urlConnection.getResponseCode());
                    }

                } catch (ProtocolException e) {
                    Log.e("QueryUtils", "Problem with the protocol", e);
                } catch (IOException e) {
                    Log.e("QueryUtils", "Problem establishing the connection", e);

                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
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
        if(update){
            menu.add(Menu.NONE, 1, Menu.NONE, "Delete");
        }
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
        }if (id == 1){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("breidinga.lima-city.de")
                    .appendPath("Datenbank");
                builder.appendPath("fahrten.php");
            Uri baseUri = builder.build();
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("mode", "delete");
            uriBuilder.appendQueryParameter("_id", String.valueOf(fahrt.get_id()));
            uriBuilder.appendQueryParameter("AbfahrtOrt", abfahrtsOrt);
            Uri uri = uriBuilder.build();
            Log.d("uri", uri.toString());
            asyncTaskInsert(uri);
        }

        return super.onOptionsItemSelected(item);
    }
    private static String readFromStream(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (stream != null) {
            InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
            BufferedReader reader1 = new BufferedReader(reader);
            String line = reader1.readLine();
            while (line != null) {
                builder.append(line);
                line = reader1.readLine();
            }
            return builder.toString();
        }
        return null;
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
