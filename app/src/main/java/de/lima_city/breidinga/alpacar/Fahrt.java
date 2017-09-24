package de.lima_city.breidinga.alpacar;

import java.util.Date;

/**
 * Created by Addie on 24.09.2017.
 */

public class Fahrt {
    private int _id;
    private Date date;
    private String abfahrtsOrt;
    private String ankunftsOrt;
    private int plaetze;
    private int fahrerId;
    public Fahrt (int _id, Date date, String abfahrtsOrt, String ankunftsOrt, int plaetze, int fahrerId){
        this._id = _id;
        this.date = date;
        this.abfahrtsOrt = abfahrtsOrt;
        this.ankunftsOrt = ankunftsOrt;
        this.plaetze = plaetze;
        this.fahrerId = fahrerId;
    }
    public Fahrt (Date date, String abfahrtsOrt, String ankunftsOrt, int plaetze, int fahrerId){
        this.date = date;
        this.abfahrtsOrt = abfahrtsOrt;
        this.ankunftsOrt = ankunftsOrt;
        this.plaetze = plaetze;
        this.fahrerId = fahrerId;
    }
    public Date getDate(){
        return date;
    }
    public int get_id(){
        return _id;
    }
    public String getAbfahrtsOrt(){
        return abfahrtsOrt;
    }
    public String getAnkunftsOrt(){
        return ankunftsOrt;
    }
    public int getPlaetze(){
        return plaetze;
    }
    public int getFahrerId(){
        return fahrerId;
    }
    public void set_id(int id){
        _id = id;
    }
}