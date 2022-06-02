state类相关模板
data class MainViewState(): State() {
}

sealed class MainViewEvent:Event {
    object A : MainViewEvent()
}

sealed class MainViewAction:Action {
    object B : MainViewAction()
}