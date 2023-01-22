package diclojurej;

public class SerialNumberManager {
    public Long serialNumber;

    public Long getSerialNumber() {
        return serialNumber++;
    }
}
