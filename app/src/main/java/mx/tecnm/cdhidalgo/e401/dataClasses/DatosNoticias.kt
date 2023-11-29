package mx.tecnm.cdhidalgo.e401.dataClasses

import android.os.Parcel
import android.os.Parcelable

data class DatosNoticias(

val imagen:String?,
    val titulo:String?,
    val descripcion:String?,
   var editable: Boolean = false
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    ){
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imagen)
        parcel.writeString(titulo)
        parcel.writeString(descripcion)
    }
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DatosNoticias> {
        override fun createFromParcel(parcel: Parcel): DatosNoticias {
            return DatosNoticias(parcel)
        }

        override fun newArray(size: Int): Array<DatosNoticias?> {
            return arrayOfNulls(size)
        }
    }
}
