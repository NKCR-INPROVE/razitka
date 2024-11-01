package cz.incad.razitka;


import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import cz.incad.razitka.server.Structure;
import org.aplikator.server.data.ContainerNode;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.PersisterTriggers;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.*;
import org.aplikator.server.query.QueryExpression;
import org.aplikator.server.query.QueryParameterReference;

import java.util.List;

import static org.aplikator.server.descriptor.Panel.column;
import static org.aplikator.server.descriptor.Panel.row;
import static org.aplikator.utils.AplikatorUtils.unaccent;

public class Exemplar extends Entity {
    public Property<String> signatura;
    public Property<String> sys;
    public Property<String> napis;
    public BinaryProperty obrazek;
    public BinaryProperty obrazek2;
    public Property<String> druh;
    public Property<String> prijmeni;
    public Property<String> instituce;
    public Property<String> obecne;
    public Property<String> mesto;
    public Property<String> vlastnik;
    public Property<String> jazyk;
    public Property<String> label;
    public Property<String> velikost;
    public Property<Boolean> hidden;

    public Property<String> napis_ascii;
    public Property<String> prijmeni_ascii;
    public Property<String> instituce_ascii;
    public Property<String> mesto_ascii;

    public Collection<Kniha> kniha;
    public Collection<Zdroj> zdroj;

    Function souvisejici;

    public Exemplar() {
        super("Exemplar", "Exemplar", "Exemplar_ID");
        initFields();
    }

    public void initFields() {
        signatura = textProperty("signatura");
        sys = textProperty("sys");
        napis = stringProperty("napis");
        obrazek = binaryProperty("obrazek");
        obrazek2 = binaryProperty("obrazek2");
        druh = stringProperty("druh").setListProvider(Structure.DLists.listDruh());
        prijmeni = stringProperty("prijmeni");
        instituce = stringProperty("instituce");
        obecne = stringProperty("obecne");
        mesto = stringProperty("mesto");
        vlastnik = stringProperty("vlastnik").setListProvider(Structure.DLists.listVlastnik());
        jazyk = stringProperty("jazyk").setListProvider(Structure.DLists.listJazyk());
        label = stringProperty("label");
        velikost = stringProperty("velikost");
        hidden = booleanProperty("hidden");

        napis_ascii = stringProperty("napis_ascii");
        prijmeni_ascii = stringProperty("prijmeni_ascii");
        instituce_ascii = stringProperty("instituce_ascii");
        mesto_ascii = stringProperty("mesto_ascii");

        setPersistersTriggers(new ExemplarTriggers());

        souvisejici = new Function("Souvisejici", "Souvisejici", new Souvisejici());
        //souvisejici.setAccessControl(AccessControl.Default.authenticated(Access.NONE).role("admin", Access.READ_WRITE_CREATE_DELETE));

    }

    private Function exportCSV = new ExportCSV();

    @Override
    protected View initDefaultView() {
        View retval = new View(this).setListPanelWidth(2).setPageSize(20);
        addCommonProperties(retval);
        //retval.addProperty(napis).addProperty(sys).addProperty(signatura).addProperty(druh).addProperty(prijmeni).addProperty(instituce).addProperty(obecne).addProperty(mesto).addProperty(vlastnik).addProperty(jazyk).addProperty(label).addProperty(hidden);

        retval.addFunction(exportCSV);
        //retval.addFunction(new Function("ImportRazitek2", "ImportRazitek2", new ImportRazitek()));

        Form form = new Form(false);
        form.setLayout(column(
                row(napis),
                row(
                        column(new BinaryField(obrazek).setHeight(400).useThumbnail(false)).setSize(6),
                        column(new BinaryField(obrazek2).setHeight(400).useThumbnail(false)).setSize(6)
                ),
                row(druh, prijmeni, instituce),
                row(mesto, vlastnik, jazyk),
                row(obecne, velikost, hidden),
                row(RepeatedForm.repeated(zdroj)),
                row(NestedTable.nestedTable(kniha))
        ));
        retval.setForm(form);
        retval.addFunction(souvisejici);
        return retval;
    }

