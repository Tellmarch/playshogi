package com.playshogi.website.gwt.client.widget.kifu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.playshogi.library.shogi.models.formats.sfen.SfenConverter;
import com.playshogi.library.shogi.models.moves.EditMove;
import com.playshogi.library.shogi.models.moves.Move;
import com.playshogi.library.shogi.models.moves.ShogiMove;
import com.playshogi.library.shogi.models.record.GameNavigation;
import com.playshogi.library.shogi.models.record.GameTree;
import com.playshogi.library.shogi.models.record.Node;
import com.playshogi.website.gwt.client.UserPreferences;
import com.playshogi.website.gwt.client.events.gametree.GameTreeChangedEvent;
import com.playshogi.website.gwt.client.events.gametree.NewVariationPlayedEvent;
import com.playshogi.website.gwt.client.events.gametree.NodeChangedEvent;
import com.playshogi.website.gwt.client.events.gametree.PositionChangedEvent;
import com.playshogi.website.gwt.client.events.user.NotationStyleSelectedEvent;
import org.dominokit.domino.ui.style.Color;

import java.util.Iterator;
import java.util.List;

public class GameTreePanel extends Composite {

    interface MyEventBinder extends EventBinder<GameTreePanel> {
    }

    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private final String activityId;
    private final GameNavigation gameNavigation;
    private final boolean readOnly;
    private final UserPreferences userPreferences;
    private final boolean colorVisitedNodes;
    private final boolean colorNewNodes;
    private final Tree tree;
    private final PopupPanel contextMenu;
    private MenuItem promoteVariationMenu;
    private MenuItem deleteVariationMenu;
    private MenuItem rebaseMenu;
    private EventBus eventBus;

    public GameTreePanel(final String activityId, final GameNavigation gameNavigation, final boolean readOnly,
                         final UserPreferences userPreferences, final boolean colorVisitedNodes,
                         final boolean colorNewNodes) {
        this.activityId = activityId;
        this.gameNavigation = gameNavigation;
        this.readOnly = readOnly;
        this.userPreferences = userPreferences;
        this.colorVisitedNodes = colorVisitedNodes;
        this.colorNewNodes = colorNewNodes;
        contextMenu = createContextMenu();

        FlowPanel panel = new FlowPanel();

        tree = new Tree() {
            @Override
            public void setFocus(boolean focus) {
                // Don't do anything (https://github.com/gwtproject/gwt/issues/9475)
            }
        };
        tree.setScrollOnSelectEnabled(true);
        tree.addSelectionHandler(selectionEvent -> {
            TreeItem item = selectionEvent.getSelectedItem();
            Node node = null;
            TreeItem additionalItem = null;

            if (item.getUserObject() instanceof Node) {
                node = (Node) item.getUserObject();
            } else if (item.getChildCount() > 0 && item.getChild(0).getUserObject() instanceof Node) {
                additionalItem = item.getChild(0);
                node = (Node) item.getChild(0).getUserObject();
            }
            if (node != null && gameNavigation.getCurrentNode() != node) {
                gameNavigation.moveToNode(node);
                colorNode(item, node);
                if (additionalItem != null) colorNode(additionalItem, node);
                eventBus.fireEvent(new PositionChangedEvent(gameNavigation.getPosition(),
                        gameNavigation.getBoardDecorations(), gameNavigation.getPreviousMove(), true));
            }
        });
        panel.add(tree);

        initWidget(panel);
    }

