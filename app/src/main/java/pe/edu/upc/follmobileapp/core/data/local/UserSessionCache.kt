package pe.edu.upc.follmobileapp.core.data.local

import pe.edu.upc.follmobileapp.features.care.data.local.dao.PatientDao
import pe.edu.upc.follmobileapp.features.communication.data.local.dao.CareRequestDao
import pe.edu.upc.follmobileapp.features.emergency.data.local.dao.FallEventDao

/**
 * Caché local ligada a la sesión del usuario (Room).
 *
 * Al cerrar sesión o iniciar con otra cuenta hay que vaciar estas tablas;
 * de lo contrario la UI sigue leyendo abuelitos, alertas e invitaciones del usuario anterior.
 */
class UserSessionCache(
    private val patientDao: PatientDao,
    private val fallEventDao: FallEventDao,
    private val careRequestDao: CareRequestDao
) {
    suspend fun clearAll() {
        patientDao.clearPatients()
        fallEventDao.clearAll()
        careRequestDao.clearAllCareRequests()
    }
}