    private View adminView;

    public View adminView() {
        if (adminView == null) {
            View retval = new View(this, "admin").setListPanelWidth(2).setPageSize(20);
            addCommonProperties(retval);

            retval.addFunction(exportCSV);
            //retval.addFunction(new Function("ImportRazitek2", "ImportRazitek2", new ImportRazitek()));

            Form form = new Form(false);
            form.setLayout(column(
                    row(napis, label),
                    row(
                            column(new BinaryField(obrazek).setHeight(400).useThumbnail(false)).setSize(6),
                            column(new BinaryField(obrazek2).setHeight(400).useThumbnail(false)).setSize(6)
                    ),
                    row(druh, prijmeni, instituce),
                    row(mesto, vlastnik, jazyk),
                    row(obecne, velikost, hidden),
                    row(RepeatedForm.repeated(zdroj)),
                    row(NestedTable.nestedTable(kniha))

            ));
            retval.setForm(form);
            retval.addFunction(souvisejici);
            adminView = retval;
        }
        return adminView;
    }

    private void addCommonProperties(View retval) {
        retval.addProperty(napis, false,false,false);
        retval.addProperty(napis_ascii, true,true,false);
        retval.addQueryDescriptor("napis_contains", napis_ascii.getLocalizationKey(), napis_ascii.LIKE_UNACCENT(QueryParameterReference.param(0)), new QueryParameter(napis_ascii));
        retval.addProperty(druh).addProperty(sys, false, false, false);
        retval.addQueryDescriptor("sys_contains", sys.getLocalizationKey(), sys.LIKE_IGNORECASE(QueryParameterReference.param(0)), new QueryParameter(sys));
        retval.addProperty(signatura, false, false, false);
        retval.addQueryDescriptor("signatura_contains", signatura.getLocalizationKey(), signatura.LIKE_IGNORECASE(QueryParameterReference.param(0)), new QueryParameter(signatura));
        retval.addProperty(prijmeni, false,false,false);
        retval.addProperty(prijmeni_ascii,true,true,false);
        retval.addQueryDescriptor("prijmeni_contains", prijmeni_ascii.getLocalizationKey(), prijmeni_ascii.LIKE_UNACCENT(QueryParameterReference.param(0)), new QueryParameter(prijmeni_ascii));
        retval.addProperty(instituce, false,false,false);
        retval.addProperty(instituce_ascii,true,true,false);
        retval.addQueryDescriptor("instituce", instituce_ascii.getLocalizationKey(), instituce_ascii.LIKE_UNACCENT(QueryParameterReference.param(0)), new QueryParameter(instituce_ascii));
        retval.addProperty(obecne, false, false, false);
        retval.addProperty(mesto, false,false,false);
        retval.addProperty(mesto_ascii, true,true,false);
        retval.addQueryDescriptor("mesto_contains", mesto_ascii.getLocalizationKey(), mesto_ascii.LIKE_UNACCENT(QueryParameterReference.param(0)), new QueryParameter(mesto_ascii));
        retval.addProperty(vlastnik).addProperty(jazyk).addProperty(label, false, false, false).addProperty(hidden, false, false, false);
    }


    private static class GuestView extends View {
        public GuestView(Entity entity, String id) {
            super(entity, id);
        }

        @Override
        public QueryDescriptor getQueryDescriptor(String id, Context ctx) {
            QueryDescriptor qd = super.getQueryDescriptor(id, ctx);
            QueryExpression existingExpression = qd.getQueryExpression(null, ctx);
            QueryExpression addedQueryExpression = Structure.Exemplar.hidden.EQUAL(false);
            if (existingExpression != null) {
                qd.setQueryExpression(existingExpression.AND(addedQueryExpression));
            } else {
                qd.setQueryExpression(addedQueryExpression);
            }
            return qd;
        }
    }

