package beauty.scheduler.web.myspring.core;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.StringUtils;
import beauty.scheduler.web.myspring.annotation.DefaultTemplate;
import beauty.scheduler.web.myspring.annotation.EndpointMethod;
import beauty.scheduler.web.myspring.annotation.Restriction;
import beauty.scheduler.web.myspring.enums.ContentType;
import beauty.scheduler.web.myspring.enums.RequestMethod;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.StringJoiner;

import static beauty.scheduler.util.AppConstants.CURLY_BRACES_LEFT;
import static beauty.scheduler.util.AppConstants.SLASH_SYMBOL;

class Endpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);

    private Method method;
    private RoleMap<ExceptionKind> exceptions;
    private RoleMap<String> redirections;
    private RoleMap<String> templates;
    private RequestMethod requestMethod;
    private String urlPattern;
    private ContentType contentType;
    private String quickUrlPattern;
    private int slashesCount;

    static String endpointKey(RequestMethod requestMethod, String urlPattern) {
        StringJoiner sj = new StringJoiner("<-");
        sj.add(urlPattern);
        sj.add(requestMethod.name());
        return sj.toString();
    }

    public Endpoint(Method endpointMethod) {
        this.method = endpointMethod;
        this.exceptions = new RoleMap<>();
        this.redirections = new RoleMap<>();
        this.templates = new RoleMap<>();

        if (endpointMethod == null) {
            this.exceptions.addValueForRole(Role.all, ExceptionKind.PAGE_NOT_FOUND);
            return;
        }

        EndpointMethod endpointMethodAnnotation = endpointMethod.getAnnotation(EndpointMethod.class);

        this.requestMethod = endpointMethodAnnotation.requestMethod();
        this.setUrlPattern(endpointMethodAnnotation.urlPattern());
        this.contentType = endpointMethodAnnotation.contentType();

        initializeRestrictions(endpointMethod);
        initializeDefaultTemplates(endpointMethod);
    }

    private void initializeRestrictions(Method method) {
        Arrays.stream(method.getAnnotationsByType(Restriction.class)).forEach(restriction -> {
            ExceptionKind exceptionKind = restriction.exception();
            String redirection = restriction.redirection();

            Role.getAllByRoleTag(restriction.role()).forEach(role -> {
                if (!exceptionKind.equals(ExceptionKind.NULL)) {
                    exceptions.addValueForRole(role, exceptionKind);
                } else if (!"".equals(redirection)) {
                    redirections.addValueForRole(role, redirection);
                }
            });
        });
    }

    private void initializeDefaultTemplates(Method method) {
        Arrays.stream(method.getAnnotationsByType(DefaultTemplate.class)).forEach(defaultTemplate -> {
            String template = defaultTemplate.template();

            Role.getAllByRoleTag(defaultTemplate.role()).forEach(role -> {
                if (!"".equals(template)) {
                    templates.addValueForRole(role, template);
                }
            });
        });
    }

    String getEndpointKey() {
        return endpointKey(this.requestMethod, this.urlPattern);
    }

    private void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;

        this.slashesCount = StringUtils.count(urlPattern, SLASH_SYMBOL);
        int indexOf = urlPattern.indexOf(CURLY_BRACES_LEFT);
        if (indexOf == -1) {
            this.quickUrlPattern = urlPattern;
        } else {
            this.quickUrlPattern = urlPattern.substring(0, indexOf);
        }
    }

    boolean hasExceptionForRole(Role role) {
        return exceptions.getForRole(role) != null;
    }

    boolean hasRedirectionForRole(Role role) {
        return redirections.getForRole(role) != null;
    }

    boolean hasDefaultTemplateForRole(Role role) {
        return templates.getForRole(role) != null;
    }

    ExceptionKind getExceptionForRole(Role role) {
        return exceptions.getForRole(role);
    }

    String getRedirectionForRole(Role role) {
        return redirections.getForRole(role);
    }

    String getDefaultTemplateForRole(Role role) {
        return templates.getForRole(role);
    }

    RequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    String getUrlPattern() {
        return this.urlPattern;
    }

    ContentType getContentType() {
        return contentType;
    }

    Method getMethod() {
        return this.method;
    }

    int getSlashesCount() {
        return slashesCount;
    }

    String getQuickUrlPattern() {
        return quickUrlPattern;
    }

    //NOTE: two Endpoints that have equal requestMethod and urlPattern has to be equal,
    //that is why these equals and hashCode methods use only two fields:
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(requestMethod)
                .append(urlPattern)
                .toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Endpoint endpoint = (Endpoint) o;

        return new EqualsBuilder()
                .append(requestMethod, endpoint.requestMethod)
                .append(urlPattern, endpoint.urlPattern)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("requestMethod", requestMethod)
                .append("urlPattern", urlPattern)
                .append("method", method)
                .append("exceptions", exceptions)
                .append("redirections", redirections)
                .append("templates", templates)
                .toString();
    }
}