package com.immotor.albert.mvicore.base

import androidx.lifecycle.ViewModel
import com.immotor.albert.mvicore.SharedFlowEvents
import com.immotor.albert.mvicore.interfaces.Action
import com.immotor.albert.mvicore.interfaces.Event
import com.immotor.albert.mvicore.interfaces.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
object EmptyState:State

/**
 * 用于无意图，事件和状态
 */
abstract class BaseEmptyViewModel: BaseViewModel<Action, Event, State>(EmptyState) {
    override fun dispatch(viewAction: Action) {
    }
}
/**
 * 用于有意图，无事件和状态
 */
abstract class BaseActionViewModel<T : Action>: BaseViewModel<T,Event,State>(EmptyState) {
}
/**
 * 用于有意图和事件，无状态
 */
abstract class BaseActionEventViewModel<T : Action,E : Event>: BaseViewModel<T,E,State>(EmptyState) {
}

/**
 * 定义意图，事件和状态
 */
abstract class BaseViewModel<A : Action,E : Event, S : State>(state: S) : ViewModel() {
    protected val openViewStates = MutableStateFlow(state)
    val viewStates = openViewStates.asStateFlow()
    protected val openViewEvents = SharedFlowEvents<E>()
    val viewEvents = openViewEvents.asSharedFlow()

    /**
     * viewModel处理action的方法
     */
    abstract fun dispatch(viewAction: A)
}
