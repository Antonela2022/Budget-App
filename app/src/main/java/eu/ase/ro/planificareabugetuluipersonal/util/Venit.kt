package eu.ase.ro.planificareabugetuluipersonal.util

import android.os.Parcel
import android.os.Parcelable

data class Venit(
    val zi: String,
    val denumire: String,
    val suma: Double,
    val tipVenit: Boolean
) : Parcelable {
    // Implementați metodele necesare pentru a face clasa Venit Parcelable
    // ...

    // Implementați metoda writeToParcel() pentru a scrie obiectul în Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(zi)
        parcel.writeString(denumire)
        parcel.writeDouble(suma)
        parcel.writeByte(if (tipVenit) 1 else 0)
    }

    // Implementați constructorul special pentru a citi obiectul din Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readByte() != 0.toByte()
    )

    // Implementați metoda describeContents()
    override fun describeContents(): Int {
        return 0
    }

    // Adăugați companion object pentru a crea instanțe Parcelable
    companion object CREATOR : Parcelable.Creator<Venit> {
        override fun createFromParcel(parcel: Parcel): Venit {
            return Venit(parcel)
        }

        override fun newArray(size: Int): Array<Venit?> {
            return arrayOfNulls(size)
        }
    }
}
