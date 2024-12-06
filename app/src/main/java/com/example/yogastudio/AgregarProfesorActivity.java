package com.example.yogastudio;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogastudio.clases.Profesor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgregarProfesorActivity extends AppCompatActivity {

    private EditText editTextNombre;
    private EditText editTextEmail;
    private EditText editTextTelefono;

    private EditText editTextDni;
    private Button buttonGuardar;

    private Button buttonAtras;
    private Profesor profesor;

    private ExecutorService executorService;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_profesor);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        editTextDni = findViewById(R.id.editTextDni);
        buttonGuardar = findViewById(R.id.buttonGuardar);
        buttonAtras = findViewById(R.id.buttonBack);

        executorService = Executors.newSingleThreadExecutor();

        // Verificar si estamos editando un profesor existente
        if (getIntent().hasExtra("profesor")) {
            profesor = (Profesor) getIntent().getSerializableExtra("profesor");
            if (profesor != null) {
                editTextNombre.setText(profesor.getNombre());
                editTextEmail.setText(profesor.getEmail());
                editTextTelefono.setText(profesor.getTelefono());
                editTextDni.setText(String.valueOf(profesor.getDni()));
            }
        }

        buttonAtras.setOnClickListener(v -> finish());

        buttonGuardar.setOnClickListener(v -> guardarProfesor());
    }

    private void guardarProfesor() {
        String nombre = editTextNombre.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String telefono = editTextTelefono.getText().toString().trim();
        String dniStr = editTextDni.getText().toString().trim();

        // Validaciones
        if (!validarNombre(nombre)) {
            Toast.makeText(this, "El nombre solo debe contener letras", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validarSoloNumeros(dniStr)) {
            Toast.makeText(this, "El DNI debe ser un número válido", Toast.LENGTH_SHORT).show();
            return;
        }
        int dni = Integer.parseInt(dniStr);

        if (!validarEmail(email)) {
            Toast.makeText(this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validarSoloNumeros(telefono)) {
            Toast.makeText(this, "El teléfono solo debe contener números", Toast.LENGTH_SHORT).show();
            return;
        }

        // Operaciones en segundo plano
        executorService.submit(() -> {
            try {
                Profesor profesorExistente = Profesor.buscarPorDni(this, dni);

                if (profesorExistente != null && (profesor == null || profesor.getDni() != dni)) {
                    runOnUiThread(() -> Toast.makeText(this, "Ya existe un profesor registrado con este DNI", Toast.LENGTH_SHORT).show());
                    return;
                }

                if (profesor == null) {
                    // Crear un nuevo profesor
                    profesor = new Profesor(nombre, email, telefono, dni);
                    Profesor.insertar(this, profesor);
                    runOnUiThread(() -> Toast.makeText(this, "Profesor agregado", Toast.LENGTH_SHORT).show());
                } else {
                    // Actualizar el profesor existente
                    profesor.setNombre(nombre);
                    profesor.setEmail(email);
                    profesor.setTelefono(telefono);
                    profesor.setDni(dni);
                    Profesor.actualizar(this, profesor);
                    runOnUiThread(() -> Toast.makeText(this, "Profesor actualizado", Toast.LENGTH_SHORT).show());
                }

                runOnUiThread(this::finish);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al guardar el profesor", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private boolean validarNombre(String nombre) {
        // Expresión regular para validar solo letras y espacios
        return nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$");
    }

    private boolean validarEmail(String email) {
        // Expresión regular para validar correos electrónicos
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean validarSoloNumeros(String texto) {
        // Expresión regular para validar solo números
        return texto.matches("^[0-9]+$");
    }
}
