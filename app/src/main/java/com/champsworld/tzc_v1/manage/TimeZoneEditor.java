package com.champsworld.tzc_v1.manage;

import com.champsworld.tzc_v1.ResultItem;

import java.util.function.BiConsumer;

public interface TimeZoneEditor {

    void loadItem(ResultItem item);

    void editItem(ResultItem item, BiConsumer<Boolean, String> onUpdateFinished);

    boolean deleteItem(ResultItem item, BiConsumer<Boolean, String> onDeleteFinished);
}