package good.space.runnershi.shared.architecture

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class ViewModel<State : Any, Event : Any> {
    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<State> = _state.asStateFlow()

    protected val currentState: State
        get() = _state.value

    protected fun updateState(update: State.() -> State) {
        _state.value = _state.value.update()
    }

    protected fun setState(newState: State) {
        _state.value = newState
    }

    abstract fun initialState(): State
    abstract fun handleEvent(event: Event)
}
