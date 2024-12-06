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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CursoActivity extends AppCompatActivity {

    private ListView listViewCursos;
    private Handler handler = new Handler();
    private Button buttonBack;
    private Button buttonAddCurso;
    private ExecutorService executorService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curso);

        listViewCursos = findViewById(R.id.listViewCursos);
        buttonBack = findViewById(R.id.buttonBack);
        buttonAddCurso = findViewById(R.id.buttonAddCurso);

        executorService = Executors.newSingleThreadExecutor();


        buttonBack.setOnClickListener(v -> finish()); // Volver a la actividad anterior

        buttonAddCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CursoActivity.this, AgregarCursoActivity.class);
                startActivity(intent);
            }
        });

        loadCursos();
    }
    @Override
    protected  void onResume(){
        super.onResume();
        loadCursos();
    }


    private void loadCursos() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // Cargar los cursos en segundo plano
                    List<Curso> cursos = obtenerCursos();
                    // Actualizar la interfaz de usuario con los cursos cargados
                    actualizarUIConCursos(cursos);
                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarErrorCargaCursos();
                }
            }
        });
    }

    private List<Curso> obtenerCursos() {
        // Simulamos la carga de los datos de la base de datos
        return Curso.obtenerTodos(CursoActivity.this);
    }

    private void actualizarUIConCursos(List<Curso> cursos) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (cursos != null && !cursos.isEmpty()) {
                    configurarListViewCursos(cursos);
                } else {
                    // Si no hay cursos, mostramos un mensaje
                    Toast.makeText(CursoActivity.this, "No hay cursos disponibles", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void configurarListViewCursos(List<Curso> cursos) {
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
                                    eliminarCurso(curso);
                                }
                            }
                        });
                builder.create().show();
            }
        });
    }

    private void eliminarCurso(Curso curso) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Curso.eliminar(CursoActivity.this, curso.getId());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CursoActivity.this, "Curso eliminado", Toast.LENGTH_SHORT).show();
                            // Recargar la lista de cursos
                            loadCursos();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CursoActivity.this, "Error al eliminar el curso", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void mostrarErrorCargaCursos() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CursoActivity.this, "Error al cargar los cursos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}