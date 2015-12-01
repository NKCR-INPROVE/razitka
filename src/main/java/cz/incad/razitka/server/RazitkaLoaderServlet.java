package cz.incad.razitka.server;

import javax.servlet.ServletException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aplikator.server.ApplicationLoaderServlet;
import org.aplikator.server.descriptor.Application;
import org.aplikator.server.descriptor.Function;
import org.aplikator.server.descriptor.Menu;

import cz.incad.razitka.ImportRazitek;

@SuppressWarnings("serial")
public class RazitkaLoaderServlet extends ApplicationLoaderServlet {

    private static final Logger LOG = Logger.getLogger(RazitkaLoaderServlet.class.getName());

    Structure struct;

    @Override
    public void init() throws ServletException {
        try {
            LOG.info("Razitka Loader started");
            struct = (Structure) Application.get();

            Menu menuAgendy = new Menu("agendy");
            menuAgendy.addView(Structure.Exemplar.view());

            Menu menuAdministrace = new Menu("administrace");
            menuAdministrace.addView(Structure.DLists.Dulozeni());
            Function importFunction = new Function("ImportRazitek", "ImportRazitek", new ImportRazitek());
            menuAdministrace.addFunction(importFunction);

            struct.addMenu(menuAgendy).addMenu(menuAdministrace);
            LOG.info("Razitka Loader finished");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Razitka Loader error:", ex);
            throw new ServletException("Razitka Loader error: ", ex);
        }
    }


}
