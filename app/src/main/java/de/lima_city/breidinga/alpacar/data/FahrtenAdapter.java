package de.lima_city.breidinga.alpacar.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.lima_city.breidinga.alpacar.Fahrt;
import de.lima_city.breidinga.alpacar.R;

/**
 * Created by Addie on 01.11.2017.
 */

public class FahrtenAdapter extends ArrayAdapter <Fahrt> {

    public FahrtenAdapter(Context context, ArrayList <Fahrt> fahrten){
        super (context, 0, fahrten);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View list_item = convertView;
        if (list_item==null){
            list_item = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        GradientDrawable circle = (GradientDrawable) list_item.findViewById(R.id.list_item_freie_plaetze).getBackground();
        Fahrt currentFahrt = getItem(position);
        circle.setColor(getColor(currentFahrt.getPlaetze()));
        TextView abfahrt = (TextView) list_item.findViewById(R.id.list_item_abfahrt);
        abfahrt.setText(currentFahrt.getAbfahrtsOrt());
        TextView ankunft = (TextView) list_item.findViewById(R.id.list_item_ankunft);
        ankunft.setText(currentFahrt.getAnkunftsOrt());
        TextView plaetze = (TextView) list_item.findViewById(R.id.list_item_freie_plaetze);
        plaetze.setText(String.valueOf(currentFahrt.getPlaetze()));
        if (currentFahrt.getPlaetze() > 7){

        }
        TextView datum = (TextView) list_item.findViewById(R.id.list_item_distance);
        datum.setText(currentFahrt.getDate().toString());
        return list_item;
    }
    private int getColor(int platz){
        int color;
        switch (platz) {
            case 9:
                color = Color.parseColor("#006400");
                return color;

            case 8:
                color = Color.parseColor("#408B00");
                return color;

            case 7:
                color = Color.parseColor("#80B200");
                return color;

            case 6:
                color = Color.parseColor("#BFD800");
                return color;

            case 5:
                color = Color.parseColor("#FFFF00");
                return color;

            case 4:
                color = Color.parseColor("#FFFF00");
                return color;

            case 3:
                color = Color.parseColor("#E2BF00");
                return color;

            case 2:
                color = Color.parseColor("#C58000");
                return color;

            case 1:
                color = Color.parseColor("#A84000");
                return color;

            case 0:
                color = Color.parseColor("#8B0000");
                return color;
        }
        color = Color.parseColor("#000000");
        return color;
    }
}
