package cz.incad.razitka;

import static org.aplikator.server.descriptor.Panel.column;
import static org.aplikator.server.descriptor.Panel.row;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.aplikator.client.shared.data.ClientContext;
import org.aplikator.client.shared.data.FunctionResult;
import org.aplikator.client.shared.data.FunctionResultType;
import org.aplikator.client.shared.descriptor.QueryDescriptorDTO;
import org.aplikator.client.shared.descriptor.QueryParameterDTO;
import org.aplikator.server.DescriptorRegistry;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.Executable;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.Function;
import org.aplikator.server.descriptor.Property;
import org.aplikator.server.descriptor.QueryParameter;
import org.aplikator.server.descriptor.SortDescriptor;
import org.aplikator.server.descriptor.SortItem;
import org.aplikator.server.descriptor.View;
import org.aplikator.server.descriptor.WizardPage;
import org.aplikator.server.persistence.tempstore.Tempstore;
import org.aplikator.server.persistence.tempstore.TempstoreFactory;
import org.aplikator.server.query.QueryExpression;

import cz.incad.razitka.server.Structure;


public class ExportCSV extends Function {
    private static final String NAME = "ExportCSV";
    Property<String> filename;


    public ExportCSV() {
        super(NAME, NAME, new Executor());
        WizardPage p1 = new WizardPage(this, "firstPage");
        filename = p1.stringProperty("filename");
        p1.form(row(
                column(filename)
        ), false);

    }


    private static class Executor extends Executable {
        Logger logger = Logger.getLogger(ExportCSV.class.getName());


        private QueryDescriptorDTO queryDescriptor;

        @Override
        public FunctionResult execute(Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
            ExportCSV func = (ExportCSV) function;
            try {
                String viewId = clientContext.getView().getId();
                String activeSort = clientContext.getView().getActiveSort();
                String activeFilter = clientContext.getView().getActiveFilter();
                queryDescriptor = clientContext.getView().getActiveQueryDescriptor();

                View view = (View) DescriptorRegistry.get().getDescriptionItem(viewId);
                List<QueryParameter> queryParameters = new ArrayList<QueryParameter>(queryDescriptor.getQueryParameters().size());
                for (QueryParameterDTO queryParameterDTO : queryDescriptor.getQueryParameters()) {
                    queryParameters.add(new QueryParameter(queryParameterDTO));
                }
                QueryExpression queryExpression = view.getQueryDescriptor(activeFilter, context).getQueryExpression(queryParameters, context);
                SortItem[] sortItems = null;
                if (activeSort != null && !activeSort.equals("")) {
                    SortDescriptor sortDescriptor = view.getSortDescriptor(activeSort);
                    sortItems = sortDescriptor.getItems().toArray(new SortItem[sortDescriptor.getItems().size()]);
                }


                StringBuilder sb = new StringBuilder("\"ID\";" +
                        "\"Nápis\";" +
                        "\"Signatura\";" +
                        "\"Sys\";" +
                        "\"Druh\";" +
                        "\"Osoba\";" +
                        "\"Instituce\";" +
                        "\"Obecné poznámky\";" +
                        "\"Město\";" +
                        "\"Vlastník\"\r\n");

                logger.info("STARTED READING RECORDS ");
                for (Record slozka : context.getRecords(Structure.Exemplar)
                        .withQuery(queryExpression)
                        .withSort(sortItems)
                        .list()) {
                    logger.info("RECORD:" + slozka.getPrimaryKey().getId() + " - " + slozka.getStringValue(Structure.Exemplar.napis, context));
                    sb.append("\"").append(slozka.getPrimaryKey().getId()).append("\"").append(";");
                    sb.append("\"").append(slozka.getStringValue(Structure.Exemplar.napis, context).replaceAll("\"", "'")).append("\";");
                    sb.append("\"").append(slozka.getStringValue(Structure.Exemplar.signatura, context).replaceAll("\"", "'")).append("\";");
                    sb.append("\"").append(slozka.getStringValue(Structure.Exemplar.sys, context).replaceAll("\"", "'")).append("\";");
                    sb.append("\"").append(slozka.getStringValue(Structure.Exemplar.druh, context).replaceAll("\"", "'")).append("\";");
                    sb.append("\"").append(slozka.getStringValue(Structure.Exemplar.prijmeni, context).replaceAll("\"", "'")).append("\";");
                    sb.append("\"").append(slozka.getStringValue(Structure.Exemplar.instituce, context).replaceAll("\"", "'")).append("\";");
                    sb.append("\"").append(slozka.getStringValue(Structure.Exemplar.obecne, context).replaceAll("\"", "'")).append("\";");
                    sb.append("\"").append(slozka.getStringValue(Structure.Exemplar.mesto, context).replaceAll("\"", "'")).append("\";");
                    sb.append("\"").append(slozka.getStringValue(Structure.Exemplar.vlastnik, context).replaceAll("\"", "'")).append("\"").append("\r\n");


                }
                logger.info("FINISHED READING RECORDS");

                Tempstore ts = TempstoreFactory.getTempstore();
                String fileTempID = ts.storeString(wizardParameters.getValue(func.filename), sb.toString(), "UTF-8");
                HttpServletRequest req = context.getHttpServletRequest();
                String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath()
                        + "/export?fileId=" + fileTempID;
                return new FunctionResult(baseUrl, true, FunctionResultType.DOWNLOAD);
            } catch (Throwable t) {

                return new FunctionResult("Export do CSV nebyl spuštěn: " + t, false);
            }

        }

        @Override
        public WizardPage getWizardPage(String currentPageId, boolean forwardDirection, Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
            WizardPage page = super.getWizardPage(currentPageId, forwardDirection, currentRecord, wizardParameters, clientContext, context);
            ExportCSV func = (ExportCSV) function;
            wizardParameters.setValue(func.filename, "export.csv");
            page.setClientRecord(wizardParameters);
            return page;
        }
    }


}

