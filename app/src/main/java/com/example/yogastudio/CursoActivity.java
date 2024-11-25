package com.example.yogastudio;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import com.example.yogastudio.clases.Curso;
import com.example.yogastudio.clases.Profesor;


import java.io.Serializable;
import java.util.List;

public class CursoActivity extends AppCompatActivity {

    private ListView listViewCursos;
    private Handler handler = new Handler();
    private Button buttonBack;
    private Button buttonAddCurso;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curso);

        listViewCursos = findViewById(R.id.listViewCursos);
        buttonBack = findViewById(R.id.buttonBack);
        buttonAddCurso = findViewById(R.id.buttonAddCurso);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Volver a la actividad anterior
            }
        });

        buttonAddCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CursoActivity.this, AgregarCursoActivity.class);
                startActivity(intent);
            }
        });

        // Cargar los datos de los profesores en un hilo separado
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Curso> cursos = Curso.obtenerTodos(CursoActivity.this);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Si tenemos profesores, los mostramos en el ListView
                        if (cursos != null && !cursos.isEmpty()) {
                            ArrayAdapter<Curso> adapter = new ArrayAdapter<>(CursoActivity.this, android.R.layout.simple_list_item_1, cursos);
                            listViewCursos.setAdapter(adapter);

                            listViewCursos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    final Curso curso = (Curso) parent.getItemAtPosition(position);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(CursoActivity.this);
                                    builder.setTitle("Opciones")
                                            .setItems(new String[]{"Modificar", "Eliminar"}, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {
                                                        // Modificar
                                                        Intent intent = new Intent(CursoActivity.this, AgregarCursoActivity.class);
                                                        intent.putExtra("curso", (Serializable) curso);
                                                        startActivity(intent);
                                                    } else if (which == 1) {
                                                        // Eliminar
                                                        Curso.eliminar(CursoActivity.this, curso.getId());
                                                        Toast.makeText(CursoActivity.this, "Curso eliminado", Toast.LENGTH_SHORT).show();
                                                        // Recargar la lista de Cursos
                                                        loadCursos();
                                                    }
                                                }
                                            });
                                    builder.create().show();
                                }
                            });

                        } else {
                            // Si no hay cursos, mostramos un mensaje
                            Toast.makeText(CursoActivity.this, "No hay cursos disponibles", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar la lista de profesores cuando se regresa a esta actividad
        loadCursos();
    }

    private void loadCursos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Simulamos la carga de los datos de la base de datos
                List<Curso> cursos = Curso.obtenerTodos(CursoActivity.this);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (cursos != null && !cursos.isEmpty()) {
                            ArrayAdapter<Curso> adapter = new ArrayAdapter<>(CursoActivity.this, android.R.layout.simple_list_item_1, cursos);
                            listViewCursos.setAdapter(adapter);
                        } else {
                            // Si no hay profesores, mostramos un mensaje
                            Toast.makeText(CursoActivity.this, "No hay cursos disponibles", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}