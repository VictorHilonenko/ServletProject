package beauty.scheduler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ExtendedException extends Exception {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedException.class);

    private static final long serialVersionUID = 1L;

    private ExceptionKind anException;
    private Optional<Exception> originalException;

    public ExtendedException(ExceptionKind anException) {
        this.anException = anException;
        this.originalException = Optional.empty();
    }

    public ExtendedException(ExceptionKind anError, Exception originalException) {
        this.anException = anError;
        this.originalException = Optional.of(originalException);
    }
}