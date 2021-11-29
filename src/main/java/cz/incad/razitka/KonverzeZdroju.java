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

import java.util.logging.Logger;

import static org.aplikator.server.data.RecordUtils.newSubrecord;


public class KonverzeZdroju extends Executable {
    private static final String NAME = "KonverzeZdroju";

    Logger logger = Logger.getLogger(KonverzeZdroju.class.getName());


    private QueryDescriptorDTO queryDescriptor;

    @Override
    public FunctionResult execute(Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        try {

            logger.info("STARTED READING RECORDS ");
            for (Record exemplar : context.getRecords(Structure.Exemplar).list()) {
                logger.info("RECORD:" + exemplar.getPrimaryKey().getId() + " - " + exemplar.getStringValue(Structure.Exemplar.napis, context));
                String poznamka = exemplar.getStringValue(Structure.Exemplar.obecne, context);
                if (poznamka.toLowerCase().startsWith("http")) {
                    logger.info("--- Novy zdroj:" + poznamka );
                    Record novyZdroj = newSubrecord(exemplar.getPrimaryKey(), Structure.Exemplar.zdroj);
                    novyZdroj.setValue(Structure.Zdroj.zdroj, poznamka);
                    context.addNewRecordToContainer(novyZdroj);
                    context.processRecordContainer();
                    context.clearRecordContainer();
                }

            }
            logger.info("FINISHED READING RECORDS");

            return new FunctionResult("Konvertovano", FunctionResultStatus.SUCCESS);
        } catch (Throwable t) {

            return new FunctionResult("Konverze zdroju nebyla spuštěna: " + t, FunctionResultStatus.ERROR);
        }

    }

    @Override
    public WizardPage getWizardPage(String currentPage, boolean forwardFlag, Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        return null;
    }


}

