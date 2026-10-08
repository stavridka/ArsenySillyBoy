package com.mirea.kt.ribo.agapov;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TermAdapter extends ArrayAdapter<Term> {

    private DatabaseHelper databaseHelper;

    public TermAdapter(Context context, ArrayList<Term> terms, DatabaseHelper databaseHelper) {
        super(context, 0, terms);
        this.databaseHelper = databaseHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_term, parent, false);
        }
        final Term term = getItem(position);

        TextView textName = convertView.findViewById(R.id.textTermName);
        TextView textShort = convertView.findViewById(R.id.textTermShort);
        final TextView textStar = convertView.findViewById(R.id.textStar);

        textName.setText(term.getName());
        textShort.setText(term.getDefinition());
        if (term.isFavorite()) {
            textStar.setText("★");
        } else {
            textStar.setText("☆");
        }

        textStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                term.setFavorite(!term.isFavorite());
                databaseHelper.setFavorite(term.getId(), term.isFavorite());
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
