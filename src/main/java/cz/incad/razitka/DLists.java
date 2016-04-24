package cz.incad.razitka;

import static org.aplikator.server.descriptor.Panel.column;
import static org.aplikator.server.descriptor.Panel.row;

import org.aplikator.server.ListRegistry;
import org.aplikator.server.data.ContainerNode;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.PersisterTriggers;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.Entity;
import org.aplikator.server.descriptor.EntityListProvider;
import org.aplikator.server.descriptor.Form;
import org.aplikator.server.descriptor.Property;
import org.aplikator.server.descriptor.SortItem;
import org.aplikator.server.descriptor.TextArea;
import org.aplikator.server.descriptor.View;

import cz.incad.razitka.server.Structure;

public class DLists extends Entity {
    public Property<String> classType;
    public Property<String> value;
    public Property<String> cz;
    public Property<String> en;
    public Property<String> de;
    public Property<String> fr;
    public Property<Boolean> use;
    public Property<Integer> poradi;
    public Property<String> poznamka;

    private static final String LIST_DRUH = "listDruh";

    static public EntityListProvider listDruh() {
        EntityListProvider listProvider = (EntityListProvider)ListRegistry.get().getListProvider(LIST_DRUH);
        if (listProvider==null) {
            listProvider = new EntityListProvider(LIST_DRUH, Structure.DLists.druh(), Structure.DLists.value);
            listProvider.addLanguageProperty("cs", Structure.DLists.cz);
            listProvider.addLanguageProperty("en", Structure.DLists.en);
        }
        return listProvider;
    }
    
    public DLists() {
        super("DLists","DLists", "DLists_ID");
        initFields();
        this.setPersistersTriggers(new DlistTriggers());
    }
    
    class DlistTriggers extends PersisterTriggers.Default {        
        @Override
        public void afterCommit(ContainerNode node, Context ctx) {
            super.afterCommit(node, ctx);
            listDruh().refreshListValues(ctx);
        }
        
        @Override
        public void onLoad(Record record, View view, Context ctx) {
            record.setPreview(record.getValue(cz)+" - "+record.getValue(value));
        }
    }
    
    @Override
    protected View initDefaultView() {
        View retval = new View(this);
        retval.addProperty(value).addProperty(cz).addProperty(en).addProperty(de).addProperty(fr);
        retval.insertFirstSortDescriptor(poradi.getId(), poradi.getLocalizationKey(), SortItem.ascending(poradi));
        Form form = new Form(false);
        form.setLayout(column(
                row(value, cz),
                row(en, de, fr),
                row(use, poradi),
                row(new TextArea(poznamka).setRows(3))
                ));
        retval.setForm(form);
        return retval;
    }
    
    public enum DListsType {
        druh, obecne
    }

    private View viewDruh;
    public View druh() {
        if (viewDruh == null) {
            viewDruh = inheritanceView(this.view(), classType, DListsType.druh);
        }
        return viewDruh;
    }

    public View obecne() {
        return inheritanceView(this.view(), classType, DListsType.obecne);
    }


    public void initFields() {
        classType = stringProperty("classType");
        value = stringProperty("value");
        cz = stringProperty("cz");
        en = stringProperty("en");
        de = stringProperty("de");
        fr = stringProperty("fr");
        use = booleanProperty("use");
        poradi = integerProperty("poradi");
        poznamka = stringProperty("poznamka");
    }
}
