package lithium.service.cashier.mock.hexopay.data.exceptions;

public class HexopayInvalidInputPageExeption extends HexopayPageMockException {
    public HexopayInvalidInputPageExeption(String message) {
        super(400, message);
    }
}
