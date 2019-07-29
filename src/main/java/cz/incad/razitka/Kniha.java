package cz.incad.razitka;


import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import cz.incad.razitka.server.Structure;
import org.aplikator.server.Configurator;
import org.aplikator.server.data.ContainerNode;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.PersisterTriggers;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.*;

import static org.aplikator.server.descriptor.Panel.column;
import static org.aplikator.server.descriptor.Panel.row;

public class Kniha extends Entity {
    public Property<String> signatura;
    public Property<String> sys;
    public Property<String> link;

    public Kniha() {
        super("Kniha", "Kniha", "Kniha_ID");
        initFields();
    }

    public void initFields() {
        signatura = stringProperty("signatura");
        sys = stringProperty("sys");
        link = virtualStringProperty("link");
        setPersistersTriggers(new KnihaTriggers());
    }

    @Override
    protected View initDefaultView() {
        View retval = new View(this).setListPanelWidth(2).setPageSize(20);
        retval.addProperty(sys).addProperty(signatura);

        Form form = new Form(false);
        form.setLayout(
                row(signatura, sys, new HtmlField(link))
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
            record.setValue(link,new SafeHtmlBuilder()
                    .appendHtmlConstant("<a href=\"https://aleph.nkp.cz/F/?func=direct&doc_number=").appendEscaped(record.getStringValue(sys, ctx))
                    .appendHtmlConstant("&local_base=NKC\"  target=\"_blank\">").appendEscaped(Configurator.get().getLocalizedString("link.text", ctx.getUserLocale())).appendHtmlConstant("</a>")
                    .toSafeHtml().asString());
        }
    }

}
