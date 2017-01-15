package de.footballmanager.backend.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.footballmanager.backend.domain.club.Team;
import de.footballmanager.backend.domain.league.League;
import de.footballmanager.backend.domain.league.Match;
import de.footballmanager.backend.domain.league.MatchDay;
import de.footballmanager.backend.domain.league.TimeTable;
import de.footballmanager.backend.domain.persons.Manager;
import de.footballmanager.backend.domain.persons.Player;
import de.footballmanager.backend.domain.util.Pair;
import de.footballmanager.backend.enumeration.PlayingSystem;
import de.footballmanager.backend.enumeration.Position;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Service
public class KIService {

    @Autowired
    private TeamManagerService teamManagerService;
    @Autowired
    private LeagueService leagueService;
    @Autowired
    private ClubService clubService;



    public void handleNextMatchDay(String leagueName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(leagueName), "leagueName must be set");
        TimeTable timeTable = leagueService.getCurrentSeason(leagueName).getTimeTable();
        int currentMatchDay = timeTable.getCurrentMatchDay();
        MatchDay matchDay = timeTable.getMatchDay(currentMatchDay);
        handleMatchDay(leagueName, matchDay);
    }

    public void handleMatchDay(String leagueName, MatchDay matchDay) {
        Preconditions.checkArgument(StringUtils.isNotBlank(leagueName), "leagueName must be set");
        Preconditions.checkNotNull(matchDay, "matchDay must be set");
        Preconditions.checkArgument(isKIManaged(matchDay), "match day is not KI managed: ", matchDay);
        handleSetStartEleven(matchDay);
        leagueService.startNextMatchDay(leagueName);
        matchDay.getMatches().forEach(match -> {
            IntStream.range(1, 90).forEach(i -> leagueService.runNextMinute(leagueName));
        });
    }

    public void handleSetStartEleven(MatchDay matchDay){
        matchDay.getMatches().forEach(match -> {
            setPositionPlayerMapForKITeams(match, match.getHomeTeam(), true);
            setPositionPlayerMapForKITeams(match, match.getGuestTeam(), false);
        });
    }

    private void setPositionPlayerMapForKITeams(Match match, Team team, boolean homeTeam) {
        if (isKIManged(team.getName())) {
            Pair<PlayingSystem, Map<Position, Player>> pair = teamManagerService.getBestPlayersForBestSystem(team);
            if(homeTeam) {
                match.setPositionPlayerMapHomeTeam(pair.getSecond());
            } else {
                match.setPositionPlayerMapGuestTeam(pair.getSecond());
            }
        }
    }

    public boolean isKIManged(String clubName) {
        Manager manager = clubService.getClub(clubName).getManager();
        Preconditions.checkNotNull(manager, "no manager set for team {}", clubName);
        return manager.isComputerManaged();
    }

    public boolean isKIManaged(Match match) {
        String homeTeam = match.getHomeTeam().getName();
        String guestTeam = match.getGuestTeam().getName();

        return CollectionUtils.isEmpty(
                Lists.newArrayList(homeTeam, guestTeam).stream()
                        .filter(this::isKIManged)
                        .collect(toList()));
    }

    public boolean isKIManaged(MatchDay matchDay) {
        return CollectionUtils.isEmpty(
                matchDay.getMatches().stream()
                        .filter(this::isKIManaged)
                        .collect(toList()));
    }

    public boolean isKIManaged(League league) {
        return CollectionUtils.isEmpty(
                league.getTeams().stream()
                        .filter(team -> isKIManged(team.getName()))
                        .collect(toList()));
    }

    public void handleChanges(){}
    public void handleTraining(){}
    public void handleTransfers(){}

}
