package com.playshogi.website.gwt.client.tables;

import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.Node;
import jsinterop.base.Js;
import org.dominokit.domino.ui.datatable.DataTable;
import org.dominokit.domino.ui.datatable.events.SearchClearedEvent;
import org.dominokit.domino.ui.datatable.events.TableEvent;
import org.dominokit.domino.ui.datatable.model.Category;
import org.dominokit.domino.ui.datatable.model.Filter;
import org.dominokit.domino.ui.datatable.plugins.HeaderActionElement;
import org.dominokit.domino.ui.forms.TextBox;
import org.dominokit.domino.ui.icons.Icon;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Styles;
import org.dominokit.domino.ui.utils.ElementUtil;
import org.gwtproject.timer.client.Timer;
import org.jboss.elemento.EventType;

import static org.jboss.elemento.Elements.div;

public class CustomSearchTableAction<T> implements HeaderActionElement<T> {

    private final static String searchToolTip = "Search";
    private final static String clearSearchToolTip = "Clear search";

    private final static int autoSearchDelay = 200;
    private final HTMLDivElement element = div().css("search-new").element();
    private DataTable<T> dataTable;
    private final TextBox textBox;
    private Timer autoSearchTimer;

    public CustomSearchTableAction() {

        Icon searchIcon = Icons.ALL.search()
                .addClickListener(
                        evt -> {
                            autoSearchTimer.cancel();
                            doSearch();
                        })
                .setTooltip(searchToolTip).style().setCursor("pointer").get();

        Icon clearIcon = Icons.ALL.clear().setTooltip(clearSearchToolTip).style().setCursor("pointer").get();

        textBox =
                TextBox.create()
                        .setPlaceholder(searchToolTip).addLeftAddOn(searchIcon).addRightAddOn(clearIcon)
                        .styler(style -> style.add("table-search-box")
                                .setMarginBottom("0px")
                                .setMaxWidth("300px")
                                .add(Styles.pull_right));

        clearIcon.addClickListener(evt -> {
            textBox.clear();
            autoSearchTimer.cancel();
            doSearch();
        });

        element.appendChild(textBox.element());

        autoSearchTimer = new Timer() {
            @Override
            public void run() {
                doSearch();
            }
        };

        EventListener autoSearchEventListener = evt -> {
            autoSearchTimer.cancel();
            autoSearchTimer.schedule(autoSearchDelay);
        };

        textBox.addEventListener("input", autoSearchEventListener);

        textBox.addEventListener(EventType.keypress.getName(),
                evt -> {
                    if (ElementUtil.isEnterKey(Js.uncheckedCast(evt))) {
                        doSearch();
                    }
                });

    }

    void doSearch() {
        Category search = Category.SEARCH;
        dataTable.getSearchContext().removeByCategory(search);
        dataTable.getSearchContext().add(Filter.create("*", textBox.getValue(), Category.SEARCH)).fireSearchEvent();
    }

    @Override
    public void handleEvent(TableEvent event) {
        if (SearchClearedEvent.SEARCH_EVENT_CLEARED.equals(event.getType())) {
            textBox.pauseChangeHandlers();
            textBox.clear();
            textBox.resumeChangeHandlers();
        }
    }

    @Override
    public Node asElement(DataTable<T> dataTable) {
        this.dataTable = dataTable;
        dataTable.addTableEventListener(SearchClearedEvent.SEARCH_EVENT_CLEARED, this);
        return element;
    }

    void setSearch(final String search) {
        textBox.pauseChangeHandlers();
        textBox.setValue(search);
        textBox.resumeChangeHandlers();
        doSearch();
    }
}