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

public class AgregarCursoActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private Spinner spinnerProfesores;

    private Button buttonGuardar;
    private Button buttonBack;
    private Curso curso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_curso);

        editTextNombre = findViewById(R.id.editTextNombre);
        spinnerProfesores = findViewById(R.id.spinnerProfesores);
        buttonGuardar = findViewById(R.id.buttonGuardar);
        buttonBack = findViewById(R.id.buttonBack);

        List<Profesor> listaProfesores = Profesor.obtenerTodos(this);

        ArrayAdapter<Profesor> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaProfesores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerProfesores.setAdapter(adapter);

        if (getIntent().hasExtra("curso")) {
            curso = (Curso) getIntent().getSerializableExtra("curso");
            if (curso != null) {
                editTextNombre.setText(curso.getNombre());
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
            }
        }

        buttonBack.setOnClickListener(v -> finish());
        buttonGuardar.setOnClickListener(v -> guardarCurso());

    }
    private void guardarCurso() {
        String nombreCurso = editTextNombre.getText().toString().trim();
        Profesor profesorSeleccionado = (Profesor) spinnerProfesores.getSelectedItem();

        if (!validarNombre(nombreCurso)) {
            // Mostrar mensaje de error si el nombre no es válido
            Toast.makeText(this, "El nombre del curso solo debe contener letras y números", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombreCurso.isEmpty() || profesorSeleccionado == null) {
            // Mostrar mensaje de error si falta algún dato
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (curso == null) {
            // Crear un nuevo curso
            curso = new Curso(nombreCurso, profesorSeleccionado.getId());
            Curso.insertar(this, curso);
            Toast.makeText(this, "Curso agregado", Toast.LENGTH_SHORT).show();
        } else {
            // Actualizar el curso existente
            curso.setNombre(nombreCurso);
            curso.setProfesorId(profesorSeleccionado.getId());
            Curso.actualizar(this, curso);
            Toast.makeText(this, "Curso actualizado", Toast.LENGTH_SHORT).show();
        }

        finish(); // Cierra la actividad actual
    }
    private boolean validarNombre(String nombre) {
        // Expresión regular para validar letras y números
        return nombre.matches("^[a-zA-Z0-9 ]+$");
    }
}