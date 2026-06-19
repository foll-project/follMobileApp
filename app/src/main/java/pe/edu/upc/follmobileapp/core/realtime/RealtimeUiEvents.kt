package pe.edu.upc.follmobileapp.core.realtime

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Bus de eventos efímeros de tiempo real para la UI.
 *
 * El servicio de SignalR (que vive fuera del árbol de Compose) emite aquí eventos
 * puntuales que merecen mostrarse como un aviso global (banner/toast) sin importar
 * en qué vista esté el usuario. La capa de UI (MainActivity) los colecciona.
 */
object RealtimeUiEvents {
    private val _incidentResolved = MutableSharedFlow<IncidentResolvedUiEvent>(extraBufferCapacity = 16)
    val incidentResolved: SharedFlow<IncidentResolvedUiEvent> = _incidentResolved.asSharedFlow()

    fun emitIncidentResolved(event: IncidentResolvedUiEvent) {
        _incidentResolved.tryEmit(event)
    }
}

/**
 * Aviso en vivo de que una caída fue atendida/cerrada.
 *
 * @param byMe true si fui yo quien la atendió (para no auto-notificarme con banner).
 * @param isFalseAlarm true si se cerró como falsa alarma / cancelada por el sistema.
 */
data class IncidentResolvedUiEvent(
    val patientName: String,
    val resolvedByName: String,
    val byMe: Boolean,
    val isFalseAlarm: Boolean
)
