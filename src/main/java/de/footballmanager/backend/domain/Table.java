package de.footballmanager.backend.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class Table {

    private List<TableEntry> entries = Lists.newArrayList();
    private transient Map<String, TableEntry> teamNameToEntry = Maps.newHashMap();

    public TableEntry getEntryByTeamName(String teamName) {
        return teamNameToEntry.get(teamName);
    }

    public void addEntry(TableEntry tableEntry) {
        entries.add(tableEntry);
        teamNameToEntry.put(tableEntry.getTeam().getName(), tableEntry);
    }

    public List<TableEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<TableEntry> entries) {
        this.entries = entries;
        for (TableEntry entry : entries) {
            teamNameToEntry.put(entry.getTeam().getName(), entry);
        }
    }

    @Override
    public String toString() {
        return "Table{" +
                "entries=" + entries +
                '}';
    }
}