package cz.incad.razitka.server;

import javax.servlet.ServletException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aplikator.client.shared.descriptor.ApplicationDTO;
import org.aplikator.server.Configurator;
import org.aplikator.server.data.Context;
import org.aplikator.server.descriptor.Application;
import org.aplikator.server.descriptor.Function;
import org.aplikator.server.descriptor.Menu;

import com.typesafe.config.ConfigValue;

import cz.incad.razitka.DLists;
import cz.incad.razitka.Exemplar;
import cz.incad.razitka.ImportRazitek;


public class Structure extends Application {
    private static final Logger LOG = Logger.getLogger(Structure.class.getName());

    public static final DLists DLists = new DLists();
    public static final Exemplar Exemplar = new Exemplar();

    static {
    }

    @Override
    public ApplicationDTO getApplicationDTO(Context ctx) {
        ApplicationDTO retval = new ApplicationDTO(ctx.getHttpServletRequest().getRemoteUser());

        for (Menu m : menus) {
            if (!m.getId().equals("Menu:administrace")) {
                retval.addMenu(m.getMenuDTO(ctx));
            }else if (ctx.getHttpServletRequest().isUserInRole("admin")){
                retval.addMenu(m.getMenuDTO(ctx));
            }
        }
        retval.setBrand(Configurator.get().getLocalizedString(Configurator.BRAND, ctx.getUserLocale()));
        if (!ctx.getHttpServletRequest().isUserInRole("admin")){
            retval.setDefaultAction("list/"+Exemplar.view().getId());
            retval.setShowNavigation(false);
        }else{
            retval.setDefaultAction(null);
            retval.setShowNavigation(true);
        }

        Set<Map.Entry<String, ConfigValue>> configSet = Configurator.get().getConfig().entrySet();
        for (Map.Entry<String, ConfigValue> entry : configSet) {
            if (entry.getKey().startsWith("aplikator")) {
                retval.setConfigString(entry.getKey(), entry.getValue().render());
            }
        }
        retval.getConfig().putAll(Configurator.get().getSystemLabels(ctx.getUserLocale()));
        return retval;
    }

    @Override
    public void initialize()  {
        try {
            LOG.info("Razitka Loader started");

            Menu menuAgendy = new Menu("agendy");
            menuAgendy.addView(Structure.Exemplar.view());

            Menu menuAdministrace = new Menu("administrace");
            menuAdministrace.addView(Structure.DLists.druh());
            Function importFunction = new Function("ImportRazitek", "ImportRazitek", new ImportRazitek());
            menuAdministrace.addFunction(importFunction);

            addMenu(menuAgendy).addMenu(menuAdministrace);
            LOG.info("Razitka Loader finished");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Razitka Loader error:", ex);
            throw new RuntimeException("Razitka Loader error: ", ex);
        }
    }
}
