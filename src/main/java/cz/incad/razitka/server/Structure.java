package cz.incad.razitka.server;

import cz.incad.razitka.*;
import org.aplikator.client.shared.descriptor.Access;
import org.aplikator.client.shared.descriptor.ApplicationDTO;
import org.aplikator.server.data.Context;
import org.aplikator.server.descriptor.*;
import org.aplikator.server.security.Accounts;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Structure extends Application {
    private static final Logger LOG = Logger.getLogger(Structure.class.getName());

    public static final DLists DLists = new DLists();
    public static final cz.incad.razitka.Kniha Kniha = new Kniha();
    public static final cz.incad.razitka.Zdroj Zdroj = new Zdroj();
    public static final Exemplar Exemplar = new Exemplar();
    public static final org.aplikator.server.security.Accounts Accounts = new Accounts();

    static {
        Exemplar.kniha = Exemplar.collectionProperty(Kniha, "Exemplar", "Exemplar_ID");
        Exemplar.zdroj = Exemplar.collectionProperty(Zdroj, "Zdroj", "Exemplar_ID");
    }

    private Action adminViewAction = new Action(Exemplar.adminView().getId(), Exemplar.adminView().getLocalizationKey(), "list/" + Exemplar.adminView().getId());
    private Action guestViewAction = new Action(Exemplar.guestView().getId(), Exemplar.guestView().getLocalizationKey(), "list/" + Exemplar.guestView().getId());
    @Override
    public ApplicationDTO getApplicationDTO(Context ctx) {
        ApplicationDTO retval = super.getApplicationDTO(ctx);
        if (!ctx.isAuthenticated()){
            retval.setShowNavigation(false);
            retval.setDefaultAction("list/" + Exemplar.guestView().getId());
        }else{
            retval.setShowNavigation(true);
            if (ctx.isUserInRole("admin")){
                retval.getMenus().get(0).getActions().clear();
                retval.getMenus().get(0).getActions().add(adminViewAction.getActionDTO(ctx));
                retval.setDefaultAction("list/" + Exemplar.adminView().getId());
            } else {
                retval.setDefaultAction("list/" + Exemplar.view().getId());
            }
        }
        return retval;
    }


    @Override
    public void initialize() {
        try {
            LOG.info("Razitka Loader started");
            Exemplar.setAccessControl(AccessControl.Default.authenticatedFullAccess().guest(Access.READ));
            Kniha.setAccessControl(AccessControl.Default.authenticatedFullAccess().guest(Access.READ));
            DLists.setAccessControl(AccessControl.Default.authenticated(Access.NONE).role("superuser", Access.READ_WRITE_CREATE_DELETE).role("admin", Access.READ_WRITE_CREATE_DELETE));
            Accounts.setAccessControl(AccessControl.Default.authenticated(Access.NONE).role("admin", Access.READ_WRITE_CREATE_DELETE));
            setAccountsEntity(Accounts);
            //setDefaultAction("list/" + Exemplar.view().getId());
            Menu menuAgendy = new Menu("agendy");
            menuAgendy.addView(Structure.Exemplar.view());

            Menu menuAdministrace = new Menu("administrace");
            menuAdministrace.addView(Structure.DLists.druh());
            menuAdministrace.addView(Structure.DLists.vlastnik());
            menuAdministrace.addView(Structure.DLists.jazyk());
            menuAdministrace.addView(Structure.Accounts.view());
//            Function importFunction = new Function("ImportRazitek", "ImportRazitek", new ImportRazitek());
//            importFunction.setAccessControl(AccessControl.Default.authenticated(Access.NONE).role("admin", Access.READ_WRITE_CREATE_DELETE));
//            menuAdministrace.addFunction(importFunction);
//            Function convertFunction = new Function("AktualizaceLabelu", "AktualizaceLabelu", new AktualizaceLabelu());
//            convertFunction.setAccessControl(AccessControl.Default.authenticated(Access.NONE).role("admin", Access.READ_WRITE_CREATE_DELETE));
//            menuAdministrace.addFunction(convertFunction);


//            Function update = new Function("KonverzeZdroju", "KonverzeZdroju", new KonverzeZdroju());
//            update.setAccessControl(AccessControl.Default.authenticated(Access.NONE).role("admin", Access.READ_WRITE_CREATE_DELETE));
//            menuAdministrace.addFunction(update);

            addMenu(menuAgendy).addMenu(menuAdministrace);
            LOG.info("Razitka Loader finished");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Razitka Loader error:", ex);
            throw new RuntimeException("Razitka Loader error: ", ex);
        }
    }
}
