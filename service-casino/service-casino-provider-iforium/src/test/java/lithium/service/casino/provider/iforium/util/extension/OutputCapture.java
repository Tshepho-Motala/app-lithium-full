package lithium.service.casino.provider.iforium.util.extension;

import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/*
 using copy of SpringBoot implementation to prevent importing whole spring-boot-test dependency
 which can cause issue with our existing tests as this class exists on next major release only
 https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-test/src/main/java/org/springframework/boot/test/system/OutputCapture.java
 */
class OutputCapture implements CapturedOutput {

    private final Deque<SystemCapture> systemCaptures = new ArrayDeque<>();

    private AnsiOutputState ansiOutputState;

    final void push() {
        if (this.systemCaptures.isEmpty()) {
            this.ansiOutputState = AnsiOutputState.saveAndDisable();
        }
        this.systemCaptures.addLast(new SystemCapture());
    }

    final void pop() {
        this.systemCaptures.removeLast().release();
        if (this.systemCaptures.isEmpty() && this.ansiOutputState != null) {
            this.ansiOutputState.restore();
            this.ansiOutputState = null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CapturedOutput || obj instanceof CharSequence) {
            return getAll().equals(obj.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return getAll();
    }

    @Override
    public String getAll() {
        return get((type) -> true);
    }

    @Override
    public String getOut() {
        return get(Type.OUT::equals);
    }

    @Override
    public String getErr() {
        return get(Type.ERR::equals);
    }

    void reset() {
        this.systemCaptures.peek().reset();
    }

    private String get(Predicate<Type> filter) {
        Assert.state(!this.systemCaptures.isEmpty(),
                     "No system captures found. Please check your output capture registration.");
        StringBuilder builder = new StringBuilder();
        for (SystemCapture systemCapture : this.systemCaptures) {
            systemCapture.append(builder, filter);
        }
        return builder.toString();
    }

    private static class SystemCapture {

        private final Object monitor = new Object();

        private final PrintStreamCapture out;

        private final PrintStreamCapture err;

        private final List<CapturedString> capturedStrings = new ArrayList<>();

        SystemCapture() {
            this.out = new PrintStreamCapture(System.out, this::captureOut);
            this.err = new PrintStreamCapture(System.err, this::captureErr);
            System.setOut(this.out);
            System.setErr(this.err);
        }

        void release() {
            System.setOut(this.out.getParent());
            System.setErr(this.err.getParent());
        }

        private void captureOut(String string) {
            synchronized (this.monitor) {
                this.capturedStrings.add(new CapturedString(Type.OUT, string));
            }
        }

        private void captureErr(String string) {
            synchronized (this.monitor) {
                this.capturedStrings.add(new CapturedString(Type.ERR, string));
            }
        }

        void append(StringBuilder builder, Predicate<Type> filter) {
            synchronized (this.monitor) {
                for (CapturedString stringCapture : this.capturedStrings) {
                    if (filter.test(stringCapture.getType())) {
                        builder.append(stringCapture);
                    }
                }
            }
        }

        void reset() {
            synchronized (this.monitor) {
                this.capturedStrings.clear();
            }
        }

    }

    private static class PrintStreamCapture extends PrintStream {

        private final PrintStream parent;

        PrintStreamCapture(PrintStream parent, Consumer<String> copy) {
            super(new OutputStreamCapture(getSystemStream(parent), copy));
            this.parent = parent;
        }

        PrintStream getParent() {
            return this.parent;
        }

        private static PrintStream getSystemStream(PrintStream printStream) {
            while (printStream instanceof PrintStreamCapture) {
                printStream = ((PrintStreamCapture) printStream).getParent();
            }
            return printStream;
        }

    }

    private static class OutputStreamCapture extends OutputStream {

        private final PrintStream systemStream;

        private final Consumer<String> copy;

        OutputStreamCapture(PrintStream systemStream, Consumer<String> copy) {
            this.systemStream = systemStream;
            this.copy = copy;
        }

        @Override
        public void write(int b) throws IOException {
            write(new byte[]{(byte) (b & 0xFF)});
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.copy.accept(new String(b, off, len));
            this.systemStream.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            this.systemStream.flush();
        }

    }

    private static class CapturedString {

        private final Type type;

        private final String string;

        CapturedString(Type type, String string) {
            this.type = type;
            this.string = string;
        }

        Type getType() {
            return this.type;
        }

        @Override
        public String toString() {
            return this.string;
        }

    }

    private enum Type {

        OUT, ERR

    }

    private static class AnsiOutputState {

        private AnsiOutput.Enabled saved;

        AnsiOutputState() {
            this.saved = AnsiOutput.Enabled.NEVER;
            AnsiOutput.setEnabled(AnsiOutput.Enabled.NEVER);
        }

        void restore() {
            AnsiOutput.setEnabled(this.saved);
        }

        static AnsiOutputState saveAndDisable() {
            if (!ClassUtils.isPresent("org.springframework.boot.ansi.AnsiOutput",
                                      OutputCapture.class.getClassLoader())) {
                return null;
            }
            return new AnsiOutputState();
        }

    }

}
