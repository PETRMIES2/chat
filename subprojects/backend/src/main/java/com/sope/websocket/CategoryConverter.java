package com.sope.websocket;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.sope.domain.category.Category;
import com.sope.domain.category.CategoryType;
import com.sope.domain.websocket.CategoryDTO;
import com.sope.utils.DateTimeZoneUtils;

public class CategoryConverter {
    private static final Map<CategoryType, String> DATEFORMATS = new ImmutableMap.Builder<CategoryType, String>()
            .put(CategoryType.EVENT, "dd.MM")
            .put(CategoryType.SHOW, "HH.mm")
            .build();

    public static CategoryDTO build(final Category category, final CategoryType categoryType) {
        final CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryType(categoryType);
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());

        if (category.getStartTime() != null && DATEFORMATS.containsKey(category.getType())) {
            final SimpleDateFormat DateFormatter = new SimpleDateFormat(DATEFORMATS.get(category.getType()));
            categoryDTO.setStartTime(DateFormatter.format(DateTimeZoneUtils.convertToTimeZone(category.getStartTime(), category.getTimezone())));
            categoryDTO.setEndTime(DateFormatter.format(DateTimeZoneUtils.convertToTimeZone(category.getEndTime(), category.getTimezone())));
        }
        Optional.ofNullable(category.getIconName()).ifPresent(iconName -> {
            categoryDTO.setIcon(iconName.toLowerCase());
        });
        categoryDTO.setPlace(category.getPlace());
        categoryDTO.setCompany(category.getCompany());

        return categoryDTO;
    }

}
