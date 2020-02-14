package beauty.scheduler.web.myspring;

import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.web.myspring.annotations.DefaultTemplate;
import beauty.scheduler.web.myspring.annotations.EndpointMethod;
import beauty.scheduler.web.myspring.annotations.Restriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//NOTE: partly ready for review
public class Endpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);

    private RequestMethod requestMethod;
    private String urlPattern;
    private Method controllerMethod;
    private RoleMap<ExceptionKind> exceptions;
    private RoleMap<String> redirections;
    private RoleMap<String> templates;

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

    public static int keyHashCode(RequestMethod requestMethod, String urlPattern) {
        int result = 1;
        final int PRIME = 59;
        result = result * PRIME + (requestMethod == null ? 43 : requestMethod.hashCode());
        result = result * PRIME + (urlPattern == null ? 79 : urlPattern.hashCode());
        return result;
    }

    @Override
    public int hashCode() {
        final RequestMethod requestMethod = this.getRequestMethod();
        final String urlPattern = this.getUrlPattern();

        return keyHashCode(requestMethod, urlPattern);
    }

    @Override
    public String toString() {
        //TODO
        return super.toString();
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

}