package com.playshogi.website.gwt.client.tables;

import com.playshogi.website.gwt.shared.models.LessonDetails;
import org.dominokit.domino.ui.datatable.events.SearchEvent;
import org.dominokit.domino.ui.datatable.model.Category;
import org.dominokit.domino.ui.datatable.model.Filter;
import org.dominokit.domino.ui.datatable.store.SearchFilter;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

public class LessonsSearchFilter implements SearchFilter<LessonDetails> {

    @Override
    public boolean filterRecord(final SearchEvent searchEvent, final LessonDetails record) {
        List<Filter> searchFilters = searchEvent.getByCategory(Category.SEARCH);
        List<Filter> headerFilters = searchEvent.getByCategory(Category.HEADER_FILTER);

        boolean foundBySearch = searchFilters.isEmpty() || foundBySearch(record, searchFilters.get(0));
        boolean foundByHeaderFilter = headerFilters.stream().allMatch(filter -> findByHeaderFilter(record, filter));

        return foundBySearch && foundByHeaderFilter;
    }

    private boolean findByHeaderFilter(final LessonDetails record, final Filter filter) {
        if (nonNull(filter.getValues().get(0)) && !filter.getValues().get(0).isEmpty()) {
            switch (filter.getFieldName()) {
                case "title":
                    return record.getTitle().toLowerCase().contains(filter.getValues().get(0).toLowerCase());
                case "description":
                    return record.getDescription().toLowerCase().contains(filter.getValues().get(0).toLowerCase());
                case "author":
                    return record.getAuthor().toLowerCase().contains(filter.getValues().get(0).toLowerCase());
                case "difficulty":
                    return record.getDifficulty() == Integer.parseInt(filter.getValues().get(0));
                case "status":
                    return record.isHidden() == Boolean.parseBoolean(filter.getValues().get(0));
                case "parent":
                    return record.getParentLessonId().toLowerCase().contains(filter.getValues().get(0).toLowerCase());
                default:
                    return false;
            }
        }
        return false;
    }

    private boolean foundBySearch(final LessonDetails record, final Filter searchFilter) {
        if (nonNull(searchFilter.getValues().get(0)) && !searchFilter.getValues().get(0).isEmpty()) {
            return filterByAll(record, searchFilter.getValues().get(0));
        }
        return true;
    }

    private boolean filterByAll(final LessonDetails record, final String searchText) {
        if ("very easy".equalsIgnoreCase(searchText) && record.getDifficulty() == 1) return true;
        if ("easy".equalsIgnoreCase(searchText) && record.getDifficulty() <= 2) return true;
        if ("medium".equalsIgnoreCase(searchText) && record.getDifficulty() == 3) return true;
        if ("hard".equalsIgnoreCase(searchText) && record.getDifficulty() >= 4) return true;
        if ("very hard".equalsIgnoreCase(searchText) && record.getDifficulty() == 5) return true;

        return record.getAuthor().toLowerCase().contains(searchText.toLowerCase()) ||
                record.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                record.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                record.getParentLessonId().toLowerCase().contains(searchText.toLowerCase()) ||
                Arrays.stream(record.getTags()).anyMatch(tag -> tag.toLowerCase().contains(searchText.toLowerCase()));
    }
}
