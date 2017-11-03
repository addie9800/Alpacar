package de.lima_city.breidinga.alpacar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.List;

import de.lima_city.breidinga.alpacar.data.FahrtenAdapter;


/**
 * Created by Addie on 19.10.2017.
 */

public class FahrtenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String ans;
    boolean fahrer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fahrten);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        findViewById(R.id.empty_linear).setVisibility(View.GONE);
        if (getIntent().hasExtra("Result")){
            fahrer = false;
            view(parseJSON(getIntent().getStringExtra("Result")));
            ProgressBar bar = (ProgressBar) findViewById(R.id.progress);
            bar.setVisibility(View.GONE);
        }
        else{
            fahrer = true;
            if (networkInfo != null && networkInfo.isConnected()) {
                getData();
                ListView fahrtListView = (ListView) findViewById(R.id.list);
                fahrtListView.setEmptyView(findViewById(R.id.empty));
                ProgressBar bar = (ProgressBar) findViewById(R.id.progress);
                bar.setVisibility(View.VISIBLE);
            } else {
                ProgressBar bar = (ProgressBar) findViewById(R.id.progress);
                bar.setVisibility(View.GONE);
                TextView empty = (TextView) findViewById(R.id.empty);
                empty.setText("No internet connection");}
        }


    }

    private void getData(){
        AsyncTask loader = new AsyncTask() {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                view(parseJSON(ans));
                ProgressBar bar = (ProgressBar) findViewById(R.id.progress);
                bar.setVisibility(View.GONE);
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                URL url;
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("breidinga.lima-city.de")
                        .appendPath("Datenbank")
                        .appendPath("fahrten.php");
                Uri baseUri = builder.build();
                Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("mode", "query");
                uriBuilder.appendQueryParameter("fahrer", String.valueOf(((Alpacar) getApplication()).getFahrerId()));
                final Uri uri = uriBuilder.build();
                Log.d("FahrtenActivity", uri.toString());
                try{
                    url = new URL(uri.toString());}
                catch (MalformedURLException u){
                    u.printStackTrace();
                    Toast.makeText(getBaseContext(), "Error Creating URL", Toast.LENGTH_SHORT).show();
                    return null;
                }

                // new try
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
                return null;            }
        };
        loader.execute();
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
            Intent intent = new Intent(FahrtenActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_meine_fahrten) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.logout){
            ((Alpacar) getApplication()).setFahrerId(-1);
            ((Alpacar) getApplication()).setLoginState(false);
            SharedPreferences preferences = this.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.putBoolean("login", false);
            editor.putInt("fahrerId", -1);
            editor.clear().apply();
            Intent intent = new Intent(FahrtenActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private ArrayList<Fahrt> parseJSON(String data){
        ArrayList<Fahrt> fahrten = new ArrayList<>();
        try{
            JSONArray result = new JSONArray(data);

            for (int i = 0; i < result.length(); i++) {
                JSONObject fahrt = result.getJSONObject(i);
                String date = fahrt.getString("Datum");
                String abfahrt = fahrt.getString("AbfahrtOrt");
                String ankunft = fahrt.getString("AnkunftOrt");
                int plaetze = fahrt.getInt("Plaetze");
                int fahrerId = fahrt.getInt("Fahrer");
                int id = fahrt.getInt("_id");
                Fahrt fahrt1 = new Fahrt(id, Date.valueOf(date), abfahrt, ankunft, plaetze, fahrerId);
                Log.d("FahrtenActivity", fahrt1.toString());
                fahrten.add(fahrt1);
            }
        }catch (JSONException e){
            e.printStackTrace();
            return fahrten;
        }
        return fahrten;
    }
    private void view (final ArrayList<Fahrt> fahrten){


        // Find a reference to the {@link ListView} in the layout
        ListView fahrtenListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        FahrtenAdapter adapter = new FahrtenAdapter(this, fahrten);
        fahrtenListView.setAdapter(adapter);
        fahrtenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (fahrer){
                    Fahrt item = fahrten.get(position);
                    Intent intent = new Intent(FahrtenActivity.this, MainActivity.class);
                    intent.putExtra("id", item.get_id());
                    intent.putExtra("Abfahrt", item.getAbfahrtsOrt());
                    intent.putExtra("Ankunft", item.getAnkunftsOrt());
                    intent.putExtra("Datum", item.getDate().toString());
                    intent.putExtra("FahrerId", item.getFahrerId());
                    intent.putExtra("Plaetze", item.getPlaetze());
                    finish();
                    startActivity(intent);
                }
                //TODO: OnClick nutzen
            }


        });
        fahrtenListView.setEmptyView( findViewById(R.id.empty_linear));
    }

    public FahrtenActivity() {
        super();
    }

}
