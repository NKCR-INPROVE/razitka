package cz.incad.razitka;

import cz.incad.razitka.server.Structure;
import org.aplikator.client.shared.data.ClientContext;
import org.aplikator.client.shared.data.FunctionResult;
import org.aplikator.client.shared.data.FunctionResultStatus;
import org.aplikator.client.shared.descriptor.QueryDescriptorDTO;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.Executable;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.WizardPage;

import java.util.List;
import java.util.logging.Logger;


public class Aktualizace2023 extends Executable {
    private static final String NAME = "Aktualizace2023";

    Logger logger = Logger.getLogger(Aktualizace2023.class.getName());


    private QueryDescriptorDTO queryDescriptor;

    @Override
    public FunctionResult execute(Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        try {

            logger.info("STARTED READING RECORDS ");
            for (Record exemplar : context.getRecords(Structure.Exemplar).list()) {
                logger.info("RECORD:" + exemplar.getPrimaryKey().getId() + " - " + exemplar.getStringValue(Structure.Exemplar.napis, context));
                Record clone = exemplar.clone();
                Structure.Exemplar.processAccents(clone, context);
                List<Record> knihy = exemplar.getCollectionRecords(Structure.Exemplar.kniha, context);
                if (!knihy.isEmpty()) {
                    Structure.Exemplar.collectValuesKnihy(context, clone, knihy);
                }
                context.addUpdatedRecordToContainer(exemplar, clone);
                context.processRecordContainer();
                context.clearRecordContainer();
            }
            logger.info("FINISHED READING RECORDS");

            return new FunctionResult("Aktualizovano", FunctionResultStatus.SUCCESS);
        } catch (Throwable t) {

            return new FunctionResult("Aktualizace 2023 nebyla spuštěna: " + t, FunctionResultStatus.ERROR);
        }

    }

    @Override
    public WizardPage getWizardPage(String currentPage, boolean forwardFlag, Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        return null;
    }


}