    private PopupPanel createContextMenu() {
        final PopupPanel contextMenu;
        contextMenu = new PopupPanel(true);


        MenuBar menuBar = new MenuBar(true);

        deleteVariationMenu = new MenuItem("Delete Variation", () -> {
            GWT.log("Delete variation");
            Node node = (Node) tree.getSelectedItem().getUserObject();
            boolean confirm = Window.confirm("Delete variation starting with " +
                    tree.getSelectedItem().getText() + "? This can not be undone.");
            if (confirm) {
                node.removeFromParent();
                eventBus.fireEvent(new GameTreeChangedEvent(gameNavigation.getGameTree()));
            }
            contextMenu.hide();
        });
        menuBar.addItem(deleteVariationMenu);

        if (!readOnly) {
            promoteVariationMenu = new MenuItem("Promote variation to main line", () -> {
                GWT.log("Promote variation");
                Node node = (Node) tree.getSelectedItem().getUserObject();
                node.promoteVariation();
                eventBus.fireEvent(new GameTreeChangedEvent(gameNavigation.getGameTree()));
                contextMenu.hide();
            });
            menuBar.addItem(promoteVariationMenu);

            rebaseMenu = new MenuItem("Start from here", () -> {
                GWT.log("Rebase");
                Node node = (Node) tree.getSelectedItem().getUserObject();
                if (!(node.getMove() instanceof EditMove)) {
                    GWT.log("Not an edit move - aborting");
                    return;
                }
                boolean confirm = Window.confirm("Only keep the variations starting from this position?" +
                        " This can not be undone.");
                if (confirm) {
                    node.setParent(null);
                    node.setParentIndex(0);
                    GameTree gameTree = new GameTree(node);
                    eventBus.fireEvent(new GameTreeChangedEvent(gameTree));
                }
                contextMenu.hide();
            });
            menuBar.addItem(rebaseMenu);
        }

        contextMenu.add(menuBar);
        contextMenu.hide();
        return contextMenu;
    }

    public void activate(final EventBus eventBus) {
        GWT.log(activityId + ": Activating GameTreePanel");
        this.eventBus = eventBus;
        eventBinder.bindEventHandlers(this, this.eventBus);
    }

    @EventHandler
    public void onGameTreeChanged(final GameTreeChangedEvent gameTreeChangedEvent) {
        GWT.log(activityId + " GameTreePanel: Handling game tree changed event - move " + gameTreeChangedEvent.getGoToMove());
        populateTree();
    }

    @EventHandler
    public void onNewVariationPlayed(final NewVariationPlayedEvent event) {
        GWT.log(activityId + " GameTreePanel: Handling NewVariationPlayedEvent");
        populateTree();
    }

    @EventHandler
    public void onNodeChangedEvent(final NodeChangedEvent event) {
        GWT.log(activityId + " GameTreePanel: Handling NodeChangedEvent");
        updateSelection();
    }

    @EventHandler
    public void onNotationStyleSelected(final NotationStyleSelectedEvent event) {
        GWT.log(activityId + " GameTreePanel: Handling NotationStyleSelectedEvent");
        populateTree();
    }


    private void updateSelection() {
        Iterator<TreeItem> iterator = tree.treeItemIterator();
        while (iterator.hasNext()) {
            TreeItem item = iterator.next();
            if (item.getUserObject() == gameNavigation.getCurrentNode()) {
                tree.setSelectedItem(item, false);
                item.getElement().scrollIntoView();
                colorNode(item, gameNavigation.getCurrentNode());
            }
        }
    }


    private void populateTree() {
        tree.clear();
        Node node = gameNavigation.getGameTree().getRootNode();
        populateMainVariationAndBranches(tree, node, 0);
        openTree();
        updateSelection();
    }

    private void openTree() {
        Iterator<TreeItem> iterator = tree.treeItemIterator();
        while (iterator.hasNext()) {
            iterator.next().setState(true);
        }
    }

    /**
     * Add all the moves of the main variation to parent
     * Also create branches if a node has multiple moves
     */
    private void populateMainVariationAndBranches(final HasTreeItems parent, final Node variationNode,
                                                  final int moveCount) {
        Node currentNode = variationNode;
        int variationMoveCount = moveCount;

        // First add the move of the current node
        TreeItem item = createTreeItem();
        setMoveNode(item, currentNode, variationMoveCount);
        parent.addItem(item);

        // Then add all children, along the main line and variations
        while (currentNode.hasChildren()) {
            item = createTreeItem();
            populateMainMoveAndBranches(item, currentNode, ++variationMoveCount);
            parent.addItem(item);
            currentNode = currentNode.getChildren().get(0);
        }
    }

