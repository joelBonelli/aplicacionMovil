package com.example.yogastudio.clases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.yogastudio.db.DatabaseHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Curso implements Serializable {
    private int id;
    private String nombre;
    private int profesorId;

    public Curso() {}

    public Curso(String nombre, int profesorId) {
        this.nombre = nombre;
        this.profesorId = profesorId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(int profesorId) {
        this.profesorId = profesorId;
    }

    @Override
    public String toString() {
        return nombre;
    }

    // Método para insertar
    public static void insertar(Context context, Curso curso) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CURSO_NOMBRE, curso.getNombre());
        values.put(DatabaseHelper.COLUMN_CURSO_PROFESOR_ID, curso.getProfesorId());

        db.insert(DatabaseHelper.TABLE_CURSO, null, values);
        db.close();
    }


    // Método para obtener todos los cursos
    public static List<Curso> obtenerTodos(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Curso> cursos = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CURSO, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Curso curso = new Curso();
                curso.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CURSO_ID)));
                curso.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CURSO_NOMBRE)));
                curso.setProfesorId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CURSO_PROFESOR_ID)));
                cursos.add(curso);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cursos;
    }


    // Método para actualizar

    public static void actualizar(Context context, Curso curso) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CURSO_NOMBRE, curso.getNombre());
        values.put(DatabaseHelper.COLUMN_CURSO_PROFESOR_ID, curso.getProfesorId());

        String whereClause = DatabaseHelper.COLUMN_CURSO_ID + " = ?";
        String[] whereArgs = {String.valueOf(curso.getId())};

        db.update(DatabaseHelper.TABLE_CURSO, values, whereClause, whereArgs);
        db.close();
    }


    // Método para eliminar
    public static void eliminar(Context context, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = DatabaseHelper.COLUMN_CURSO_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        db.delete(DatabaseHelper.TABLE_CURSO, whereClause, whereArgs);
        db.close();
    }
}

