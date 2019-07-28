package cz.incad.razitka.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.incad.razitka.Kniha;
import org.aplikator.client.shared.descriptor.Access;
import org.aplikator.server.descriptor.AccessControl;
import org.aplikator.server.descriptor.Application;
import org.aplikator.server.descriptor.Function;
import org.aplikator.server.descriptor.Menu;

import cz.incad.razitka.DLists;
import cz.incad.razitka.Exemplar;
import cz.incad.razitka.ImportRazitek;
import org.aplikator.server.security.Accounts;


public class Structure extends Application {
    private static final Logger LOG = Logger.getLogger(Structure.class.getName());

    public static final DLists DLists = new DLists();
    public static final cz.incad.razitka.Kniha Kniha = new Kniha();
    public static final Exemplar Exemplar = new Exemplar();
    public static final org.aplikator.server.security.Accounts Accounts = new Accounts();

    static {
        Exemplar.kniha = Exemplar.collectionProperty(Kniha, "Exemplar", "Exemplar_ID");
    }
/*
    @Override
    public ApplicationDTO getApplicationDTO(Context ctx) {
        ApplicationDTO retval = super.getApplicationDTO(ctx);

        if (!ctx.getHttpServletRequest().isUserInRole("admin")){
            retval.setShowNavigation(false);
        }else{
            retval.setShowNavigation(true);
        }
        return retval;
    }
*/

    @Override
    public void initialize() {
        try {
            LOG.info("Razitka Loader started");
            Exemplar.setAccessControl(AccessControl.Default.authenticatedFullAccess()); //.guest(Access.READ));
            DLists.setAccessControl(AccessControl.Default.authenticated(Access.NONE).role("superuser", Access.READ_WRITE_CREATE_DELETE).role("admin", Access.READ_WRITE_CREATE_DELETE));
            Accounts.setAccessControl(AccessControl.Default.authenticated(Access.NONE).role("admin", Access.READ_WRITE_CREATE_DELETE));
            setDefaultAction("list/" + Exemplar.view().getId());
            Menu menuAgendy = new Menu("agendy");
            menuAgendy.addView(Structure.Exemplar.view());

            Menu menuAdministrace = new Menu("administrace");
            menuAdministrace.addView(Structure.DLists.druh());
            menuAdministrace.addView(Structure.DLists.vlastnik());
            menuAdministrace.addView(Structure.DLists.jazyk());
            menuAdministrace.addView(Structure.Accounts.view());
            Function importFunction = new Function("ImportRazitek", "ImportRazitek", new ImportRazitek());
            importFunction.setAccessControl(AccessControl.Default.authenticated(Access.NONE).role("admin", Access.READ_WRITE_CREATE_DELETE));
            menuAdministrace.addFunction(importFunction);

            addMenu(menuAgendy).addMenu(menuAdministrace);
            LOG.info("Razitka Loader finished");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Razitka Loader error:", ex);
            throw new RuntimeException("Razitka Loader error: ", ex);
        }
    }
}
