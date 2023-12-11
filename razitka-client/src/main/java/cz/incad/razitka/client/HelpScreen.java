package cz.incad.razitka.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.aplikator.client.local.Aplikator;
import org.aplikator.client.shared.descriptor.ViewDTO;
import org.gwtbootstrap3.client.ui.Container;
import org.jboss.errai.ui.nav.client.local.Page;

@Page( path = "help")
public class HelpScreen extends Composite {


    public HelpScreen() {
        initWidget(createTable());
    }

    protected ViewDTO view;

    final String url = "https://docs.google.com/document/d/1yV5Pw6nnywdI3SFsdqUaTwkluFOKmZDGniCJ4t50Tek/edit";

     final int SCROLL_OFFSET = Aplikator.application.isShowNavigation() ? Aplikator.MAINMENUBAR_HEIGHT  : 0;
     final int scrollHeight =  Window.getClientHeight() - SCROLL_OFFSET - 20;
     final int scrollWidth = Window.getClientWidth()-30;

    private  HTML label = new HTML( "<iframe src=\""+url+"\" width=\""+scrollWidth+"\" height=\""+scrollHeight+"\"></iframe>");


    private void drawPanel(AcceptsOneWidget panel) {
        if (widget == null) {
            widget = createTable();
        }
        panel.setWidget(widget);


    }


    private Widget widget = null;

    private Widget createTable() {
        //label.addStyleName(ColumnSize.XS_12.getCssName());




        Container container = new Container();
        container.add(label);
        container.setFluid(true);
        return container.asWidget();

    }


}
