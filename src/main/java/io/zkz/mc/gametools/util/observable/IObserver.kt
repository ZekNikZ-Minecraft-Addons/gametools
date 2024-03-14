package io.zkz.mc.gametools.util.observable

fun interface IObserver<T : IObservable<T>> {
    fun handleChanged(observable: T)
}
