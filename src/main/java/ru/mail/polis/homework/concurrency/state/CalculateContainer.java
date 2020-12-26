package ru.mail.polis.homework.concurrency.state;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Потокобезопасный контейнер для вычислений. Контейнер создается с некторым дэфолтным значеним.
 * Далее значение инициализируется, вычисляется и отдается к потребителю. В каждом методе контейнер меняет состояние
 * и делает некоторое вычисление (которое передано ему в определенный метод)
 * <p>
 * Последовательность переходов из состояния в состояние строго определена:
 * START -> INIT -> RUN -> FINISH
 * Из состояния FINISH можно перейти или в состояние INIT или в состояние CLOSE.
 * CLOSE - конченое состояние.
 * <p>
 * Если какой-либо метод вызывается после перехода в состояние CLOSE
 * он должен написать ошибку (НЕ бросить) и сразу выйти.
 * Если вызван метод, который не соответствует текущему состоянию - он ждет,
 * пока состояние не станет подходящим для него (или ждет состояние CLOSE, чтобы написать ошибку и выйти)
 * <p>
 * <p>
 * Есть три варианта решения этой задачи.
 * 1) через методы wait and notify - 5 баллов
 * 2) через Lock and Condition - 5 баллов
 * 3) через операцию compareAndSet на Atomic классах - 9 баллов
 * Баллы за методы не суммируются, берется наибольший балл из всех методов. (то есть, если вы сделали 1 метод на 4 балла,
 * и 3 метод на 3 балла, я поставлю баллы только 4 балла)
 * <p>
 * Max 8 баллов
 */
@SuppressWarnings(value = "unchecked")
public class CalculateContainer<T> {
    
    private final AtomicReference<Object[]> resWithState;
    private static final String ERROR_MSG = "ERROR: Container already closed";
    private final int id;
    
    public CalculateContainer(int id, T result) {
        this.id = id;
        this.resWithState = new AtomicReference<>(new Object[]{State.START, result});
    }
    
    /**
     * Инициализирует результат и переводит контейнер в состояние INIT (Возможно только из состояния START и FINISH)
     */
    public void init(UnaryOperator<T> initOperator) {
        Object[] oldResult;
        Object[] newResult;
        do {
            oldResult = resWithState.get();
            newResult = new Object[]{State.INIT, initOperator.apply((T) resWithState.get()[1])};
            if (resWithState.get()[0].equals(State.CLOSE)) {
                System.out.println(ERROR_MSG + " " + id);
                return;
            }
        } while (!oldResult[0].equals(State.START) && !oldResult[0].equals(State.FINISH)
                || !resWithState.compareAndSet(oldResult, newResult));
    }
    
    /**
     * Вычисляет результат и переводит контейнер в состояние RUN (Возможно только из состояния INIT)
     */
    public void run(BinaryOperator<T> runOperator, T value) {
        Object[] oldResult;
        Object[] newResult;
        do {
            oldResult = resWithState.get();
            newResult = new Object[]{State.RUN, runOperator.apply((T) resWithState.get()[1], value)};
            if (resWithState.get()[0].equals(State.CLOSE)) {
                System.out.println(ERROR_MSG + " " + id);
                return;
            }
        } while (!oldResult[0].equals(State.INIT)
                || !resWithState.compareAndSet(oldResult, newResult));
    }
    
    
    /**
     * Передает результат потребителю и переводит контейнер в состояние FINISH (Возможно только из состояния RUN)
     */
    public void finish(Consumer<T> finishConsumer) {
        Object[] oldResult;
        Object[] newResult;
        do {
            oldResult = resWithState.get();
            newResult = new Object[]{State.FINISH, oldResult[1]};
            if (resWithState.get()[0].equals(State.CLOSE)) {
                System.out.println(ERROR_MSG + " " + id);
                return;
            }
        } while (!oldResult[0].equals(State.RUN)
                || !resWithState.compareAndSet(oldResult, newResult));
        System.out.print(id + " ");
        finishConsumer.accept((T) oldResult[1]);
    }
    
    
    /**
     * Закрывает контейнер и передает результат потребителю. Переводит контейнер в состояние CLOSE
     * (Возможно только из состояния FINISH)
     */
    public void close(Consumer<T> closeConsumer) {
        Object[] oldResult;
        Object[] newResult;
        do {
            oldResult = resWithState.get();
            newResult = new Object[]{State.CLOSE, oldResult[1]};
            if (resWithState.get()[0].equals(State.CLOSE)) {
                System.out.println(ERROR_MSG + " " + id);
                return;
            }
        } while (!oldResult[0].equals(State.FINISH)
                || !resWithState.compareAndSet(oldResult, newResult));
        System.out.print(id + " ");
        closeConsumer.accept((T) oldResult[1]);
    }
    
    public T getResult() {
        return (T) resWithState.get()[1];
    }
    
    public State getState() {
        return (State) resWithState.get()[0];
    }
}
