package cz.incad.razitka;


import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.aplikator.server.Configurator;
import org.aplikator.server.data.ContainerNode;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.PersisterTriggers;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.*;

import static org.aplikator.server.descriptor.Panel.row;

public class Zdroj extends Entity {
    public Property<String> zdroj;
    public Property<String> link;

    public Zdroj() {
        super("Zdroj", "Zdroj", "Zdroj_ID");
        initFields();
    }

    public void initFields() {
        zdroj = stringProperty("zdroj");
        link = virtualStringProperty("link");
        setPersistersTriggers(new KnihaTriggers());
    }

    @Override
    protected View initDefaultView() {
        View retval = new View(this).setListPanelWidth(2).setPageSize(20);
        retval.addProperty(zdroj);

        Form form = new Form(false);
        form.setLayout(
                row(zdroj.widget().setSize(8), new HtmlField(link))
                );
        retval.setForm(form);
        return retval;
    }


    private class KnihaTriggers extends PersisterTriggers.Default {
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
        public void onLoad(Record record, View view, Context ctx) {
            super.onLoad(record, view, ctx);
            if (record.getStringValue(zdroj, ctx).toLowerCase().startsWith("http")) {
                record.setValue(link, new SafeHtmlBuilder().appendHtmlConstant("<a href=\"").appendEscaped(record.getStringValue(zdroj, ctx))
                        .appendHtmlConstant("\"   target=\"_blank\">").appendEscaped(Configurator.get().getLocalizedString("zdroj.text", ctx.getUserLocale())).appendHtmlConstant("</a>")
                        .toSafeHtml().asString());
            }
        }
    }

}
