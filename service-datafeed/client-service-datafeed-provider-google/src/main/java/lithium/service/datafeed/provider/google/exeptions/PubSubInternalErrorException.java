package lithium.service.datafeed.provider.google.exeptions;

public class PubSubInternalErrorException extends Exception{
    public PubSubInternalErrorException(String message){
        super("Cant Read Json file exception" + message);
    }
}
