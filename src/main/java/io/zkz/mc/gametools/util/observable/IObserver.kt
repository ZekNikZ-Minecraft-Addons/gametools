package io.zkz.mc.gametools.util.observable

interface IObserver<T : IObservable<T>> {
    fun handleChanged(observable: IObservable<T>)
}
