package com.playshogi.website.gwt.client.tables;

import com.playshogi.website.gwt.shared.models.KifuDetails;
import org.dominokit.domino.ui.datatable.events.SearchEvent;
import org.dominokit.domino.ui.datatable.model.Category;
import org.dominokit.domino.ui.datatable.model.Filter;
import org.dominokit.domino.ui.datatable.store.SearchFilter;

import java.util.List;

import static java.util.Objects.nonNull;

public class KifuSearchFilter implements SearchFilter<KifuDetails> {

    @Override
    public boolean filterRecord(final SearchEvent searchEvent, final KifuDetails record) {
        List<Filter> searchFilters = searchEvent.getByCategory(Category.SEARCH);
        List<Filter> headerFilters = searchEvent.getByCategory(Category.HEADER_FILTER);

        boolean foundBySearch = searchFilters.isEmpty() || foundBySearch(record, searchFilters.get(0));
        boolean foundByHeaderFilter = headerFilters.stream().allMatch(filter -> findByHeaderFilter(record, filter));

        return foundBySearch && foundByHeaderFilter;
    }

    private boolean findByHeaderFilter(final KifuDetails record, final Filter filter) {
        if (nonNull(filter.getValues().get(0)) && !filter.getValues().get(0).isEmpty()) {
            switch (filter.getFieldName()) {
                case "name":
                    return record.getName().toLowerCase().contains(filter.getValues().get(0).toLowerCase());
                case "type":
                    return record.getType().name().equalsIgnoreCase(filter.getValues().get(0));
                default:
                    return false;
            }
        }
        return false;
    }

    private boolean foundBySearch(final KifuDetails record, final Filter searchFilter) {
        if (nonNull(searchFilter.getValues().get(0)) && !searchFilter.getValues().get(0).isEmpty()) {
            return filterByAll(record, searchFilter.getValues().get(0));
        }
        return true;
    }

    private boolean filterByAll(final KifuDetails record, final String searchText) {
        return record.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                record.getType().name().equalsIgnoreCase(searchText) ||
                record.getUpdateDate().toString().toLowerCase().contains(searchText.toLowerCase());
    }
}