    private View guestView;

    public View guestView() {
        if (guestView == null) {
            View retval = new GuestView(this, "guest").setListPanelWidth(2).setPageSize(20);
            addCommonProperties(retval);
            //retval.addProperty(napis).addProperty(sys).addProperty(signatura).addProperty(druh).addProperty(prijmeni).addProperty(instituce).addProperty(obecne).addProperty(mesto).addProperty(vlastnik).addProperty(jazyk).addProperty(label, false, false, false);

            Form form = new Form(false);
            form.setLayout(column(
                    row(napis.widget().setEnabled(false)),
                    row(
                            column(new BinaryField(obrazek).setHeight(400).useThumbnail(false)).setSize(6).setEnabled(false),
                            column(new BinaryField(obrazek2).setHeight(400).useThumbnail(false)).setSize(6).setEnabled(false)
                    ),
                    row(RepeatedForm.repeated(zdroj).setEnabled(false)),
                    row(NestedTable.nestedTable(kniha).setEnabled(false))
            ));
            retval.setForm(form);
            guestView = retval;
        }
        return guestView;
    }

    private class ExemplarTriggers extends PersisterTriggers.Default {
        @Override
        public void onPrepare(ContainerNode node, boolean isCopy, Context ctx) {
            super.onPrepare(node, isCopy, ctx);
            node.getEdited().setValue(hidden, false);
        }

        @Override
        public void onCreate(ContainerNode node, Context ctx) {
            super.onCreate(node, ctx);
            onCreateUpdate(node, ctx);
        }

        @Override
        public void onUpdate(ContainerNode node, Context ctx) {
            super.onUpdate(node, ctx);
            onCreateUpdate(node, ctx);

            //record.getStringValue(sys, ctx).split("\n")[0]
        }

        private void onCreateUpdate(ContainerNode node, Context ctx) {
            List<Record> knihy = node.getMerged().getCollectionRecords(kniha, ctx);
            if (!knihy.isEmpty()) {
                collectValuesKnihy(ctx, node.getEdited(), knihy);
            }
            processAccents(node.getEdited(), ctx);
        }


        @Override
        public void onLoad(Record record, View view, Context ctx) {
            record.setPreview(new SafeHtmlBuilder()
                    .appendHtmlConstant("<B>").appendEscaped(record.getStringValue(druh, ctx))
                    .appendHtmlConstant("</B><br> ").appendEscaped(record.getStringValue(napis, ctx))
                    .toSafeHtml());
        }

    }

    public void collectValuesKnihy(Context ctx, Record edited, List<Record> knihy) {
        edited.setValue(label, knihy.get(0).getStringValue(Structure.Kniha.sys, ctx).split("\n")[0]);
        String signaturaS = "";
        String sysS = "";
        for (Record kniha : knihy) {
            signaturaS += " " + kniha.getStringValue(Structure.Kniha.signatura, ctx);
            sysS += " " + kniha.getStringValue(Structure.Kniha.sys, ctx);
        }
        edited.setValue(signatura, signaturaS);
        edited.setValue(sys, sysS);
    }


    public void processAccents(Record exemplar, Context ctx) {
        removeDiacriticsIfNotNull(exemplar, napis, napis_ascii);
        removeDiacriticsIfNotNull(exemplar, prijmeni, prijmeni_ascii);
        removeDiacriticsIfNotNull(exemplar, instituce, instituce_ascii);
        removeDiacriticsIfNotNull(exemplar, mesto, mesto_ascii);
        }

    private void removeDiacriticsIfNotNull(Record exemplar, Property<String> field, Property<String> fieldAscii) {
        if (exemplar.getValue(field) != null) {
            exemplar.setValue(fieldAscii, unaccent(exemplar.getValue(field)));
        }
    }



}
