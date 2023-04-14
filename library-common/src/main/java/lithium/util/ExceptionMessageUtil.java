package lithium.util;

public class ExceptionMessageUtil {

    private final static int MAX_DEPTH = 10;

    public static String allMessages(Throwable e) {
        String message = "";
        Throwable current = e;
        int depth = 1;
        while (current != null) {
            if (message.length() > 0) message += " - ";
            message += current.getMessage();
            Throwable cause = e.getCause();
            depth ++;
            if (cause == current) return message;
            if (depth == MAX_DEPTH) return message;
            current = cause;
        }
        return message;
    }
}
