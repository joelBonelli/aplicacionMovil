package com.example.yogastudio.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper {
    // Nombre de la base de datos y version
    private static final String DATABASE_NAME = "escuela.db";
    //private static final int DATABASE_VERSION = 1;
    private static final int DATABASE_VERSION = 6;

    // Tablas y columnas para "profesor"
    public static final String TABLE_PROFESOR = "Profesor";
    public static final String COLUMN_PROFESOR_ID = "id";
    public static final String COLUMN_PROFESOR_NOMBRE = "nombre";

    public static final String COLUMN_PROFESOR_DNI = "dni";

    public static final String COLUMN_PROFESOR_EMAIL = "email";
    public static final String COLUMN_PROFESOR_TELEFONO = "telefono";

    // Tablas y columnas para "curso"
    public static final String TABLE_CURSO = "Curso";
    public static final String COLUMN_CURSO_ID = "id";
    public static final String COLUMN_CURSO_NOMBRE = "nombre";
    public static final String COLUMN_CURSO_PROFESOR_ID = "profesor_id";

    //Tablas y columnas para "usuario"
    public static final String TABLE_USUARIO = "Usuario";
    public static final String COLUMN_USUARIO_ID = "id";
    public static final String COLUMN_USUARIO_NOMBRE = "nombre";
    public static final String COLUMN_USUARIO_PASSWORD = "password";


    // Scripts para crear tablas
    private static final String CREATE_TABLE_PROFESOR =
            "CREATE TABLE " + TABLE_PROFESOR + " (" +
                    COLUMN_PROFESOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PROFESOR_NOMBRE + " TEXT NOT NULL, " +
                    COLUMN_PROFESOR_EMAIL + " TEXT, " +
                    COLUMN_PROFESOR_TELEFONO + " TEXT, " +
                    COLUMN_PROFESOR_DNI + " INTEGER);";

    private static final String CREATE_TABLE_CURSO =
            "CREATE TABLE " + TABLE_CURSO + " (" +
                    COLUMN_CURSO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CURSO_NOMBRE + " TEXT NOT NULL, " +
                    COLUMN_CURSO_PROFESOR_ID + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_CURSO_PROFESOR_ID + ") REFERENCES " + TABLE_PROFESOR + "(" + COLUMN_PROFESOR_ID + "));";

    private static final String CREATE_TABLE_USUARIO =
            "CREATE TABLE " + TABLE_USUARIO + " (" +
                    COLUMN_USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USUARIO_NOMBRE + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_USUARIO_PASSWORD + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_PROFESOR);
        db.execSQL(CREATE_TABLE_CURSO);
        db.execSQL(CREATE_TABLE_USUARIO);

        // Insertar usuario administrador
        String insertAdminUser =
                "INSERT INTO " + TABLE_USUARIO + " (" + COLUMN_USUARIO_NOMBRE + ", " + COLUMN_USUARIO_PASSWORD + ") " +
                "VALUES ('ADMIN', 'ADMIN');";
        db.execSQL(insertAdminUser);

        // Insertar 3 profesores
        String insertProfesor1 =
                "INSERT INTO " + TABLE_PROFESOR + " (" + COLUMN_PROFESOR_NOMBRE + ", " + COLUMN_PROFESOR_EMAIL + ", " + COLUMN_PROFESOR_TELEFONO + ", " + COLUMN_PROFESOR_DNI + ") " +
                        "VALUES ('Luciana Gimenez', 'lgimenez@yogastudio.com', '123456789', '34569874');";
        db.execSQL(insertProfesor1);

        String insertProfesor2 =
                "INSERT INTO " + TABLE_PROFESOR + " (" + COLUMN_PROFESOR_NOMBRE + ", " + COLUMN_PROFESOR_EMAIL + ", " + COLUMN_PROFESOR_TELEFONO + ", " + COLUMN_PROFESOR_DNI + ") " +
                        "VALUES ('Cristina Lopez', 'clopez@yogastudio.com', '987654321', '8765241');";
        db.execSQL(insertProfesor2);

        String insertProfesor3 =
                "INSERT INTO " + TABLE_PROFESOR + " (" + COLUMN_PROFESOR_NOMBRE + ", " + COLUMN_PROFESOR_EMAIL + ", " + COLUMN_PROFESOR_TELEFONO + ", " + COLUMN_PROFESOR_DNI + ") " +
                        "VALUES ('Paula Kross', 'pkross@yoga.com', '456789123', '30548763');";
        db.execSQL(insertProfesor3);

        // Insertar 3 cursos
        String insertCurso1 =
                "INSERT INTO " + TABLE_CURSO + " (" + COLUMN_CURSO_NOMBRE + ", " + COLUMN_CURSO_PROFESOR_ID + ") " +
                        "VALUES ('Yoga principiantes 1', 1);";  // Asumimos que el Profesor 1 tiene ID 1
        db.execSQL(insertCurso1);

        String insertCurso2 =
                "INSERT INTO " + TABLE_CURSO + " (" + COLUMN_CURSO_NOMBRE + ", " + COLUMN_CURSO_PROFESOR_ID + ") " +
                        "VALUES ('Yoga avanzado 2', 2);";  // Asumimos que el Profesor 2 tiene ID 2
        db.execSQL(insertCurso2);

        String insertCurso3 =
                "INSERT INTO " + TABLE_CURSO + " (" + COLUMN_CURSO_NOMBRE + ", " + COLUMN_CURSO_PROFESOR_ID + ") " +
                        "VALUES ('Yoga terapia 1', 3);";  // Asumimos que el Profesor 2 tiene ID 2
        db.execSQL(insertCurso3);
    }

    // Actualizar la base de datos si hay cambios en la estructura
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFESOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURSO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }


}
