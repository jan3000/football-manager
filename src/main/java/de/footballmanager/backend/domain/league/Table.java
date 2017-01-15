package de.footballmanager.backend.domain.league;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Comparator;
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
        teamNameToEntry.put(tableEntry.getTeam(), tableEntry);
    }

    public List<TableEntry> getTableEntriesSorted() {
        entries.sort(new Comparator<TableEntry>() {
            @Override
            public int compare(TableEntry tableEntry1, TableEntry tableEntry2) {
                return tableEntry1.getPlace() > tableEntry2.getPlace() ? 1 : -1;
            }
        });
        return entries;
    }

    public List<TableEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<TableEntry> entries) {
        this.entries = entries;
        for (TableEntry entry : entries) {
            teamNameToEntry.put(entry.getTeam(), entry);
        }
    }

    @Override
    public String toString() {
        return "Table{" +
                "entries=" + entries +
                '}';
    }
}
