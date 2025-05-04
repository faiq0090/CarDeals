package com.example.smdproject

import Car
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "UserDatabase"
        private const val TABLE_USERS = "users"
        private const val TABLE_CARS = "cars"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_PHONE = "phone"

        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_CITY = "city"
        private const val KEY_MODEL = "model"
        private const val KEY_REGISTERED = "registered"
        private const val KEY_COLOR = "color"
        private const val KEY_KM = "km"
        private const val KEY_FUEL_TYPE = "fuelType"
        private const val KEY_PRICE = "price"
        private const val KEY_DESC = "desc"
        private const val KEY_CAR_COMPANY = "carCompany"
        private const val KEY_CAR_MODEL = "carModel"
        private const val KEY_TRANSMISSION_TYPE = "transmissionType"
        private const val KEY_BODY_TYPE = "bodyType"
        private const val KEY_ADDRESS = "address"
        private const val KEY_ENGINE_CAPACITY = "engineCapacity"
        private const val KEY_USER_ID = "userId"
        private const val KEY_IMAGE_URL = "imageUrl"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTableQuery = ("CREATE TABLE $TABLE_USERS ("
                + "$KEY_ID INTEGER PRIMARY KEY,"
                + "$KEY_NAME TEXT,"
                + "$KEY_PHONE TEXT,"
                + "$KEY_EMAIL TEXT UNIQUE,"
                + "$KEY_PASSWORD TEXT" + ")")
        db.execSQL(createUserTableQuery)

        val createCarsTableQuery = ("CREATE TABLE $TABLE_CARS ("
                + "$KEY_ID INTEGER PRIMARY KEY,"
                + "$KEY_CITY TEXT,"
                + "$KEY_MODEL TEXT,"
                + "$KEY_REGISTERED TEXT,"
                + "$KEY_COLOR TEXT,"
                + "$KEY_KM TEXT,"
                + "$KEY_FUEL_TYPE TEXT,"
                + "$KEY_PRICE TEXT,"
                + "$KEY_DESC TEXT,"
                + "$KEY_CAR_COMPANY TEXT,"
                + "$KEY_CAR_MODEL TEXT,"
                + "$KEY_TRANSMISSION_TYPE TEXT,"
                + "$KEY_BODY_TYPE TEXT,"
                + "$KEY_ADDRESS TEXT,"
                + "$KEY_ENGINE_CAPACITY TEXT,"
                + "$KEY_USER_ID TEXT,"
                + "$KEY_IMAGE_URL TEXT" + ")")
        db.execSQL(createCarsTableQuery)

        Log.d("DatabaseHelper", "Tables created successfully") // Add this line
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CARS")
        onCreate(db)
    }

    // Add your database functions here



    // Add your database functions here

    fun addUser(name: String, phone: String, email: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, name)
        values.put(KEY_PHONE, phone)
        values.put(KEY_EMAIL, email)
        values.put(KEY_PASSWORD, password)
        val id = db.insert(TABLE_USERS, null, values)
        db.close()
        return id
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $KEY_EMAIL=? AND $KEY_PASSWORD=?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(email, password))
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

    fun addCar(car: Car): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.apply {
            put(KEY_CITY, car.city)
            put(KEY_MODEL, car.model)
            put(KEY_REGISTERED, car.registered)
            put(KEY_COLOR, car.color)
            put(KEY_KM, car.km)
            put(KEY_FUEL_TYPE, car.fuelType)
            put(KEY_PRICE, car.price)
            put(KEY_DESC, car.desc)
            put(KEY_CAR_COMPANY, car.carCompany)
            put(KEY_CAR_MODEL, car.carModel)
            put(KEY_TRANSMISSION_TYPE, car.transmissionType)
            put(KEY_BODY_TYPE, car.bodyType)
            put(KEY_ADDRESS, car.address)
            put(KEY_ENGINE_CAPACITY, car.engineCapacity)
            put(KEY_USER_ID, car.userId)
            put(KEY_IMAGE_URL, car.imageUrl)
        }
        val id = db.insert(TABLE_CARS, null, values)
        db.close()
        return id
    }

    fun getCars(): MutableList<Car> {
        val carList: MutableList<Car> = mutableListOf()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_CARS"
        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val cityIndex = cursor.getColumnIndex(KEY_CITY)
                val modelIndex = cursor.getColumnIndex(KEY_MODEL)
                val registeredIndex = cursor.getColumnIndex(KEY_REGISTERED)
                val colorIndex = cursor.getColumnIndex(KEY_COLOR)
                val kmIndex = cursor.getColumnIndex(KEY_KM)
                val fuelTypeIndex = cursor.getColumnIndex(KEY_FUEL_TYPE)
                val priceIndex = cursor.getColumnIndex(KEY_PRICE)
                val descIndex = cursor.getColumnIndex(KEY_DESC)
                val carCompanyIndex = cursor.getColumnIndex(KEY_CAR_COMPANY)
                val carModelIndex = cursor.getColumnIndex(KEY_CAR_MODEL)
                val transmissionTypeIndex = cursor.getColumnIndex(KEY_TRANSMISSION_TYPE)
                val bodyTypeIndex = cursor.getColumnIndex(KEY_BODY_TYPE)
                val addressIndex = cursor.getColumnIndex(KEY_ADDRESS)
                val engineCapacityIndex = cursor.getColumnIndex(KEY_ENGINE_CAPACITY)
                val userIdIndex = cursor.getColumnIndex(KEY_USER_ID)
                val imageUrlIndex = cursor.getColumnIndex(KEY_IMAGE_URL)

                if (cityIndex != -1 && modelIndex != -1 && registeredIndex != -1 &&
                    colorIndex != -1 && kmIndex != -1 && fuelTypeIndex != -1 &&
                    priceIndex != -1 && descIndex != -1 && carCompanyIndex != -1 &&
                    carModelIndex != -1 && transmissionTypeIndex != -1 &&
                    bodyTypeIndex != -1 && addressIndex != -1 &&
                    engineCapacityIndex != -1 && userIdIndex != -1 && imageUrlIndex != -1) {

                    val car = Car(
                        city = cursor.getString(cityIndex),
                        model = cursor.getString(modelIndex),
                        registered = cursor.getString(registeredIndex),
                        color = cursor.getString(colorIndex),
                        km = cursor.getString(kmIndex),
                        fuelType = cursor.getString(fuelTypeIndex),
                        price = cursor.getString(priceIndex),
                        desc = cursor.getString(descIndex),
                        carCompany = cursor.getString(carCompanyIndex),
                        carModel = cursor.getString(carModelIndex),
                        transmissionType = cursor.getString(transmissionTypeIndex),
                        bodyType = cursor.getString(bodyTypeIndex),
                        address = cursor.getString(addressIndex),
                        engineCapacity = cursor.getString(engineCapacityIndex),
                        userId = cursor.getString(userIdIndex),
                        imageUrl = cursor.getString(imageUrlIndex)
                    )
                    carList.add(car)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return carList
    }
}
