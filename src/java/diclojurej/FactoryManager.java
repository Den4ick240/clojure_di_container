package diclojurej;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class FactoryManager {
    public final OrderProvider orderProvider;
    public final CurrentOrder currentOrder;

    @Inject
    public SerialNumberManager serialNumberManager;

    @Inject
    public CarMaker carMaker;

    public FactoryManager(OrderProvider orderProvider, CurrentOrder currentOrder) {
        this.orderProvider = orderProvider;
        this.currentOrder = currentOrder;
    }

    @PostConstruct
    public void initSerialNumberManager() {
        serialNumberManager.serialNumber = 123L;
    }

    public void fulfillOrders() {
        orderProvider.getOrders().forEach(order -> {
            currentOrder.order = order;
            carMaker.fulfillOrder();
        });
    }
}
