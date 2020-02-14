package beauty.scheduler.web;

import beauty.scheduler.web.myspring.ClassFactory;
import beauty.scheduler.web.myspring.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import static beauty.scheduler.util.AppConstants.ATTR_ACTIVE_USERS;
import static beauty.scheduler.util.AppConstants.ATTR_ROUTER;

//NOTE: ready for review
@WebServlet("/")
public class Servlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(Servlet.class);

    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        ClassFactory classFactory = new ClassFactory(new HashMap<>());
        Router router = new Router(new HashMap<>(), classFactory);

        ServletContext context = getServletContext();
        context.setAttribute(ATTR_ROUTER, router);
        context.setAttribute(ATTR_ACTIVE_USERS, new HashSet<String>());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = getServletContext();

        Router router = (Router) context.getAttribute(ATTR_ROUTER);
        router.route(req, resp);

        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}