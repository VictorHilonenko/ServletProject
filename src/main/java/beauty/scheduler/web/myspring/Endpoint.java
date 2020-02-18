package beauty.scheduler.web.myspring;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.web.myspring.annotations.DefaultTemplate;
import beauty.scheduler.web.myspring.annotations.EndpointMethod;
import beauty.scheduler.web.myspring.annotations.Restriction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

//NOTE: partly ready for review
public class Endpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);

    private RequestMethod requestMethod;
    private String urlPattern;
    private Method controllerMethod;
    private RoleMap<ExceptionKind> exceptions;
    private RoleMap<String> redirections;
    private RoleMap<String> templates;

    public static String endpointKey(RequestMethod requestMethod, String urlPattern) {
        StringJoiner sj = new StringJoiner("->");
        sj.add(requestMethod.name());
        sj.add(urlPattern);
        return sj.toString();
    }

    public Endpoint(Method routableMethod) {
        if (routableMethod == null) {
            this.urlPattern = ""; //that's for "notFoundEdnpoint" in Router
            return;
        }

        EndpointMethod endpointMethodAnnotation = routableMethod.getAnnotation(EndpointMethod.class);

        this.controllerMethod = routableMethod;
        this.requestMethod = endpointMethodAnnotation.requestMethod();
        this.urlPattern = endpointMethodAnnotation.urlPattern();

        initializeRestrictions(routableMethod);
        initializeDefaultTemplates(routableMethod);
    }

    private void initializeRestrictions(Method method) {
        Map<Role, ExceptionKind> exceptions = new HashMap<>();
        Map<Role, String> redirections = new HashMap<>();

        Arrays.stream(method.getAnnotationsByType(Restriction.class)).forEach(restriction -> {
            ExceptionKind exceptionKind = restriction.exception();
            String redirection = restriction.redirection();

            Role.getAllByRoleTag(restriction.role()).forEach(role -> {
                if (!exceptionKind.equals(ExceptionKind.NULL)) {
                    exceptions.put(role, exceptionKind);
                } else if (!"".equals(redirection)) {
                    redirections.put(role, redirection);
                }
            });
        });

        if (exceptions.size() > 0) {
            this.exceptions = new RoleMap<>(exceptions);
        }
        if (redirections.size() > 0) {
            this.redirections = new RoleMap<>(redirections);
        }
    }

    private void initializeDefaultTemplates(Method method) {
        Map<Role, String> templates = new HashMap<>();

        Arrays.stream(method.getAnnotationsByType(DefaultTemplate.class)).forEach(defaultTemplate -> {
            String template = defaultTemplate.template();

            Role.getAllByRoleTag(defaultTemplate.role()).forEach(role -> {
                if (!"".equals(template)) {
                    templates.put(role, template);
                }
            });
        });

        if (templates.size() > 0) {
            this.templates = new RoleMap<>(templates);
        }
    }

    public String getEndpointKey() {
        return endpointKey(this.requestMethod, this.urlPattern);
    }

    public boolean hasExceptionForRole(Role role) {
        if (exceptions == null) {
            return false;
        }

        return exceptions.getForRole(role) != null;
    }

    public boolean hasRedirectionForRole(Role role) {
        if (redirections == null) {
            return false;
        }

        return redirections.getForRole(role) != null;
    }

    public boolean hasDefaultTemplateForRole(Role role) {
        if (templates == null) {
            return false;
        }

        return templates.getForRole(role) != null;
    }

    public ExceptionKind getExceptionForRole(Role role) {
        if (exceptions == null) {
            return null;
        }

        return exceptions.getForRole(role);
    }

    public String getRedirectionForRole(Role role) {
        if (redirections == null) {
            return null;
        }

        return redirections.getForRole(role);
    }

    public String getDefaultTemplateForRole(Role role) {
        if (templates == null) {
            return null;
        }

        return templates.getForRole(role);
    }

    public RequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    public String getUrlPattern() {
        return this.urlPattern;
    }

    public Method getControllerMethod() {
        return this.controllerMethod;
    }

    public RoleMap<ExceptionKind> getExceptions() {
        return this.exceptions;
    }

    public RoleMap<String> getRedirections() {
        return this.redirections;
    }

    public RoleMap<String> getTemplates() {
        return this.templates;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public void setControllerMethod(Method controllerMethod) {
        this.controllerMethod = controllerMethod;
    }

    public void setExceptions(RoleMap<ExceptionKind> exceptions) {
        this.exceptions = exceptions;
    }

    public void setRedirections(RoleMap<String> redirections) {
        this.redirections = redirections;
    }

    public void setTemplates(RoleMap<String> templates) {
        this.templates = templates;
    }

    //NOTE: two Endpoints that have equal requestMethod and urlPattern has to be equal,
    //that is why these equals and hashCode methods use only two fields
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
                .append("controllerMethod", controllerMethod)
                .append("exceptions", exceptions)
                .append("redirections", redirections)
                .append("templates", templates)
                .toString();
    }
}