package com.example.smdproject.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CarDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "CarAds.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE car_ads (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                car_type TEXT,
                car_model TEXT,
                city TEXT,
                model TEXT,
                registered TEXT,
                color TEXT,
                fuel_type TEXT,
                body_type TEXT,
                transmission_type TEXT,
                engine_capacity TEXT,
                km_driven TEXT,
                price TEXT,
                description TEXT,
                address TEXT,
                image_base64 TEXT
            );
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS car_ads")
        onCreate(db)
    }

    fun insertCarAd(values: ContentValues): Boolean {
        val db = writableDatabase
        val result = db.insert("car_ads", null, values)
        db.close()
        return result != -1L
    }

    // Helper to convert Cursor row to ContentValues
    private fun cursorToContentValues(cursor: android.database.Cursor): ContentValues {
        val values = ContentValues()
        for (i in 0 until cursor.columnCount) {
            values.put(cursor.getColumnName(i), cursor.getString(i))
        }
        return values
    }

    // Generic helper to filter by a single column
    private fun getCarsByCondition(column: String, value: String): List<ContentValues> {
        val db = readableDatabase
        val carList = mutableListOf<ContentValues>()
        val cursor = db.query("car_ads", null, "$column = ?", arrayOf(value), null, null, null)

        while (cursor.moveToNext()) {
            carList.add(cursorToContentValues(cursor))
        }

        cursor.close()
        db.close()
        return carList
    }

    // Get all cars (no filter)
    fun getAllCars(): List<ContentValues> {
        val db = readableDatabase
        val carList = mutableListOf<ContentValues>()
        val cursor = db.rawQuery("SELECT * FROM car_ads", null)

        while (cursor.moveToNext()) {
            carList.add(cursorToContentValues(cursor))
        }

        cursor.close()
        db.close()
        return carList
    }

    // FILTER FUNCTIONS

    fun getAutomaticCars(): List<ContentValues> {
        return getCarsByCondition("transmission_type", "Automatic")
    }

    fun getManualCars(): List<ContentValues> {
        return getCarsByCondition("transmission_type", "Manual")
    }

    fun getImportedCars(): List<ContentValues> {
        return getCarsByCondition("registered", "No")
    }

    fun getJapaneseCars(): List<ContentValues> {
        return getCarsByCondition("car_model", "Honda") +
                getCarsByCondition("car_model", "Suzuki") +
                getCarsByCondition("car_model", "Toyota")
    }

    fun get660ccCars(): List<ContentValues> {
        return getCarsByCondition("engine_capacity", "660")
    }

    fun get1300ccCars(): List<ContentValues> {
        return getCarsByCondition("engine_capacity", "1300")
    }

    fun getSportsCars(): List<ContentValues> {
        return getCarsByCondition("car_type", "Sports")
    }

    fun getCarsByCity(cityName: String): List<ContentValues> {
        return getCarsByCondition("city", cityName)
    }

    fun getCarsByPriceRange(minPrice: Int, maxPrice: Int): List<ContentValues> {
        val db = readableDatabase
        val carList = mutableListOf<ContentValues>()
        val cursor = db.query(
            "car_ads", null,
            "CAST(price AS INTEGER) BETWEEN ? AND ?",
            arrayOf(minPrice.toString(), maxPrice.toString()),
            null, null, null
        )

        while (cursor.moveToNext()) {
            carList.add(cursorToContentValues(cursor))
        }

        cursor.close()
        db.close()
        return carList
    }
}
