package cz.incad.razitka;

import cz.incad.razitka.server.Structure;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.aplikator.server.data.AplikatorServiceBackend;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.SortItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(
        name = "RelatedServlet",
        urlPatterns = {"/related"})
public class RelatedServlet extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(RelatedServlet.class.getName());
    private static final String CHAR_ENCODING = "UTF-8";

    private static Configuration getFreemarkerConfig() {
        return LazyHolder.instance;
    }

    private static Configuration instance() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(RelatedServlet.class, "/");
        cfg.setDefaultEncoding(CHAR_ENCODING);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        return cfg;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String prijmeni = req.getParameter("prijmeni");
        String instituce = req.getParameter("instituce");
        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath()+"/#display/View:Exemplar/";
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Pragma", "no-cache");
        OutputStreamWriter out = new OutputStreamWriter(resp.getOutputStream(), CHAR_ENCODING);
        Map<String, Object> root = new HashMap<>();
        root.put("title", "Související záznamy");
        Template header = getFreemarkerConfig().getTemplate("header.ftlh");
        try {
            Environment env = header.createProcessingEnvironment(root, out);
            env.setOutputEncoding(CHAR_ENCODING);
            env.process();
        } catch (TemplateException e) {
            throw new ServletException(e);
        }

        Template temp = getFreemarkerConfig().getTemplate("label.ftlh");
        try (Context ctx = new Context(req, resp, new AplikatorServiceBackend(), null);){
            out.write("<h1>Související záznamy</h1>\n");
            out.write("<h2>Příjmení: "+prijmeni+"</h2>\n");
            if (!prijmeni.isEmpty()) {
                for (Record slozka : ctx.getRecords(Structure.Exemplar)
                        .withQuery(Structure.Exemplar.prijmeni.STARTSWITH_IGNORECASE(prijmeni))
                        .withSort(SortItem.ascending(Structure.Exemplar.prijmeni))
                        .withSort(SortItem.ascending(Structure.Exemplar.label))
                        .list()) {
                    root.clear();
                    root.put("baseUrl", baseUrl);
                    root.put("id", Integer.toString(slozka.getPrimaryKey().getId()));
                    root.put("napis", slozka.getStringValue(Structure.Exemplar.napis, ctx));
                    root.put("label", slozka.getStringValue(Structure.Exemplar.label, ctx));
                    root.put("prijmeni", slozka.getStringValue(Structure.Exemplar.prijmeni, ctx));
                    root.put("instituce", slozka.getStringValue(Structure.Exemplar.instituce, ctx));
                    try {
                        Environment env = temp.createProcessingEnvironment(root, out);
                        env.setOutputEncoding(CHAR_ENCODING);
                        env.process();
                    } catch (TemplateException e) {
                        LOG.log(Level.SEVERE, "Error in template evaluation", e);
                        throw new ServletException(e);
                    }
                }
            }
            out.write("<h2>Instituce: "+instituce+"</h2>\n");
            if (!instituce.isEmpty()) {
                for (Record slozka : ctx.getRecords(Structure.Exemplar)
                        .withQuery(Structure.Exemplar.instituce.STARTSWITH_IGNORECASE(instituce))
                        .withSort(SortItem.ascending(Structure.Exemplar.instituce))
                        .withSort(SortItem.ascending(Structure.Exemplar.label))
                        .list()) {
                    root.clear();
                    root.put("baseUrl", baseUrl);
                    root.put("id", Integer.toString(slozka.getPrimaryKey().getId()));
                    root.put("napis", slozka.getStringValue(Structure.Exemplar.napis, ctx));
                    root.put("label", slozka.getStringValue(Structure.Exemplar.label, ctx));
                    root.put("prijmeni", slozka.getStringValue(Structure.Exemplar.prijmeni, ctx));
                    root.put("instituce", slozka.getStringValue(Structure.Exemplar.instituce, ctx));
                    try {
                        Environment env = temp.createProcessingEnvironment(root, out);
                        env.setOutputEncoding(CHAR_ENCODING);
                        env.process();
                    } catch (TemplateException e) {
                        LOG.log(Level.SEVERE, "Error in template evaluation", e);
                        throw new ServletException(e);
                    }
                }
            }
            out.write(
                    "  </body>\n</html>");
            out.flush();
            out.close();
        }
    }

    private static class LazyHolder {
        private static Configuration instance = instance();
    }

}
