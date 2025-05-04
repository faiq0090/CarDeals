import android.os.Parcel
import android.os.Parcelable

class Car() : Parcelable {
    var city: String? = null
    var model: String? = null
    var registered: String? = null
    var color: String? = null
    var km: String? = null
    var fuelType: String? = null
    var price: String? = null
    var desc: String? = null
    var carCompany: String? = null
    var carModel: String? = null
    var transmissionType: String? = null
    var bodyType: String? = null
    var address: String? = null
    var engineCapacity: String? = null
    var userId: String? = null
    var imageUrl: String? = null // Add image URL

    constructor(
        city: String?,
        model: String?,
        registered: String?,
        color: String?,
        km: String?,
        fuelType: String?,
        price: String?,
        desc: String?,
        carCompany: String?,
        carModel: String?,
        transmissionType: String?,
        bodyType: String?,
        address: String?,
        engineCapacity: String?,
        userId: String?,
        imageUrl: String? // Add image URL
    ) : this() {
        this.city = city
        this.model = model
        this.registered = registered
        this.color = color
        this.km = km
        this.fuelType = fuelType
        this.price = price
        this.desc = desc
        this.carCompany = carCompany
        this.carModel = carModel
        this.transmissionType = transmissionType
        this.bodyType = bodyType
        this.address = address
        this.engineCapacity = engineCapacity
        this.userId = userId
        this.imageUrl = imageUrl // Add image URL
    }

    constructor(parcel: Parcel) : this() {
        city = parcel.readString()
        model = parcel.readString()
        registered = parcel.readString()
        color = parcel.readString()
        km = parcel.readString()
        fuelType = parcel.readString()
        price = parcel.readString()
        desc = parcel.readString()
        carCompany = parcel.readString()
        carModel = parcel.readString()
        transmissionType = parcel.readString()
        bodyType = parcel.readString()
        address = parcel.readString()
        engineCapacity = parcel.readString()
        userId = parcel.readString()
        imageUrl = parcel.readString() // Add image URL
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(city)
        parcel.writeString(model)
        parcel.writeString(registered)
        parcel.writeString(color)
        parcel.writeString(km)
        parcel.writeString(fuelType)
        parcel.writeString(price)
        parcel.writeString(desc)
        parcel.writeString(carCompany)
        parcel.writeString(carModel)
        parcel.writeString(transmissionType)
        parcel.writeString(bodyType)
        parcel.writeString(address)
        parcel.writeString(engineCapacity)
        parcel.writeString(userId)
        parcel.writeString(imageUrl) // Add image URL
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Car(city=$city, model=$model, registered=$registered, color=$color, km=$km, fuelType=$fuelType, price=$price, desc=$desc, carCompany=$carCompany, carModel=$carModel, transmissionType=$transmissionType, bodyType=$bodyType, address=$address, engineCapacity=$engineCapacity, userId=$userId, imageUrl=$imageUrl)"
    }

    companion object CREATOR : Parcelable.Creator<Car> {
        override fun createFromParcel(parcel: Parcel): Car {
            return Car(parcel)
        }

        override fun newArray(size: Int): Array<Car?> {
            return arrayOfNulls(size)
        }
    }
}
