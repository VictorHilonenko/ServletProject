package beauty.scheduler.util;

public enum ExceptionKind {
    PAGE_NOT_FOUND(404, "error.pageNotFound"),
    REPOSITORY_ISSUE(410, "error.someRepositoryIssueTryAgainLater"),
    WRONG_DATA_PASSED(406, "error.wrongDataPassed"),
    NO_IDLE_MASTER(409, "warning.noIdleMaster"),
    ACCESS_DENIED(403, "error.accessDenied"),
    NOT_ACCEPTABLE_DATA_PASSED(406, "error.notAcceptableDataPassed"),
    MAIL_ISSUE(410, "error.someMailIssueTryAgainLater"),
    USER_NOT_FOUND(404, "error.userNotFound"),
    WRONG_CONFIGURATION(410, "error.wrongConfiguration"),
    WRONG_METHOD_CALLED(410, "error.someRepositoryIssueTryAgainLater"),
    NOT_SUPPORTED(404, "error.notSupported"),
    NULL(0, ""); //this is used to make possible default value in annotation @Restriction

    private int statusCode;
    private String errorMessage;

    private ExceptionKind(int statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}