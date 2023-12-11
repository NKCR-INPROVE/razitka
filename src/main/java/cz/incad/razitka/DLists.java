package cz.incad.razitka;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import cz.incad.razitka.server.Structure;
import org.aplikator.server.ListRegistry;
import org.aplikator.server.data.ContainerNode;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.PersisterTriggers;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.*;

import static org.aplikator.server.descriptor.Panel.column;
import static org.aplikator.server.descriptor.Panel.row;

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
    private View viewDruh;
    private View viewVlastnik;
    private View viewJazyk;

    public DLists() {
        super("DLists", "DLists", "DLists_ID");
        initFields();
        this.setPersistersTriggers(new DlistTriggers());
    }

    static public EntityListProvider listDruh() {
        EntityListProvider listProvider = (EntityListProvider) ListRegistry.get().getListProvider(DListsType.druh.name());
        if (listProvider == null) {
            listProvider = new EntityListProvider(DListsType.druh, Structure.DLists.druh(), Structure.DLists.value);
            listProvider.addLanguageProperty("cs", Structure.DLists.cz);
            listProvider.addLanguageProperty("en", Structure.DLists.en);
        }
        return listProvider;
    }


    static public EntityListProvider listJazyk() {
        EntityListProvider listProvider = (EntityListProvider) ListRegistry.get().getListProvider(DListsType.jazyk.name());
        if (listProvider == null) {
            listProvider = new EntityListProvider(DListsType.jazyk, Structure.DLists.jazyk(), Structure.DLists.value);
            listProvider.addLanguageProperty("cs", Structure.DLists.cz);
            listProvider.addLanguageProperty("en", Structure.DLists.en);
        }
        return listProvider;
    }

    static public EntityListProvider listVlastnik() {
        EntityListProvider listProvider = (EntityListProvider) ListRegistry.get().getListProvider(DListsType.vlastnik.name());
        if (listProvider == null) {
            listProvider = new EntityListProvider(DListsType.vlastnik, Structure.DLists.vlastnik(), Structure.DLists.value);
            listProvider.addLanguageProperty("cs", Structure.DLists.cz);
            listProvider.addLanguageProperty("en", Structure.DLists.en);
            listProvider.sortByName();
        }
        return listProvider;
    }

    @Override
    protected View initDefaultView() {
        View retval = new View(this);
        retval.addProperty(value).addProperty(cz).addProperty(en).addProperty(de).addProperty(fr).addProperty(use, true, true, false);
        retval.insertFirstSortDescriptor(poradi.getId(), poradi.getLocalizationKey(), SortItem.ascending(poradi));
        retval.insertQueryDescriptor("useQ","activeListItems", use.EQUAL(true) );
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

    public View druh() {
        if (viewDruh == null) {
            viewDruh = inheritanceView(this.view(), classType, DListsType.druh.name());
        }
        return viewDruh;
    }

    public View vlastnik() {
        if (viewVlastnik == null) {
            viewVlastnik = inheritanceView(this.view(), classType, DListsType.vlastnik.name());
        }
        return viewVlastnik;
    }

    public View jazyk() {
        if (viewJazyk == null) {
            viewJazyk = inheritanceView(this.view(), classType, DListsType.jazyk.name());
        }
        return viewJazyk;
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

    public enum DListsType {
        druh, vlastnik, jazyk
    }

    class DlistTriggers extends PersisterTriggers.Default {
        @Override
        public void afterCommit(ContainerNode node, Context ctx) {
            super.afterCommit(node, ctx);
            listDruh().refreshListValues(ctx);
            listVlastnik().refreshListValues(ctx);
            listJazyk().refreshListValues(ctx);
        }

        @Override
        public void onLoad(Record record, View view, Context ctx) {
            record.setPreview(new SafeHtmlBuilder()
                    .appendEscaped(record.getValue(cz)).appendHtmlConstant(" - ").appendEscaped(record.getValue(value))
                    .toSafeHtml());
        }
    }
}
