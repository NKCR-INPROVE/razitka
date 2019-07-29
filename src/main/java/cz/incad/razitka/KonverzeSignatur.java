package cz.incad.razitka;

import cz.incad.razitka.server.Structure;
import org.aplikator.client.shared.data.ClientContext;
import org.aplikator.client.shared.data.FunctionResult;
import org.aplikator.client.shared.descriptor.QueryDescriptorDTO;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.Executable;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.WizardPage;

import java.util.logging.Logger;

import static org.aplikator.server.data.RecordUtils.newSubrecord;


public class KonverzeSignatur extends Executable {
    private static final String NAME = "KonverzeSignatur";

    Logger logger = Logger.getLogger(KonverzeSignatur.class.getName());


    private QueryDescriptorDTO queryDescriptor;

    @Override
    public FunctionResult execute(Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        try {

            logger.info("STARTED READING RECORDS ");
            for (Record exemplar : context.getRecords(Structure.Exemplar).list()) {
                logger.info("RECORD:" + exemplar.getPrimaryKey().getId() + " - " + exemplar.getStringValue(Structure.Exemplar.napis, context));
                String[] signatury = exemplar.getStringValue(Structure.Exemplar.signatura, context).split("\n");
                String[] sysna = exemplar.getStringValue(Structure.Exemplar.sys, context).split("\n");
                for (int i = 0; i < Math.max(signatury.length, sysna.length); i++) {
                    String signatura = i < signatury.length ? signatury[i] : "";
                    String sysno = i < sysna.length ? sysna[i] : "";
                    logger.info("--- Nova kniha signatura:" + signatura + " sysno:" + sysno);
                    Record novaKniha = newSubrecord(exemplar.getPrimaryKey(), Structure.Exemplar.kniha);
                    novaKniha.setValue(Structure.Kniha.signatura, signatura);
                    novaKniha.setValue(Structure.Kniha.sys, sysno);
                    context.addNewRecordToContainer(novaKniha);
                }
                context.processRecordContainer();
                context.clearRecordContainer();
            }
            logger.info("FINISHED READING RECORDS");

            return new FunctionResult("Konvertovano", true);
        } catch (Throwable t) {

            return new FunctionResult("Konverze signatur nebyla spuštěna: " + t, false);
        }

    }

    @Override
    public WizardPage getWizardPage(String currentPage, boolean forwardFlag, Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        return null;
    }


}

