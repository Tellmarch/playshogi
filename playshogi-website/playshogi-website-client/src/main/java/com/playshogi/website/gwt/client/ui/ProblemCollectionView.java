package com.playshogi.website.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.website.gwt.client.SessionInformation;
import com.playshogi.website.gwt.client.events.collections.ListCollectionProblemsEvent;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.KifuEditorPlace;
import com.playshogi.website.gwt.client.place.ProblemsPlace;
import com.playshogi.website.gwt.client.tables.ProblemTable;
import com.playshogi.website.gwt.client.util.ElementWidget;
import com.playshogi.website.gwt.client.widget.collections.UploadKifusPopup;
import com.playshogi.website.gwt.client.widget.kifu.ImportKifuPanel;
import com.playshogi.website.gwt.client.widget.problems.TagsElement;
import com.playshogi.website.gwt.shared.models.KifuDetails;
import com.playshogi.website.gwt.shared.models.ProblemCollectionDetails;
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
public class ProblemCollectionView extends Composite {

    private final TagsElement tagsElement;

    interface MyEventBinder extends EventBinder<ProblemCollectionView> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final HtmlContentBuilder<HTMLHeadingElement> collectionHeading;
    private final HtmlContentBuilder<HTMLHeadingElement> collectionDescription;
    private final ProblemTable problemTable;
    private final ImportKifuPanel importKifuPanel = new ImportKifuPanel();
    private final UploadKifusPopup uploadKifusPopup = new UploadKifusPopup(false, false, false, false, true);
    private final SessionInformation sessionInformation;
    private final AppPlaceHistoryMapper historyMapper;
    private final Button newKifuButton;
    private final Button addKifuButton;
    private final Button uploadKifuButton;
    private final HtmlContentBuilder<HTMLAnchorElement> exploreLink;

    private EventBus eventBus;
    private ProblemCollectionDetails collectionDetails;

    @Inject
    public ProblemCollectionView(final SessionInformation sessionInformation, final AppPlaceHistoryMapper historyMapper,
                                 final PlaceController placeController) {
        this.sessionInformation = sessionInformation;
        this.historyMapper = historyMapper;
        problemTable = new ProblemTable(historyMapper, sessionInformation.getUserPreferences(), false);

        HtmlContentBuilder<HTMLDivElement> root = Elements.div();
        root.css(Styles.padding_20);

        tagsElement = new TagsElement(new String[0]);
        collectionHeading = Elements.h(2).textContent("");
        collectionDescription = Elements.h(4).textContent("");
        root.add(tagsElement.asElement());
        root.add(collectionHeading);
        root.add(collectionDescription);

        newKifuButton = Button.createPrimary(Icons.ALL.add_circle()).setContent("New Problem");
        root.add(newKifuButton
                .addClickListener(evt -> placeController.goTo(new KifuEditorPlace(null, KifuDetails.KifuType.PROBLEM,
                        collectionDetails.getId())))
                .style().setMarginRight("3em"));

        addKifuButton = Button.createPrimary(Icons.ALL.content_paste()).setContent("Add Problem from Clipboard");
        root.add(addKifuButton
                .addClickListener(evt -> importKifuPanel.showInDialog(collectionDetails.getId()))
                .style().setMarginRight("3em"));

        uploadKifuButton = Button.createPrimary(Icons.ALL.file_upload()).setContent("Upload Problem(s)");
        root.add(uploadKifuButton
                .addClickListener(evt -> {
                    uploadKifusPopup.setSelectedProblemCollection(collectionDetails);
                    uploadKifusPopup.show();
                })
                .style().setMarginRight("3em"));

        exploreLink = Elements.a("#");
        root.add(exploreLink.add(Button.createSuccess(Icons.ALL.timer()).setContent("Practice / Speedrun")));

        root.add(problemTable.getTable());

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(new ElementWidget(root.element()));
        scrollPanel.setSize("100%", "100%");
        initWidget(scrollPanel);
    }

    public void activate(final EventBus eventBus) {
        GWT.log("Activating CollectionView");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, eventBus);
        problemTable.activate(eventBus);
        importKifuPanel.activate(eventBus);
        uploadKifusPopup.activate(eventBus);
    }

    @EventHandler
    public void onCollectionList(final ListCollectionProblemsEvent event) {
        GWT.log("CollectionView: handle ListCollectionProblemsEvent");
        collectionDetails = event.getCollectionDetails();
        boolean isAuthor = sessionInformation.getUsername().equals(collectionDetails.getAuthor());
        if (event.getDetails() != null) {
            problemTable.setData(Arrays.asList(event.getDetails()), isAuthor);
        }
        tagsElement.setTags(collectionDetails.getTags());
        collectionHeading.textContent(collectionDetails.getName());
        collectionDescription.textContent(collectionDetails.getDescription());

        String exploreHRef = "#" + historyMapper.getToken(new ProblemsPlace(collectionDetails.getId(), 0));
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
