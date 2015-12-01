package cz.incad.razitka.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aplikator.client.shared.descriptor.ApplicationDTO;
import org.aplikator.server.Context;
import org.aplikator.server.descriptor.Application;
import org.aplikator.server.descriptor.ListProvider;
import org.aplikator.server.descriptor.Menu;
import org.aplikator.server.util.Configurator;

import com.typesafe.config.ConfigValue;

import cz.incad.razitka.Exemplar;
import cz.incad.razitka.DLists;


public class Structure extends Application {
    // Lists
    public static final HashMap<Enum<?>, ListProvider> listProviders = new HashMap<Enum<?>, ListProvider>();
    
    
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
        retval.setShowNavigation(showNavigation);
        retval.setDefaultAction(defaultActionToken);
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
