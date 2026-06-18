package pe.edu.upc.follmobileapp.core.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pe.edu.upc.follmobileapp.features.care.domain.models.Caregiver
import pe.edu.upc.follmobileapp.features.care.domain.models.Annotation
import pe.edu.upc.follmobileapp.features.care.domain.models.DeviceInfo
import pe.edu.upc.follmobileapp.features.care.domain.models.EmergencyContact

class FollConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromEmergencyContactList(value: List<EmergencyContact>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toEmergencyContactList(value: String): List<EmergencyContact> {
        val listType = object : TypeToken<List<EmergencyContact>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromCaregiverList(value: List<Caregiver>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCaregiverList(value: String): List<Caregiver> {
        val listType = object : TypeToken<List<Caregiver>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromAnnotationList(value: List<Annotation>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAnnotationList(value: String): List<Annotation> {
        val listType = object : TypeToken<List<Annotation>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromDeviceInfo(value: DeviceInfo?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toDeviceInfo(value: String?): DeviceInfo? {
        return value?.let { gson.fromJson(it, DeviceInfo::class.java) }
    }
}
