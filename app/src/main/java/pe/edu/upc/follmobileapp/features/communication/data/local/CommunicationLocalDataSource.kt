package pe.edu.upc.follmobileapp.features.communication.data.local

import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.communication.data.local.dao.CareRequestDao
import pe.edu.upc.follmobileapp.features.communication.data.local.models.CareRequestEntity

class CommunicationLocalDataSource(
    private val careRequestDao: CareRequestDao
) {
    fun getReceivedRequestsFlow(): Flow<List<CareRequestEntity>> =
        careRequestDao.getCareRequestsByTypeFlow("received")

    fun getSentRequestsFlow(): Flow<List<CareRequestEntity>> =
        careRequestDao.getCareRequestsByTypeFlow("sent")

    suspend fun saveCareRequests(requests: List<CareRequestEntity>) {
        careRequestDao.insertCareRequests(requests)
    }

    suspend fun deleteCareRequestsByType(type: String) {
        careRequestDao.deleteCareRequestsByType(type)
    }

    suspend fun deleteCareRequestById(id: Long) {
        careRequestDao.deleteCareRequestById(id)
    }

    suspend fun updateStatus(id: Long, status: String) {
        careRequestDao.updateStatus(id, status)
    }
}