    private TreeItem createTreeItem() {
        return new MoveTreeItem();
    }

    /**
     * Displays the main move at node, and adds branches for sibling moves. Populate the sibling trees.
     * - If there is only one move, display it at item.
     * - If there is one main move and one variation, display the main move at item, then create a child item and
     * populate the variation there.
     * - If there is one main move and two or more variations, display the main move at item, create one child item
     * for each move, then show the variation under each child.
     */
    private void populateMainMoveAndBranches(final TreeItem item, final Node node, final int moveCount) {
        if (!node.hasChildren()) return;

        List<Node> children = node.getChildren();
        setMoveNode(item, children.get(0), moveCount);

        if (children.size() == 2) {
            Node variationNode = children.get(1);
            populateMainVariationAndBranches(item, variationNode, moveCount);
        } else if (children.size() > 2) {
            for (int i = 1; i < children.size(); i++) {
                Node variationNode = children.get(i);
                TreeItem variationItem = createTreeItem();
                variationItem.setText("Variation #" + i);
                colorNode(variationItem, variationNode);
                populateMainVariationAndBranches(variationItem, variationNode, moveCount);
                item.addItem(variationItem);
            }
        }
    }

    private void setMoveNode(final TreeItem item, final Node node, final int moveCount) {
        Move move = node.getMove();
        item.setUserObject(node);
        if (move == null) {
            item.setText("START");
        } else if (move instanceof EditMove) {
            item.setText("POSITION (" + SfenConverter.toSFEN(((EditMove) move).getPosition()) + ")");
        } else if (move instanceof ShogiMove) {
            item.setText(moveCount + ". " + userPreferences.getMoveNotationAccordingToPreferences((ShogiMove) move,
                    true));
        } else {
            item.setText(moveCount + ". " + move);
        }
        colorNode(item, node);
        if (node.getComment().isPresent()) {
            item.setHTML(item.getText() + "&nbsp;<i class=\"mdi mdi-comment-text-outline mdi-18px\"></i>");
        }
    }

    private void colorNode(final TreeItem item, final Node node) {
        if (colorNewNodes && node.isNew()) {
            item.getElement().getStyle().setBackgroundColor(Color.YELLOW_LIGHTEN_3.getHex());
        } else if (colorVisitedNodes && node.isVisited()) {
            item.getElement().getStyle().setBackgroundColor(Color.LIGHT_GREEN_LIGHTEN_3.getHex());
        } else {
            item.getElement().getStyle().setBackgroundColor(Color.WHITE.getHex());
        }
        if (item.getParentItem() != null && item.getParentItem().getUserObject() == null) {
            colorNode(item.getParentItem(), node);
        }
    }

    /**
     * Custom TreeItem class to select and show edition menu on right click
     */
    private class MoveTreeItem extends TreeItem implements ContextMenuHandler {
        private final Label label;

        MoveTreeItem() {
            super();
            label = new Label();
            label.addBitlessDomHandler(this, ContextMenuEvent.getType());
            setWidget(label);
        }

        @Override
        public void onContextMenu(final ContextMenuEvent event) {
            event.preventDefault();
            event.stopPropagation();
            tree.setSelectedItem(this);
            Node node = (Node) getUserObject();
            deleteVariationMenu.setVisible(node.getParent() != null);
            if (promoteVariationMenu != null) promoteVariationMenu.setVisible(node.getParentIndex() > 0);
            if (rebaseMenu != null) rebaseMenu.setVisible(node.getMove() instanceof EditMove);
            contextMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
            contextMenu.show();
        }

        @Override
        public void setText(final String text) {
            label.setText(text);
        }

        @Override
        public void setSelected(final boolean selected) {
            super.setSelected(selected);
            label.setStyleName("gwt-TreeItem-selected", selected);
        }
    }
}
