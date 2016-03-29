package cz.incad.razitka;


import static org.aplikator.server.descriptor.Panel.column;
import static org.aplikator.server.descriptor.Panel.row;

import org.aplikator.server.Context;
import org.aplikator.server.data.BinaryData;
import org.aplikator.server.data.ContainerNode;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.BinaryField;
import org.aplikator.server.descriptor.Entity;
import org.aplikator.server.descriptor.Form;
import org.aplikator.server.descriptor.Property;
import org.aplikator.server.descriptor.TextArea;
import org.aplikator.server.descriptor.View;
import org.aplikator.server.persistence.PersisterTriggers;

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
        retval.addProperty(napis).addProperty(sys).addProperty(signatura).addProperty(druh).addProperty(prijmeni).addProperty(instituce).addProperty(obecne).addProperty(mesto);


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
            record.setPreview("<B>"+ record.getStringValue(sys, ctx).split("\n")[0]+"</B><br> "+ record.getStringValue(napis, ctx));
        }
    }
    
}
