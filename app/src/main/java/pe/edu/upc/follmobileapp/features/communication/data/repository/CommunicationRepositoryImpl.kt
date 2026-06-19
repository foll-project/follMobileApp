package pe.edu.upc.follmobileapp.features.communication.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upc.follmobileapp.features.communication.data.local.CommunicationLocalDataSource
import pe.edu.upc.follmobileapp.features.communication.data.local.models.CareRequestEntity
import pe.edu.upc.follmobileapp.features.communication.data.remote.models.SendInvitationRequest
import pe.edu.upc.follmobileapp.features.communication.data.remote.services.InvitationService
import pe.edu.upc.follmobileapp.features.communication.domain.models.CareRequest
import pe.edu.upc.follmobileapp.features.communication.domain.repository.CommunicationRepository

object CareRequestMapper {
    fun toDomain(entity: CareRequestEntity): CareRequest {
        return CareRequest(
            invitationId = entity.invitationId,
            patientId = entity.patientId,
            patientFirstName = entity.patientFirstName,
            patientLastName = entity.patientLastName,
            patientDni = entity.patientDni,
            requesterUserId = entity.requesterUserId,
            requesterName = entity.requesterName,
            requesterEmail = entity.requesterEmail,
            relationshipTypeId = entity.relationshipTypeId,
            relationshipName = entity.relationshipName,
            status = entity.status,
            expiresAt = entity.expiresAt,
            type = entity.type
        )
    }

    fun toEntity(dto: pe.edu.upc.follmobileapp.features.communication.data.remote.models.InvitationResponseDto, type: String): CareRequestEntity {
        return CareRequestEntity(
            invitationId = dto.invitationId ?: 0L,
            patientId = dto.patientId ?: 0L,
            patientFirstName = dto.patientFirstName ?: "",
            patientLastName = dto.patientLastName ?: "",
            patientDni = dto.patientDni ?: "",
            requesterUserId = dto.requesterUserId ?: 0,
            requesterName = dto.requesterName ?: "",
            requesterEmail = dto.requesterEmail,
            relationshipTypeId = dto.relationshipTypeId ?: 4,
            relationshipName = dto.relationshipName ?: "Familiar",
            status = dto.status ?: "Pending",
            expiresAt = dto.expiresAt ?: "",
            type = type
        )
    }
}

class CommunicationRepositoryImpl(
    private val localDataSource: CommunicationLocalDataSource,
    private val invitationService: InvitationService
) : CommunicationRepository {

    override fun getReceivedRequestsFlow(): Flow<List<CareRequest>> {
        return localDataSource.getReceivedRequestsFlow().map { list ->
            list.map { CareRequestMapper.toDomain(it) }
        }
    }

    override fun getSentRequestsFlow(): Flow<List<CareRequest>> {
        return localDataSource.getSentRequestsFlow().map { list ->
            list.map { CareRequestMapper.toDomain(it) }
        }
    }

    override suspend fun syncReceivedRequests(): Result<Unit> = runCatching {
        val response = invitationService.getReceivedInvitations()
        val entities = response.map { CareRequestMapper.toEntity(it, "received") }
        localDataSource.deleteCareRequestsByType("received")
        localDataSource.saveCareRequests(entities)
    }

    override suspend fun syncSentRequests(): Result<Unit> = runCatching {
        val response = invitationService.getSentInvitations()
        val entities = response.map { CareRequestMapper.toEntity(it, "sent") }
        localDataSource.deleteCareRequestsByType("sent")
        localDataSource.saveCareRequests(entities)
    }

    override suspend fun sendInvitation(dni: String, relationshipTypeId: Int): Result<Unit> = runCatching {
        val request = SendInvitationRequest(relationshipTypeId)
        invitationService.sendInvitation(dni, request)
        syncSentRequests()
    }

    override suspend fun acceptInvitation(id: Long): Result<Unit> = runCatching {
        invitationService.acceptInvitation(id)
        localDataSource.updateStatus(id, "Accepted")
        syncReceivedRequests()
    }

    override suspend fun rejectInvitation(id: Long): Result<Unit> = runCatching {
        invitationService.rejectInvitation(id)
        localDataSource.updateStatus(id, "Rejected")
        syncReceivedRequests()
    }
}
