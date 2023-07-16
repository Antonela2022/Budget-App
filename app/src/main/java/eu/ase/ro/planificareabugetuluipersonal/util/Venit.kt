package eu.ase.ro.planificareabugetuluipersonal.util

import android.os.Parcel
import android.os.Parcelable

data class Venit(
    val zi: String,
    val denumire: String,
    val suma: Double,
    val tipVenit: Boolean
)

