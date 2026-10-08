package com.mirea.kt.ribo.agapov;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TermActivity extends AppCompatActivity {

    public static final String EXTRA_TERM_ID = "extra_term_id";

    private static final String TAG = "TermActivity";

    private DatabaseHelper databaseHelper;
    private Term term;
    private Button buttonFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);

        databaseHelper = new DatabaseHelper(this);
        long termId = getIntent().getLongExtra(EXTRA_TERM_ID, -1);
        term = databaseHelper.getTerm(termId);
        Log.d(TAG, "Открыт термин, id = " + termId);

        if (term == null) {
            Toast.makeText(this, R.string.error_term_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView textTitle = findViewById(R.id.textTermTitle);
        TextView textDefinition = findViewById(R.id.textTermDefinition);
        buttonFavorite = findViewById(R.id.buttonFavorite);
        Button buttonShare = findViewById(R.id.buttonShare);

        textTitle.setText(term.getName());
        textDefinition.setText(term.getDefinition());
        showFavoriteText();

        buttonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                term.setFavorite(!term.isFavorite());
                databaseHelper.setFavorite(term.getId(), term.isFavorite());
                showFavoriteText();
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTerm();
            }
        });

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }

    private void showFavoriteText() {
        if (term.isFavorite()) {
            buttonFavorite.setText(R.string.favorite_remove);
        } else {
            buttonFavorite.setText(R.string.favorite_add);
        }
    }

    private void shareTerm() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, term.getName() + " — " + term.getDefinition());
        startActivity(Intent.createChooser(intent, getString(R.string.share_chooser)));
        Log.d(TAG, "Поделиться термином: " + term.getName());
    }
}
