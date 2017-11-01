package de.lima_city.breidinga.alpacar.data;

import android.content.Context;
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
        Fahrt currentFahrt = getItem(position);
        TextView abfahrt = (TextView) list_item.findViewById(R.id.list_item_abfahrt);
        abfahrt.setText(currentFahrt.getAbfahrtsOrt());
        TextView ankunft = (TextView) list_item.findViewById(R.id.list_item_ankunft);
        ankunft.setText(currentFahrt.getAnkunftsOrt());
        TextView plaetze = (TextView) list_item.findViewById(R.id.list_item_freie_plaetze);
        plaetze.setText(String.valueOf(currentFahrt.getPlaetze()));
        TextView datum = (TextView) list_item.findViewById(R.id.list_item_distance);
        datum.setText(currentFahrt.getDate().toString());
        return list_item;
    }
}
