package cz.incad.razitka;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.fileupload.util.Streams;
import org.aplikator.client.shared.descriptor.QueryParameterDTO;
import org.aplikator.server.DescriptorRegistry;
import org.aplikator.server.data.AplikatorServiceBackend;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.QueryParameter;
import org.aplikator.server.descriptor.SortDescriptor;
import org.aplikator.server.descriptor.SortItem;
import org.aplikator.server.descriptor.View;
import org.aplikator.server.persistence.tempstore.Tempstore;
import org.aplikator.server.persistence.tempstore.TempstoreFactory;
import org.aplikator.server.query.QueryExpression;

import cz.incad.razitka.server.Structure;

@WebServlet(
        name = "ExportServlet",
        urlPatterns = {"/export"})
public class ExportServlet extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(ExportServlet.class.getName());
    private static final String CHAR_ENCODING = "UTF-8";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileId = req.getParameter("fileId");

        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Pragma", "no-cache");

        OutputStreamWriter out = new OutputStreamWriter(resp.getOutputStream(), CHAR_ENCODING);

        try {
            Tempstore ts = TempstoreFactory.getTempstore();
            resp.setHeader("Content-disposition", "attachment; filename*=UTF-8''" + ts.getFilename(fileId));
            resp.setContentType("application/octet-stream");
            InputStream input = ts.load(fileId);
            Streams.copy(input,resp.getOutputStream(), true);
            input.close();
        } finally {

        }
    }


}
