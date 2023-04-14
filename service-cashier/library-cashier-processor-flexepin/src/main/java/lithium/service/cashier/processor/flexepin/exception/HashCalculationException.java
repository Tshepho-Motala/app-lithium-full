package lithium.service.cashier.processor.flexepin.exception;

public class HashCalculationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HashCalculationException(String errorMessage) {
        super(errorMessage);
    }
	public HashCalculationException(String errorMessage, Exception e) {
        super(errorMessage ,e);
    }
}
