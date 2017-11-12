package de.lima_city.breidinga.alpacar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Addie on 12.11.2017.
 */

public class Impressum extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    SharedPreferences preferences;
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.clear().apply();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_impressum);
        super.onCreate(savedInstanceState);
        preferences = this.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        String gruss = "Hallo " + ((Alpacar)getApplication()).getName();
        ((TextView) header.findViewById(R.id.welcome)).setText(gruss);
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
            Intent intent = new Intent(Impressum.this, MainActivity.class);
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_meine_fahrten) {
            Intent intent = new Intent(Impressum.this, FahrtenActivity.class);
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(Impressum.this, CreditsActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_manage) {

        } //else if (id == R.id.nav_share) {

        //} else if (id == R.id.nav_send) {

        // }
        else if (id == R.id.logout){
            ((Alpacar) getApplication()).setFahrerId(-1);
            ((Alpacar) getApplication()).setLoginState(false);
            SharedPreferences preferences = this.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.putBoolean("login", false);
            editor.putInt("fahrerId", -1);
            editor.clear().apply();
            Intent intent = new Intent(Impressum.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
