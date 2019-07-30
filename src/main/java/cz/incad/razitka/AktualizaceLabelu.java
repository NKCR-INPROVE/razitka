package cz.incad.razitka;

import cz.incad.razitka.server.Structure;
import org.aplikator.client.shared.data.ClientContext;
import org.aplikator.client.shared.data.FunctionResult;
import org.aplikator.client.shared.descriptor.QueryDescriptorDTO;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.Executable;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.WizardPage;

import java.util.List;
import java.util.logging.Logger;


public class AktualizaceLabelu extends Executable {
    private static final String NAME = "AktualizaceLabelu";

    Logger logger = Logger.getLogger(AktualizaceLabelu.class.getName());


    private QueryDescriptorDTO queryDescriptor;

    @Override
    public FunctionResult execute(Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        try {

            logger.info("STARTED READING RECORDS ");
            for (Record exemplar : context.getRecords(Structure.Exemplar).list()) {
                logger.info("RECORD:" + exemplar.getPrimaryKey().getId() + " - " + exemplar.getStringValue(Structure.Exemplar.napis, context));
                List<Record> knihy = exemplar.getCollectionRecords(Structure.Exemplar.kniha, context);
                if (!knihy.isEmpty()) {
                    Record clone = exemplar.clone();
                    clone.setValue(Structure.Exemplar.label, knihy.get(0).getStringValue(Structure.Kniha.sys, context).split("\n")[0]);
                    context.addUpdatedRecordToContainer(exemplar, clone);
                }
                context.processRecordContainer();
                context.clearRecordContainer();
            }
            logger.info("FINISHED READING RECORDS");

            return new FunctionResult("Aktualizovano", true);
        } catch (Throwable t) {

            return new FunctionResult("Aktualizace labelu nebyla spuštěna: " + t, false);
        }

    }

    @Override
    public WizardPage getWizardPage(String currentPage, boolean forwardFlag, Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        return null;
    }


}

