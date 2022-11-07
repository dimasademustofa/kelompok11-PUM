package com.kelompok11.PUM.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kelompok11.PUM.R;
import com.kelompok11.PUM.adapter.KonsultasiAdapter;
import com.kelompok11.PUM.database.DatabaseHelper;
import com.kelompok11.PUM.model.ModelKonsultasi;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class KonsultasiActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    KonsultasiAdapter konsultasiAdapter;
    ArrayList<ModelKonsultasi> modelKonsultasiArrayList = new ArrayList<>();
    DatabaseHelper databaseHelper;
    Toolbar toolbar;
    RecyclerView rvGejalaPenyakit;
    MaterialButton btnHasilDiagnosa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konsultasi);

        setStatusBar();

        databaseHelper = new DatabaseHelper(this);
        if (databaseHelper.openDatabase())
            sqLiteDatabase = databaseHelper.getReadableDatabase();

        toolbar = findViewById(R.id.toolbar);
        rvGejalaPenyakit = findViewById(R.id.rvGejalaPenyakit);
        btnHasilDiagnosa = findViewById(R.id.btnHasilDiagnosa);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        rvGejalaPenyakit.setLayoutManager(new LinearLayoutManager(this));
        konsultasiAdapter = new KonsultasiAdapter(this, modelKonsultasiArrayList);
        rvGejalaPenyakit.setAdapter(konsultasiAdapter);
        rvGejalaPenyakit.setHasFixedSize(true);

        btnHasilDiagnosa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer gejalaTerpilih = new StringBuffer();

                ArrayList<ModelKonsultasi> gejalaList = modelKonsultasiArrayList;
                for (int i = 0; i < gejalaList.size(); i++) {
                    ModelKonsultasi gejala = gejalaList.get(i);
                    if (gejala.isSelected()) {
                        gejalaTerpilih.append(gejala.getStrGejala()).append("#");
                    }
                }

                if (gejalaTerpilih.toString().equals("")) {
                    Toast.makeText(KonsultasiActivity.this, "Silakan pilih gejala dahulu!", Toast.LENGTH_SHORT).show();

                } else {
                    // tampilkan activity hasil diagnosa
                    Intent intent = new Intent(v.getContext(), HasilDiagnosaActivity.class);
                    intent.putExtra("HASIL", gejalaTerpilih.toString());
                    startActivity(intent);
                }
            }
        });

        getListData();
    }

    private void getListData() {
        modelKonsultasiArrayList = databaseHelper.getDaftarGejala();
        if (modelKonsultasiArrayList.size() == 0) {
            rvGejalaPenyakit.setVisibility(View.GONE);
        } else {
            rvGejalaPenyakit.setVisibility(View.VISIBLE);
            konsultasiAdapter = new KonsultasiAdapter(this, modelKonsultasiArrayList);
            rvGejalaPenyakit.setAdapter(konsultasiAdapter);
        }
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (on) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListData();
    }

}
