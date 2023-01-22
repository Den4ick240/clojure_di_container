package diclojurej;

public class Car {
    public final Long serialNumber;
    public final String paint;

    public Car(Long serialNumber, String paint) {
        this.serialNumber = serialNumber;
        this.paint = paint;
    }

    @Override
    public String toString() {
        return "Car{" +
                "serialNumber=" + serialNumber +
                ", paint=" + paint +
                '}';
    }
}
