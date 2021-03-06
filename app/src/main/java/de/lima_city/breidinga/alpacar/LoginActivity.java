package de.lima_city.breidinga.alpacar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

import de.lima_city.breidinga.alpacar.data.UnicodeBOMInputStream;

/**
 * Created by Addie on 30.09.2017.
 */

public class LoginActivity extends AppCompatActivity {
    EditText user;
    EditText pass;
    EditText vorname;
    EditText nachname;
    EditText mail;
    LinearLayout loginButtonLayout;
    Button loginButton;
    String ans;
    String username;
    String password;
    ProgressBar bar;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);
        vorname = (EditText) findViewById(R.id.Vorname);
        nachname = (EditText) findViewById(R.id.Nachname);
        mail = (EditText) findViewById(R.id.mail);
        loginButtonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        loginButton = (Button) findViewById(R.id.loginButton);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        Log.d("LoginActivityContext", getBaseContext().toString());
    }

    public void register(View view) {
        boolean error = false;
        if (user.getText().toString().isEmpty()) {
            user.setError("Bitte geben Sie einen Benutzernamen ein");
            error = true;
        }else if (verifyUser(user.getText().toString())){
            user.setError("Der Benutzername wird bereits verwendet");
            error = true;
        }
        if (pass.getText().toString().isEmpty()) {
            pass.setError("Bitte geben Sie ein Passwort ein");
            error = true;
        }
        if (error){
            return;
        }
        username = user.getText().toString();
        password = pass.getText().toString();
        user.setVisibility(View.GONE);
        pass.setVisibility(View.GONE);
        loginButtonLayout.setVisibility(View.GONE);
        vorname.setVisibility(View.VISIBLE);
        nachname.setVisibility(View.VISIBLE);
        mail.setVisibility(View.VISIBLE);
        bar.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        findViewById(R.id.agb).setVisibility(View.VISIBLE);
    }
    private boolean verifyUser (String name){
        //TODO: Check if username is already taken
        return false;
    }
    public void sendDataButton(View view) {
        boolean error = false;
        if (vorname.getText().toString().isEmpty()) {
            vorname.setError("Bitte geben Sie Ihren Vornamen ein");
            error = true;
        }
        if (nachname.getText().toString().isEmpty()) {
            nachname.setError("Bitte geben Sie Ihren Nachnamen ein");
            error = true;
        }
        if (mail.getText().toString().isEmpty()) {
            mail.setError("Bitte geben Sie Ihren Nachnamen ein");
            error = true;
        }
        if (!(((CheckBox) findViewById(R.id.agb)).isChecked())){
            ((CheckBox) findViewById(R.id.agb)).setError("Bitte bestätigen Sie die AGB");
            error = true;
        }
        CharSequence mailtext = mail.getText();
        if (!Patterns.EMAIL_ADDRESS.matcher(mailtext).matches()) {
            mail.setError("Bitte geben Sie eine gültige E-Mail Adresse ein");
            error = true;
        }
        if (error){
            return;
        }

        final String vname = vorname.getText().toString();
        String nname = nachname.getText().toString();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("breidinga.lima-city.de")
                .appendPath("Datenbank")
                .appendPath("nutzer.php");
        Uri baseUri = builder.build();
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("mode", "register");
        uriBuilder.appendQueryParameter("user", username);
        uriBuilder.appendQueryParameter("pass", password);
        uriBuilder.appendQueryParameter("vname", vname);
        uriBuilder.appendQueryParameter("nname", nname);
        uriBuilder.appendQueryParameter("email", mailtext.toString());
        final Uri uri = uriBuilder.build();
        AsyncTask<Uri, Void, Void> asyncTask = new AsyncTask<Uri, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ans = ans.replace("\"", "");
                ans = ans.replace(" ", "");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                SharedPreferences.Editor editor = (getBaseContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)).edit();
                editor.putBoolean("login", true);
                editor.putInt("fahrerId", Integer.parseInt(ans));
                editor.putString("name", vname);
                editor.clear().apply();
                ((Alpacar) getApplication()).setLoginState(true);
                ((Alpacar) getApplication()).setFahrerId(Integer.parseInt(ans));
                ((Alpacar) getApplication()).setName(vname);
                Toast.makeText(getBaseContext(), "Registration successful", Toast.LENGTH_LONG).show();
                finish();
                startActivity(intent);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loginButton.setVisibility(View.GONE);
                bar.setVisibility(View.VISIBLE);
            }
//TODO: Fahrer ID abfragen und speichern
            @Override
            protected Void doInBackground(Uri... uris) {
                URL url;
                url = null;
                try {
                    url = new URL(uri.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (url == null) {
                    Log.e("LoginActivity.java", "Error creating URL");
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
                        UnicodeBOMInputStream bomInputStream = new UnicodeBOMInputStream(inputStream);
                        bomInputStream.skipBOM();
                        ans = readFromStream(inputStream);
                        Log.d("test", ans);
                    } else {
                        Log.e("QueryUtils", "Error response code: " + urlConnection.getResponseCode());
                    }


                    // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
                    // build up a list of Earthquake objects with the corresponding data.

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

    public void login(View view) {
        boolean error = false;
        if (user.getText().toString().isEmpty()) {
            user.setError("Bitte geben Sie einen Benutzernamen ein");
            error = true;
        }
        if (pass.getText().toString().isEmpty()) {
            pass.setError("Bitte geben Sie ein Passwort ein");
            error = true;
        }
        if (error){
            return;
        }
        final String username = user.getText().toString();
        String password = pass.getText().toString();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("breidinga.lima-city.de")
                .appendPath("Datenbank")
                .appendPath("nutzer.php");
        Uri baseUri = builder.build();
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("mode", "login");
        uriBuilder.appendQueryParameter("user", username);
        uriBuilder.appendQueryParameter("pass", password);
        final Uri uri = uriBuilder.build();
        Log.d("uri", uri.toString());
        AsyncTask<Uri, Void, Void> asyncTask = new AsyncTask<Uri, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loginButtonLayout.setVisibility(View.GONE);
                bar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.d("test2", ans);
                ans = ans.replace(" ", "");
                ans = ans.replace("\"", "");
                Log.d("test2", ans);
                if (!(ans.equals("false")) || !(ans.equals(""))) {
                    ans = ans.replace("\"", "").trim();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    SharedPreferences.Editor editor = (getBaseContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)).edit();
                    editor.putBoolean("login", true);
                    int fahrer = Integer.valueOf(ans.toString());
                    editor.putInt("fahrerId", fahrer);
                    editor.putString("name", username);
                    editor.clear().apply();
                    ((Alpacar) getApplication()).setLoginState(true);
                    ((Alpacar) getApplication()).setFahrerId(fahrer);
                    ((Alpacar) getApplication()).setName(username);
                    Toast.makeText(getBaseContext(), "Login successful", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected Void doInBackground(Uri... uris) {
                URL url;
                url = null;
                try {
                    url = new URL(uri.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (url == null) {
                    Log.e("MainActivity.java", "Error creating URL");
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
                        UnicodeBOMInputStream bomInputStream = new UnicodeBOMInputStream(inputStream);
                        bomInputStream.skipBOM();
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

}
