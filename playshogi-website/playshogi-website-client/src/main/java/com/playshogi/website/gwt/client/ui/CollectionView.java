package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListCollectionGamesEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.KifuEditorPlace;
import com.playshogi.website.gwt.client.place.OpeningsPlace;
import com.playshogi.website.gwt.client.tables.GameTable;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.collections.UploadKifusPopup;
import com.playshogi.website.gwt.client.widget.kifu.ImportKifuPanel;
import com.playshogi.website.gwt.shared.models.GameCollectionDetails;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.style.Styles;
import org.jboss.elemento.Elements;
import org.jboss.elemento.HtmlContentBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class CollectionView extends Composite {

    interface MyEventBinder extends EventBinder<CollectionView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final HtmlContentBuilder<HTMLHeadingElement> collectionHeading;
    private final HtmlContentBuilder<HTMLHeadingElement> collectionDescription;
    private final GameTable gameTable;
    private final ImportKifuPanel importKifuPanel = new ImportKifuPanel();
    private final UploadKifusPopup uploadKifusPopup = new UploadKifusPopup(false, false, true, false, false);
    private final SessionInformation sessionInformation;
    private final AppPlaceHistoryMapper historyMapper;
    private final HtmlContentBuilder<HTMLAnchorElement> exploreLink;
    private final Button newKifuButton;
    private final Button addKifuButton;
    private final Button uploadKifuButton;

    private EventBus eventBus;
    private GameCollectionDetails collectionDetails;

    @Inject
    public CollectionView(final SessionInformation sessionInformation, final AppPlaceHistoryMapper historyMapper,
                          final PlaceController placeController) {
        this.sessionInformation = sessionInformation;
        this.historyMapper = historyMapper;
        gameTable = new GameTable(historyMapper, sessionInformation.getUserPreferences(), false);

        HtmlContentBuilder<HTMLDivElement> root = Elements.div();
        root.css(Styles.padding_20);
        collectionHeading = Elements.h(2).textContent("");
        collectionDescription = Elements.h(4).textContent("");
        root.add(collectionHeading);
        root.add(collectionDescription);

        newKifuButton = Button.createPrimary(Icons.ALL.add_circle()).setContent("New Kifu");
        root.add(newKifuButton
                .addClickListener(evt -> placeController.goTo(new KifuEditorPlace(null, KifuDetails.KifuType.GAME,
                        collectionDetails.getId())))
                .style().setMarginRight("3em"));

        addKifuButton = Button.createPrimary(Icons.ALL.content_paste()).setContent("Add Kifu from Clipboard");
        root.add(addKifuButton
                .addClickListener(evt -> importKifuPanel.showInDialog(collectionDetails.getId()))
                .style().setMarginRight("3em"));

        uploadKifuButton = Button.createPrimary(Icons.ALL.file_upload()).setContent("Upload Kifu(s)");
        root.add(uploadKifuButton
                .addClickListener(evt -> {
                    uploadKifusPopup.setSelectedGameCollection(collectionDetails);
                    uploadKifusPopup.show();
                })
                .style().setMarginRight("3em"));

        exploreLink = Elements.a("#");
        root.add(exploreLink.add(Button.createPrimary(Icons.ALL.pie_chart()).setContent("Explore Openings")));

        root.add(gameTable.getTable());

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(new ElementWidget(root.element()));
        scrollPanel.setSize("100%", "100%");
        initWidget(scrollPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating CollectionView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        gameTable.activate(eventBus);
        importKifuPanel.activate(eventBus);
        uploadKifusPopup.activate(eventBus);
    }

    @EventHandler
    public void onCollectionList(final ListCollectionGamesEvent event) {
        GWT.log("CollectionView: handle ListCollectionGamesEvent");
        collectionDetails = event.getCollectionDetails();
        boolean isAuthor = sessionInformation.getUsername().equals(collectionDetails.getAuthor());
        //TODO add loading screen
        if (event.getDetails() != null) {
            gameTable.setData(Arrays.asList(event.getDetails()), isAuthor);
        }
        collectionHeading.textContent(collectionDetails.getName());
        collectionDescription.textContent(collectionDetails.getDescription());
        String exploreHRef = "#" + historyMapper.getToken(new OpeningsPlace(OpeningsPlace.DEFAULT_SFEN,
                collectionDetails.getId()));
        exploreLink.attr("href", exploreHRef);

        if (isAuthor) {
            newKifuButton.show();
            addKifuButton.show();
            uploadKifuButton.show();
        } else {
            newKifuButton.hide();
            addKifuButton.hide();
            uploadKifuButton.hide();
        }
    }

}
