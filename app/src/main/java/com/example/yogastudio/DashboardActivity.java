package com.example.yogastudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogastudio.clases.Curso;
import com.example.yogastudio.clases.Profesor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private ListView listViewOptions, listViewResumen;
    private TextView btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        listViewOptions = findViewById(R.id.listViewOptions);
        listViewResumen = findViewById(R.id.listViewResumen);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        setupListView();

        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        listViewOptions.setOnItemClickListener((parent, view, position, id) -> {
            // Ir a la actividad de profesores
            if (position == 0)
                startActivity(new Intent(DashboardActivity.this, ProfesorActivity.class));
            else if (position == 1) {
                // Ir a la actividad de cursos
                startActivity(new Intent(DashboardActivity.this, CursoActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupListView();
    }
    private void setupListView() {
        new Thread(() -> {
            List<Profesor> profesores = Profesor.obtenerTodos(DashboardActivity.this);
            List<Curso> cursos = Curso.obtenerTodos(DashboardActivity.this);
            List<String> opciones = Arrays.asList("Profesores", "Cursos");
            List<String> resumen = new ArrayList<>();

            for (Curso curso : cursos) {
                for (Profesor profesor : profesores) {
                    if (profesor.getId() == curso.getProfesorId()) {
                        resumen.add(curso.getNombre() + " - " + profesor.getNombre());
                        break;
                    }
                }
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(DashboardActivity.this, android.R.layout.simple_list_item_1, opciones);
                listViewOptions.setAdapter(adapter);
                listViewOptions.setVisibility(View.VISIBLE);

                ArrayAdapter<String> resumenAdapter = new ArrayAdapter<>(DashboardActivity.this, android.R.layout.simple_list_item_1, resumen);
                listViewResumen.setAdapter(resumenAdapter);
                listViewResumen.setVisibility(View.VISIBLE);
            });
        }).start();
    }
}