package cz.incad.razitka;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import cz.incad.razitka.server.Structure;
import org.aplikator.client.shared.data.ClientContext;
import org.aplikator.client.shared.data.FunctionResult;
import org.aplikator.client.shared.data.FunctionResultStatus;
import org.aplikator.client.shared.data.FunctionResultType;
import org.aplikator.client.shared.descriptor.QueryDescriptorDTO;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.Executable;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.WizardPage;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;


public class Souvisejici extends Executable {
    private static final String NAME = "Souvisejici";

    Logger logger = Logger.getLogger(Souvisejici.class.getName());


    private QueryDescriptorDTO queryDescriptor;

    @Override
    public FunctionResult execute(Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        try {
            HttpServletRequest req = context.getHttpServletRequest();
            String baseUrl = new SafeHtmlBuilder().appendHtmlConstant(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath())
                    .appendHtmlConstant( "/related?")
                    .appendHtmlConstant("prijmeni=").appendEscaped(currentRecord.getStringValue(Structure.Exemplar.prijmeni, context))
                    .appendHtmlConstant("&instituce=").appendEscaped(currentRecord.getStringValue(Structure.Exemplar.instituce, context))
                    .toSafeHtml().asString();
            return new FunctionResult(baseUrl, FunctionResultStatus.SUCCESS, FunctionResultType.WINDOW);
        } catch (Throwable e) {
            return new FunctionResult("Přehled souvisejich záznamů nebyl spuštěn." + e, FunctionResultStatus.ERROR);
        }

    }

    @Override
    public WizardPage getWizardPage(String currentPage, boolean forwardFlag, Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        return null;
    }


}

