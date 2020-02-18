package beauty.scheduler.web;

import beauty.scheduler.web.myspring.core.Router;
import beauty.scheduler.web.myspring.core.ServletContextInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class Servlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(Servlet.class);

    private static final long serialVersionUID = 1L;

    @Override
    public void init() {
        ServletContextInitializer.init(getServletContext());
        LOGGER.info("servlet started work");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Router.route(req, resp);
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}