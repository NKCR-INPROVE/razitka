package cz.incad.razitka;


import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import cz.incad.razitka.server.Structure;
import org.aplikator.server.data.ContainerNode;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.PersisterTriggers;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.*;
import org.aplikator.server.query.QueryExpression;

import java.util.List;

import static org.aplikator.server.descriptor.Panel.column;
import static org.aplikator.server.descriptor.Panel.row;

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
    public Property<Boolean> hidden;
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
        hidden = booleanProperty("hidden");
        setPersistersTriggers(new ExemplarTriggers());

        souvisejici = new Function("Souvisejici", "Souvisejici", new Souvisejici());
        //souvisejici.setAccessControl(AccessControl.Default.authenticated(Access.NONE).role("admin", Access.READ_WRITE_CREATE_DELETE));

    }

    private Function exportCSV = new ExportCSV();

    @Override
    protected View initDefaultView() {
        View retval = new View(this).setListPanelWidth(2).setPageSize(20);
        retval.addProperty(napis).addProperty(sys).addProperty(signatura).addProperty(druh).addProperty(prijmeni).addProperty(instituce).addProperty(obecne).addProperty(mesto).addProperty(vlastnik).addProperty(jazyk).addProperty(label).addProperty(hidden);

        retval.addFunction(exportCSV);
        //retval.addFunction(new Function("ImportRazitek2", "ImportRazitek2", new ImportRazitek()));

        Form form = new Form(false);
        form.setLayout(column(
                row(napis),
                row(
                        column(new BinaryField(obrazek).setHeight(400).useThumbnail(false)).setSize(6),
                        column(new BinaryField(obrazek2).setHeight(400).useThumbnail(false)).setSize(6)
                ),
                row(RepeatedForm.repeated(kniha)),
                row(druh, prijmeni, instituce, hidden),
                row(obecne, mesto, vlastnik, jazyk),
                row(RepeatedForm.repeated(zdroj))
        ));
        retval.setForm(form);
        retval.addFunction(souvisejici);
        return retval;
    }

    private View adminView;
    public View adminView() {
        if (adminView == null) {
            View retval = new View(this, "admin").setListPanelWidth(2).setPageSize(20);
            retval.addProperty(napis).addProperty(sys).addProperty(signatura).addProperty(druh).addProperty(prijmeni).addProperty(instituce).addProperty(obecne).addProperty(mesto).addProperty(vlastnik).addProperty(jazyk).addProperty(label).addProperty(hidden);

            retval.addFunction(exportCSV);
            //retval.addFunction(new Function("ImportRazitek2", "ImportRazitek2", new ImportRazitek()));

            Form form = new Form(false);
            form.setLayout(column(
                    row(napis, label),
                    row(
                            column(new BinaryField(obrazek).setHeight(400).useThumbnail(false)).setSize(6),
                            column(new BinaryField(obrazek2).setHeight(400).useThumbnail(false)).setSize(6)
                    ),
                    row(RepeatedForm.repeated(kniha)),
                    row(druh, prijmeni, instituce, hidden),
                    row(obecne, mesto, vlastnik, jazyk),
                    row(RepeatedForm.repeated(zdroj))
            ));
            retval.setForm(form);
            retval.addFunction(souvisejici);
            adminView = retval;
        }
        return adminView;
    }


    private static class GuestView extends View{
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

            retval.addProperty(napis).addProperty(sys).addProperty(signatura).addProperty(druh).addProperty(prijmeni).addProperty(instituce).addProperty(obecne).addProperty(mesto).addProperty(vlastnik).addProperty(jazyk).addProperty(label, false,false,false);

            Form form = new Form(false);
            form.setLayout(column(
                    row(napis.widget().setEnabled(false)),
                    row(
                            column(new BinaryField(obrazek).setHeight(400).useThumbnail(false)).setSize(6).setEnabled(false),
                            column(new BinaryField(obrazek2).setHeight(400).useThumbnail(false)).setSize(6).setEnabled(false)
                    ),
                    row(RepeatedForm.repeated(kniha).setEnabled(false)),
                    row(RepeatedForm.repeated(zdroj).setEnabled(false))
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
            node.getEdited().setValue(hidden,false);
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
            if (!knihy.isEmpty()){
                node.getEdited().setValue(label,knihy.get(0).getStringValue(Structure.Kniha.sys, ctx).split("\n")[0]);
            }
        }

        @Override
        public void onLoad(Record record, View view, Context ctx) {
            record.setPreview(new SafeHtmlBuilder()
                    .appendHtmlConstant("<B>").appendEscaped(record.getStringValue(label, ctx))
                    .appendHtmlConstant("</B><br> ").appendEscaped(record.getStringValue(napis, ctx))
                    .toSafeHtml());
        }
    }

}
