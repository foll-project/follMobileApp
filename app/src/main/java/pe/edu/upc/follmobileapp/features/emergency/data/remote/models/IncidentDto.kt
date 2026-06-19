package pe.edu.upc.follmobileapp.features.emergency.data.remote.models

/** Incidente de emergencia (caída) activo devuelto por el backend. */
data class ActiveIncidentDto(
    val incidentId: Long = 0L,
    val patientId: Long = 0L,
    val status: String? = null
)

/** Cuerpo para cerrar/atender un incidente. */
data class ResolveIncidentRequest(
    val observation: String?
)
