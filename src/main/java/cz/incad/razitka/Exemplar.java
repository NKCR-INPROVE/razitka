package cz.incad.razitka;


import static org.aplikator.server.descriptor.Panel.column;
import static org.aplikator.server.descriptor.Panel.row;

import org.aplikator.client.shared.data.ContainerNode;
import org.aplikator.client.shared.data.Record;
import org.aplikator.client.shared.descriptor.QueryParameter;
import org.aplikator.server.Context;
import org.aplikator.server.data.BinaryData;
import org.aplikator.server.descriptor.BinaryField;
import org.aplikator.server.descriptor.Entity;
import org.aplikator.server.descriptor.Form;
import org.aplikator.server.descriptor.Property;
import org.aplikator.server.descriptor.SortItem;
import org.aplikator.server.descriptor.TextArea;
import org.aplikator.server.descriptor.View;
import org.aplikator.server.persistence.PersisterTriggers;
import org.aplikator.server.query.QueryParameterReference;

import cz.incad.razitka.server.Structure;

public class Exemplar extends Entity {
    public Property<String> signatura;
    public Property<String> sys;
    public Property<String> napis;
    public Property<BinaryData> obrazek;
    public Property<String> druh;
    public Property<String> prijmeni;
    public Property<String> instituce;
    public Property<String> obecne;
    public Property<String> mesto;

    public Exemplar() {
        super("Exemplar", "Exemplar", "Exemplar_ID");
        initFields();
    }
    
    public void initFields() {
        signatura = textProperty("signatura");
        sys = textProperty("sys");
        napis = stringProperty("napis");
        obrazek = binaryProperty("obrazek");
        druh = stringProperty("druh").setListProvider(cz.incad.razitka.Utils.namedList(Structure.DLists, DLists.DListsType.druh));;
        prijmeni = stringProperty("prijmeni");
        instituce = stringProperty("instituce");
        obecne = stringProperty("obecne");
        mesto = stringProperty("mesto");
        setPersistersTriggers(new ExemplarTriggers());
    }
    
    @Override
    protected View initDefaultView() {
        View retval = new View(this).setListPanelWidth(2).setPageSize(20);
        retval.addProperty(sys).addProperty(signatura).addProperty(napis).addProperty(druh).addProperty(prijmeni).addProperty(instituce).addProperty(obecne).addProperty(mesto);
        retval.addSortDescriptor("id_asc", "Exemplar.id", SortItem.ascending(this.getPrimaryKey()));
        retval.addSortDescriptor("id_desc", "Exemplar.id", SortItem.descending(this.getPrimaryKey()));

        retval.addQueryDescriptor("vse", "searchAll", null);
        retval.addQueryDescriptor("vybersignatury", "searchSignatura", signatura.LIKE(QueryParameterReference.param(0)), new QueryParameter("Exemplar.signatura"));
        retval.addQueryDescriptor("vybersys", "searchSys", sys.LIKE(QueryParameterReference.param(0)), new QueryParameter("Exemplar.sys"));
        retval.addQueryDescriptor("vybernazev", "searchNazev", napis.LIKE(QueryParameterReference.param(0)), new QueryParameter("Exemplar.napis"));
        retval.addQueryDescriptor("vyberprijmeni", "searchPrijmeni", prijmeni.LIKE(QueryParameterReference.param(0)), new QueryParameter("Exemplar.prijmeni"));
        retval.addQueryDescriptor("vyberinstituce", "searchInstituce", instituce.LIKE(QueryParameterReference.param(0)), new QueryParameter("Exemplar.instituce"));
        retval.addQueryDescriptor("vyberobecne", "searchObecne", obecne.LIKE(QueryParameterReference.param(0)), new QueryParameter("Exemplar.obecne"));
        retval.addQueryDescriptor("vybermesto", "searchMesto", mesto.LIKE(QueryParameterReference.param(0)), new QueryParameter("Exemplar.mesto"));


        Form form = new Form(false);
        form.setLayout(column(
                row(napis),
                row(
                        column(new BinaryField(obrazek).setHeight(400).useThumbnail(false)).setSize(8),
                        column(row( new TextArea(signatura).setRows(21).setSize(6),
                                new TextArea(sys).setRows(21).setSize(6)
                                )).setSize(4)
                ),
                row(druh, prijmeni, instituce),
                row(obecne, mesto)
                ));
        retval.setForm(form);
        return retval;
    }



    private class ExemplarTriggers extends PersisterTriggers.Default {
        @Override
        public void onPrepare(ContainerNode node, boolean isCopy, Context ctx) {
            super.onPrepare(node, isCopy, ctx);
        }

        @Override
        public void onCreate(ContainerNode node, Context ctx) {
            super.onCreate(node, ctx);
        }

        @Override
        public void onUpdate(ContainerNode node, Context ctx) {
            super.onUpdate(node, ctx);
        }
        
        @Override
        public void onLoad(Record record, Context ctx) {
            record.setPreview("<B>"+ sys.getStringValue(record).split("\n")[0]+"</B><br> "+ napis.getStringValue(record));
        }
    }
    
}
