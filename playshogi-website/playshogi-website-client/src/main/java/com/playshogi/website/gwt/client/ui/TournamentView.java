package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.TournamentDetailsEvent;
import com.playshogi.website.gwt.client.events.tutorial.LessonsListEvent;
import com.playshogi.website.gwt.client.place.ViewKifuPlace;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.board.BoardPreview;
import com.playshogi.website.gwt.shared.models.LessonDetails;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.Node;
import jsinterop.base.Js;
import org.dominokit.domino.ui.breadcrumbs.Breadcrumb;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.cards.Card;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.grid.Row_12;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.labels.Label;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.tree.Tree;
import org.dominokit.domino.ui.tree.TreeItem;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class TournamentView extends Composite {

    interface MyEventBinder extends EventBinder<TournamentView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);


    private final Tree<LessonDetails> lessonsTree;
    private final Card previewCard;
    private final HtmlContentBuilder<HTMLElement> previewDescription;
    private final Node previewDiagram;
    private final Button openButton;
    private final BoardPreview boardPreview;

    private final PlaceController placeController;
    private final SessionInformation sessionInformation;

    @Inject
    public TournamentView(final PlaceController placeController, final SessionInformation sessionInformation) {
        this.placeController = placeController;
        this.sessionInformation = sessionInformation;
        GWT.log("Creating tournament view");

        lessonsTree = Tree.create("Tournament Menu", null);

        FlowPanel flowPanel = new FlowPanel();

        previewDescription = Elements.span();
        boardPreview = new BoardPreview(SfenConverter.fromSFEN(SfenConverter.INITIAL_POSITION_SFEN), false,
                sessionInformation.getUserPreferences());
        previewDiagram = Js.uncheckedCast(boardPreview.getElement());
        openButton = Button.createPrimary("Open Tournament!");
        previewCard =
                Card.create("Select a round on the left")
                        .appendChild(previewDescription)
                        .appendChild(Elements.p())
                        .appendChild(previewDiagram)
                        .appendChild(Elements.p())
                        .appendChild(openButton);

        Row_12 row_12 = Row.create()
                .addColumn(Column.span3().appendChild(lessonsTree))
                .addColumn(Column.span9().appendChild(previewCard));

        flowPanel.add(new ElementWidget(row_12.element()));
        initWidget(flowPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating tournament view");
        eventBinder.bindEventHandlers(this, eventBus);
    }

    @EventHandler
    public void onTournamentDetails(final TournamentDetailsEvent event) {
        GWT.log("TournamentView: handle TournamentDetailsEvent");

        previewDescription.textContent(event.getDetails().getDescription());


    }

}
