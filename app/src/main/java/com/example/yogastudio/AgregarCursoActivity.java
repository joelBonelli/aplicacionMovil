package com.example.yogastudio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yogastudio.clases.Curso;
import com.example.yogastudio.clases.Profesor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgregarCursoActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private Spinner spinnerProfesores;

    private Button buttonGuardar;
    private Button buttonBack;
    private Curso curso;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_curso);

        editTextNombre = findViewById(R.id.editTextNombre);
        spinnerProfesores = findViewById(R.id.spinnerProfesores);
        buttonGuardar = findViewById(R.id.buttonGuardar);
        buttonBack = findViewById(R.id.buttonBack);

        cargarProfesoresEnSpinner();

        if (getIntent().hasExtra("curso")) {
            curso = (Curso) getIntent().getSerializableExtra("curso");
            if (curso != null) {
                editTextNombre.setText(curso.getNombre());
                configurarSpinnerConProfesorSeleccionado();
            }
        }

        buttonBack.setOnClickListener(v -> finish());
        buttonGuardar.setOnClickListener(v -> guardarCurso());
    }

    private void cargarProfesoresEnSpinner() {
        executorService.execute(() -> {
            try {
                List<Profesor> listaProfesores = Profesor.obtenerTodos(this);
                runOnUiThread(() -> {
                    ArrayAdapter<Profesor> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaProfesores);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProfesores.setAdapter(adapter);
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar los profesores", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void configurarSpinnerConProfesorSeleccionado() {
        executorService.execute(() -> {
            try {
                List<Profesor> listaProfesores = Profesor.obtenerTodos(this);
                runOnUiThread(() -> {
                    ArrayAdapter<Profesor> adapter = (ArrayAdapter<Profesor>) spinnerProfesores.getAdapter();
                    int profesorId = curso.getProfesorId();
                    int position = -1;

                    for (int i = 0; i < adapter.getCount(); i++) {
                        Profesor profesor = adapter.getItem(i);
                        if (profesor != null && profesor.getId() == profesorId) {
                            position = i;
                            break;
                        }
                    }

                    if (position != -1) {
                        spinnerProfesores.setSelection(position);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al configurar el profesor", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void guardarCurso() {
        String nombreCurso = editTextNombre.getText().toString().trim();
        Profesor profesorSeleccionado = (Profesor) spinnerProfesores.getSelectedItem();

        if (!validarNombre(nombreCurso)) {
            Toast.makeText(this, "El nombre del curso solo debe contener letras y números", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombreCurso.isEmpty() || profesorSeleccionado == null) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try {
                if (curso == null) {
                    curso = new Curso(nombreCurso, profesorSeleccionado.getId());
                    Curso.insertar(this, curso);
                    runOnUiThread(() -> Toast.makeText(this, "Curso agregado", Toast.LENGTH_SHORT).show());
                } else {
                    curso.setNombre(nombreCurso);
                    curso.setProfesorId(profesorSeleccionado.getId());
                    Curso.actualizar(this, curso);
                    runOnUiThread(() -> Toast.makeText(this, "Curso actualizado", Toast.LENGTH_SHORT).show());
                }
                runOnUiThread(this::finish);
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al guardar el curso", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private boolean validarNombre(String nombre) {
        return nombre.matches("^[a-zA-Z0-9 ]+$");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}