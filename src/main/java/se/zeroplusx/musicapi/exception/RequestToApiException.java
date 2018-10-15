package se.zeroplusx.musicapi.exception;

/** common class for all api exceptions */
public class RequestToApiException extends RuntimeException {

    private String key;

    public RequestToApiException(String message) {
        super(message);
    }

    public RequestToApiException(String key, String param) {
        super(param);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getParam() {
        return getMessage();
    }
}
