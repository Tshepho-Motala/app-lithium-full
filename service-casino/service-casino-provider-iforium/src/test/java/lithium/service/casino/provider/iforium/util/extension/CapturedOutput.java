package lithium.service.casino.provider.iforium.util.extension;

/*
 using copy of SpringBoot implementation to prevent importing whole spring-boot-test dependency
 which can cause issue with our existing tests as this class exists on next major release only
 https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-test/src/main/java/org/springframework/boot/test/system/CapturedOutput.java
 */
public interface CapturedOutput extends CharSequence {

    @Override
    default int length() {
        return toString().length();
    }

    @Override
    default char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    default CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    String getAll();

    String getOut();

    String getErr();

}
