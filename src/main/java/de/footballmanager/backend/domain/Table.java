package de.footballmanager.backend.domain;

import com.google.common.collect.Lists;

import java.util.List;

public class Table {

    private List<TableEntry> entries = Lists.newArrayList();

    public List<TableEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<TableEntry> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "Table{" +
                "entries=" + entries +
                '}';
    }
}
