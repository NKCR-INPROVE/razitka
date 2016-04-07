package cz.incad.razitka.server;

import java.util.Map;
import java.util.Set;

import org.aplikator.client.shared.descriptor.ApplicationDTO;
import org.aplikator.server.Configurator;
import org.aplikator.server.data.Context;
import org.aplikator.server.descriptor.Application;
import org.aplikator.server.descriptor.Menu;

import com.typesafe.config.ConfigValue;

import cz.incad.razitka.DLists;
import cz.incad.razitka.Exemplar;


public class Structure extends Application {

    public static final DLists DLists = new DLists();
    public static final Exemplar Exemplar = new Exemplar();

    static {
    }

    @Override
    public ApplicationDTO getApplicationDTO(Context ctx) {
        ApplicationDTO retval = new ApplicationDTO();

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
        //retval.setShowNavigation(showNavigation);

        Set<Map.Entry<String, ConfigValue>> configSet = Configurator.get().getConfig().entrySet();
        for (Map.Entry<String, ConfigValue> entry : configSet) {
            if (entry.getKey().startsWith("aplikator")) {
                retval.setConfigString(entry.getKey(), entry.getValue().render());
            }
        }
        retval.getConfig().putAll(Configurator.get().getSystemLabels(ctx.getUserLocale()));
        return retval;
    }
}
