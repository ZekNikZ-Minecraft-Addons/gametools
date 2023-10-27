package io.zkz.mc.gametools.util

interface IObserver<T : IObservable<T>> {
    fun handleChanged(observable: IObservable<T>)
}