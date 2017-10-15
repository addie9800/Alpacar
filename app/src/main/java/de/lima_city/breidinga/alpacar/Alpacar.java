package de.lima_city.breidinga.alpacar;

import android.app.Application;

/**
 * Created by Addie on 15.10.2017.
 */

public class Alpacar extends Application {
    private int FahrerId;
    public int getFahrerId(){
        return FahrerId;
    }
    public void setFahrerId(int id){
        FahrerId = id;
    }
    private boolean login;
    public boolean getLoginState(){
        return login;
    }
    public void setLoginState (boolean state){
        login = state;
    }
}
