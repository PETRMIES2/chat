package com.sope.domain.popular;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.sope.domain.websocket.CategoryDTO;

public class PopularUpdate {

    public final CategoryDTO category;
    public LocalDateTime lastUpdate;

    public PopularUpdate(final CategoryDTO category, final LocalDateTime lastUpdate) {
        this.category = category;
        this.lastUpdate = lastUpdate;
    }

    public void addOneToUserCount() {
        lastUpdate = LocalDateTime.now();
        category.setUsers(category.getUsers() + 1);
    }
    public void decreaseUserCountByOne() {
        lastUpdate = LocalDateTime.now();
        category.setUsers(category.getUsers() - 1);
    }
    @Override
    public String toString() {
        return String.join(" ", "Category", category.getName(), "Users:", "" + category.getUsers(), "ID", category.getId().toString(), "Last updated", lastUpdate.toString());

    }

    public boolean isReadyForRelease() {
        if (category.getUsers() <= 0) {
            return true;
        }
        if (ChronoUnit.HOURS.between(lastUpdate, LocalDateTime.now()) >= 20) {
            return true;
        }
        return false;
    }

}
