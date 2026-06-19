package pe.edu.upc.follmobileapp.features.communication.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.communication.domain.models.CareRequest

interface CommunicationRepository {
    fun getReceivedRequestsFlow(): Flow<List<CareRequest>>
    fun getSentRequestsFlow(): Flow<List<CareRequest>>
    suspend fun syncReceivedRequests(): Result<Unit>
    suspend fun syncSentRequests(): Result<Unit>
    suspend fun sendInvitation(dni: String, relationshipTypeId: Int): Result<Unit>
    suspend fun acceptInvitation(id: Long): Result<Unit>
    suspend fun rejectInvitation(id: Long): Result<Unit>
}
