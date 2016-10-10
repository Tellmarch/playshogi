package com.playshogi.website.gwt.client.ui;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.playshogi.website.gwt.client.mvp.AppPlaceHistoryMapper;
import com.playshogi.website.gwt.client.place.NewKifuPlace;

@Singleton
public class MyGamesView extends Composite {

	@Inject
	public MyGamesView(final AppPlaceHistoryMapper historyMapper) {
		GWT.log("Creating my games view");
		FlowPanel flowPanel = new FlowPanel();

		flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant(
				"On this page you can find all of your game records (kifu) saved on the server, or create/import new ones.<br>")));

		flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

		Hyperlink newKifuLink = new Hyperlink("Create or import a new kifu",
				historyMapper.getToken(new NewKifuPlace()));

		flowPanel.add(newKifuLink);

		flowPanel.add(new HTML(SafeHtmlUtils.fromSafeConstant("<br>")));

		// DataGrid<KifuDetails> dataGrid = new DataGrid<>(KifuDetails.KEY_PROVIDER);
		// dataGrid.setEmptyTableWidget(new HTML("No games found"));
		//
		// dockLayoutPanel.add(dataGrid);

		// Create a CellTable.
		CellTable<Contact> table = new CellTable<Contact>();
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		// Add a text column to show the name.
		TextColumn<Contact> nameColumn = new TextColumn<Contact>() {
			@Override
			public String getValue(final Contact object) {
				return object.name;
			}
		};
		table.addColumn(nameColumn, "Name");

		// Add a date column to show the birthday.
		DateCell dateCell = new DateCell();
		Column<Contact, Date> dateColumn = new Column<Contact, Date>(dateCell) {
			@Override
			public Date getValue(final Contact object) {
				return object.birthday;
			}
		};
		table.addColumn(dateColumn, "Birthday");

		// Add a text column to show the address.
		TextColumn<Contact> addressColumn = new TextColumn<Contact>() {
			@Override
			public String getValue(final Contact object) {
				return object.address;
			}
		};
		table.addColumn(addressColumn, "Address");

		// Add a selection model to handle user selection.
		final SingleSelectionModel<Contact> selectionModel = new SingleSelectionModel<Contact>();
		table.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(final SelectionChangeEvent event) {
				Contact selected = selectionModel.getSelectedObject();
				if (selected != null) {
					Window.alert("You selected: " + selected.name);
				}
			}
		});

		// Set the total row count. This isn't strictly necessary, but it affects
		// paging calculations, so its good habit to keep the row count up to date.
		table.setRowCount(CONTACTS.size(), true);

		// Push the data into the widget.
		table.setRowData(0, CONTACTS);

		flowPanel.add(table);

		initWidget(flowPanel);
	}

	/**
	 * A simple data type that represents a contact.
	 */
	private static class Contact {
		private final String address;
		private final Date birthday;
		private final String name;

		public Contact(final String name, final Date birthday, final String address) {
			this.name = name;
			this.birthday = birthday;
			this.address = address;
		}
	}

	/**
	 * The list of data to display.
	 */
	private static final List<Contact> CONTACTS = Arrays.asList(
			new Contact("John", new Date(80, 4, 12), "123 Fourth Avenue"),
			new Contact("Joe", new Date(85, 2, 22), "22 Lance Ln"),
			new Contact("George", new Date(46, 6, 6), "1600 Pennsylvania Avenue"));

}
