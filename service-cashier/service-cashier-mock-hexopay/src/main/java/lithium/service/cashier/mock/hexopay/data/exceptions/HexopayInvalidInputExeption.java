package lithium.service.cashier.mock.hexopay.data.exceptions;

public class HexopayInvalidInputExeption extends HexopayGatewayMockException {
    public HexopayInvalidInputExeption(String message) {
        super(400, message);
    }
}
