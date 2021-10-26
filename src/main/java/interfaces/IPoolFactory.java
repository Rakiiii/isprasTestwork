package interfaces;

/**
 * Интерфейс фабрики {@code IPool}
 */
public interface IPoolFactory {
    /**
     * Создает объект типа {@code IPool}
     *
     * @return новый объект типа {@code IPool}
     */
    IPool create();
}
