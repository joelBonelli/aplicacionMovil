package com.example.yogastudio;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.yogastudio.clases.Profesor;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfesorActivity extends AppCompatActivity {

    private ListView listViewProfesores;
    private final Handler handler = new Handler();
    private Button buttonBack;
    private Button buttonAddProfesor;
    private ExecutorService executorService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor);

        listViewProfesores = findViewById(R.id.listViewProfesores);
        buttonBack = findViewById(R.id.buttonBack);
        buttonAddProfesor = findViewById(R.id.buttonAddProfesor);

        executorService = Executors.newSingleThreadExecutor();

        buttonBack.setOnClickListener(v -> finish()); // Volver a la actividad anterior

        buttonAddProfesor.setOnClickListener(v -> {
            Intent intent = new Intent(ProfesorActivity.this, AgregarProfesorActivity.class);
            startActivity(intent);
        });

        loadProfesores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar la lista de profesores cuando se regresa a esta actividad
        loadProfesores();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()){
            executorService.shutdown();
        }
    }


    private void loadProfesores() {
        executorService.submit(() -> {
            try {
                List<Profesor> profesores = Profesor.obtenerTodos(ProfesorActivity.this);
                handler.post(() -> {
                    // Si tenemos profesores, los mostramos en el ListView
                    if (profesores != null && !profesores.isEmpty()) {
                        ArrayAdapter<Profesor> adapter = new ArrayAdapter<>(ProfesorActivity.this, android.R.layout.simple_list_item_1, profesores);
                        listViewProfesores.setAdapter(adapter);

                        listViewProfesores.setOnItemClickListener((parent, view, position, id) -> {
                            final Profesor profesor = (Profesor) parent.getItemAtPosition(position);

                            AlertDialog.Builder builder = new AlertDialog.Builder(ProfesorActivity.this);
                            builder.setTitle("Opciones")
                                    .setItems(new String[]{"Modificar", "Eliminar"}, (dialog, which) -> {
                                        if (which == 0) {
                                            // Modificar
                                            Intent intent = new Intent(ProfesorActivity.this, AgregarProfesorActivity.class);
                                            intent.putExtra("profesor", profesor);
                                            startActivity(intent);
                                        } else if (which == 1) {
                                            // Eliminar
                                            Profesor.eliminar(ProfesorActivity.this, profesor.getId());
                                            Toast.makeText(ProfesorActivity.this, "Profesor eliminado", Toast.LENGTH_SHORT).show();
                                            // Recargar la lista de profesores
                                            loadProfesores();
                                        }
                                    });
                            builder.create().show();
                        });
                    } else {
                        // Si no hay profesores, mostramos un mensaje
                        Toast.makeText(ProfesorActivity.this, "No hay profesores disponibles", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                handler.post(() -> {
                    // Manejo del error en la UI
                    Toast.makeText(ProfesorActivity.this, "Error al cargar los datos de los profesores", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}