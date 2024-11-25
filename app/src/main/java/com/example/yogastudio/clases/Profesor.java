package com.example.yogastudio.clases;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.yogastudio.db.DatabaseHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Profesor implements Serializable {
    private int id;
    private String nombre;
    private int dni;
    private String email;
    private String telefono;

    public Profesor(){}

    public Profesor(String nombre, String email, String telefono, int dni) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.dni = dni;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    @Override
    public String toString() {
        return "\n" + nombre + "\n" +
                "DNI: " + dni + "\n" +
                "Email: " + email + "\n" +
                "Telefono: " + telefono + "\n";
    }



    // Método para insertar
    public static void insertar(Context context, Profesor profesor) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PROFESOR_NOMBRE, profesor.getNombre());
        values.put(DatabaseHelper.COLUMN_PROFESOR_EMAIL, profesor.getEmail());
        values.put(DatabaseHelper.COLUMN_PROFESOR_TELEFONO, profesor.getTelefono());
        values.put(DatabaseHelper.COLUMN_PROFESOR_DNI, profesor.getDni());  // Insertamos el DNI

        db.insert(DatabaseHelper.TABLE_PROFESOR, null, values);
        db.close();
    }


    // Método para obtener todos los profesores
    public static List<Profesor> obtenerTodos(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Profesor> profesores = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PROFESOR, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Profesor profesor = new Profesor();
                profesor.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESOR_ID)));
                profesor.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESOR_NOMBRE)));
                profesor.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESOR_EMAIL)));
                profesor.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESOR_TELEFONO)));
                profesor.setDni(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESOR_DNI)));
                profesores.add(profesor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return profesores;
    }


    // Método para actualizar
    public static void actualizar(Context context, Profesor profesor) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PROFESOR_NOMBRE, profesor.getNombre());
        values.put(DatabaseHelper.COLUMN_PROFESOR_EMAIL, profesor.getEmail());
        values.put(DatabaseHelper.COLUMN_PROFESOR_TELEFONO, profesor.getTelefono());
        values.put(DatabaseHelper.COLUMN_PROFESOR_DNI, profesor.getDni());  // Actualizamos el DNI

        String whereClause = DatabaseHelper.COLUMN_PROFESOR_ID + " = ?";
        String[] whereArgs = {String.valueOf(profesor.getId())};

        db.update(DatabaseHelper.TABLE_PROFESOR, values, whereClause, whereArgs);
        db.close();
    }


    // Método para eliminar
    public static void eliminar(Context context, int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = DatabaseHelper.COLUMN_PROFESOR_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        db.delete(DatabaseHelper.TABLE_PROFESOR, whereClause, whereArgs);
        db.close();
    }


    // Método para buscar profesor por DNI
    public static Profesor buscarPorDni(Context context, int dni) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Profesor profesor = null;

        String selection = DatabaseHelper.COLUMN_PROFESOR_DNI + " = ?";
        String[] selectionArgs = {String.valueOf(dni)};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PROFESOR,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            profesor = new Profesor();
            profesor.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESOR_ID)));
            profesor.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESOR_NOMBRE)));
            profesor.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESOR_EMAIL)));
            profesor.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESOR_TELEFONO)));
            profesor.setDni(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESOR_DNI)));
        }

        cursor.close();
        db.close();

        return profesor; // Devuelve el profesor encontrado o null si no existe
    }





}
