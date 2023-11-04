package io.zkz.mc.gametools.util.observable

abstract class AbstractObservable<T : IObservable<T>> : IObservable<T> {
    override val listeners = mutableListOf<IObserver<T>>()

    override fun addListener(observer: IObserver<T>) {
        listeners.add(observer)
    }

    override fun removeListener(observer: IObserver<T>) {
        listeners.remove(observer)
    }
}
