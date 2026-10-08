package com.mirea.kt.ribo.agapov;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_DATA = "extra_data";

    private static final String TAG = "MainActivity";

    private DatabaseHelper databaseHelper;
    private TermAdapter adapter;
    private ArrayList<Term> shownTerms = new ArrayList<>();

    private EditText editSearch;
    private CheckBox checkFavorites;
    private ListView listTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        databaseHelper = new DatabaseHelper(this);
        loadFirstTerms();

        editSearch = findViewById(R.id.editSearch);
        checkFavorites = findViewById(R.id.checkFavorites);
        listTerms = findViewById(R.id.listTerms);
        listTerms.setEmptyView(findViewById(R.id.textEmpty));

        adapter = new TermAdapter(this, shownTerms, databaseHelper);
        listTerms.setAdapter(adapter);

        listTerms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, TermActivity.class);
                intent.putExtra(TermActivity.EXTRA_TERM_ID, shownTerms.get(position).getId());
                startActivity(intent);
            }
        });

        listTerms.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                confirmDelete(shownTerms.get(position));
                return true;
            }
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateList();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        checkFavorites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateList();
            }
        });

        findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }

    private void loadFirstTerms() {
        SharedPreferences prefs = getSharedPreferences("dictionary_prefs", MODE_PRIVATE);
        if (prefs.getBoolean("data_loaded", false)) {
            Log.d(TAG, "Термины уже есть в БД");
            return;
        }

        try {
            JSONArray data = new JSONArray(getIntent().getStringExtra(EXTRA_DATA));
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                String name = clearSpaces(item.getString("value"));
                String definition = clearSpaces(item.getString("description"));
                databaseHelper.addTerm(name, definition);
            }
            prefs.edit().putBoolean("data_loaded", true).apply();
            Log.i(TAG, "Загружено терминов: " + data.length());
        } catch (Exception e) {
            Log.e(TAG, "Ошибка загрузки терминов", e);
            Toast.makeText(this, R.string.error_load_data, Toast.LENGTH_LONG).show();
        }
    }

    private String clearSpaces(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    private void updateList() {
        String query = editSearch.getText().toString().trim().toLowerCase(new Locale("ru"));

        shownTerms.clear();
        for (Term term : databaseHelper.getAllTerms()) {
            boolean found = term.getName().toLowerCase(new Locale("ru")).contains(query);
            boolean favoriteOk = !checkFavorites.isChecked() || term.isFavorite();
            if (found && favoriteOk) {
                shownTerms.add(term);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_term, null);
        final EditText editName = dialogView.findViewById(R.id.editTermName);
        final EditText editDefinition = dialogView.findViewById(R.id.editTermDefinition);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.add_term)
                .setView(dialogView)
                .setPositiveButton(R.string.button_save, null)
                .setNegativeButton(R.string.button_cancel, null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = clearSpaces(editName.getText().toString());
                String definition = clearSpaces(editDefinition.getText().toString());
                if (name.isEmpty()) {
                    editName.setError(getString(R.string.error_empty_term));
                    return;
                }
                if (definition.isEmpty()) {
                    editDefinition.setError(getString(R.string.error_empty_definition));
                    return;
                }
                databaseHelper.addTerm(name, definition);
                updateList();
                Toast.makeText(MainActivity.this, R.string.term_added, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void confirmDelete(final Term term) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, term.getName()))
                .setPositiveButton(R.string.button_delete, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        databaseHelper.deleteTerm(term.getId());
                        updateList();
                        Toast.makeText(MainActivity.this, R.string.term_deleted, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }
}
