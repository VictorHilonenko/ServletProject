package beauty.scheduler.web.myspring.enums;

public enum ContentType {
    HTML("text/html"),
    JSON("application/json");

    //NOTE: it can be also extended by:
    // XML("application/xml"),
    // PDF("application/pdf")
    // GIF("image/gif");
    //etc...

    private String strContentType;

    ContentType(String strContentType) {
        this.strContentType = strContentType;
    }

    public String getStrContentType() {
        return strContentType;
    }
}