package beauty.scheduler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ExtendedException extends Exception {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedException.class);

    private static final long serialVersionUID = 1L;

    private ExceptionKind exceptionKind;
    private Optional<Exception> originalException;

    public ExtendedException(ExceptionKind exceptionKind) {
        this.exceptionKind = exceptionKind;
        this.originalException = Optional.empty();
    }

    public ExtendedException(ExceptionKind exceptionKind, Exception originalException) {
        this.exceptionKind = exceptionKind;
        this.originalException = Optional.of(originalException);
    }

    public ExceptionKind getExceptionKind() {
        return exceptionKind;
    }
}