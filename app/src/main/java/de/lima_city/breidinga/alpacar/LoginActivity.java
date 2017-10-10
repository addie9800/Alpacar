package de.lima_city.breidinga.alpacar;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

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
        if (user.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
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
    }
    //TODO: Check if username is already taken

    public void sendDataButton(View view) {
        if (vorname.getText().toString().isEmpty() || nachname.getText().toString().isEmpty() || mail.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ein oder mehrere Feld(er) nicht ausgefüllt", Toast.LENGTH_LONG).show();
            return;
        }
        CharSequence mailtext = mail.getText();
        if (!Patterns.EMAIL_ADDRESS.matcher(mailtext).matches()) {
            Toast.makeText(this, "Bitte eine gültige E-mail Adresse angeben", Toast.LENGTH_LONG).show();
            return;
        }

        String vname = vorname.getText().toString();
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
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("login", true);
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
        if (user.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }
        String username = user.getText().toString();
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
                ans = ans.replace(" ", "");
                Log.d("test2", ans);
                if (ans.equals("true")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("login", true);
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
