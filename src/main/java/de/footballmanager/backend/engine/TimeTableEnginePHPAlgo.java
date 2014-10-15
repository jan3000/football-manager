package de.footballmanager.backend.engine;

import java.util.List;

import com.google.common.collect.Lists;

import de.footballmanager.backend.domain.Match;
import de.footballmanager.backend.domain.Team;
import de.footballmanager.backend.domain.TimeTable;

public class TimeTableEnginePHPAlgo {

    public static TimeTable createTimeTable(final List<Team> teams) {
        TimeTable timeTable = new TimeTable();

        Team[] teamArray = (Team[]) teams.toArray();

        int anzahl = teams.size();
        int paare = anzahl / 2;
        int tage = anzahl - 1;
        int spiele = paare * tage;
        List<Integer> plan = Lists.newArrayList();
        int xPos = anzahl - 1;
        int tag = 0;
        int spielNummer = 0;
        int spielPaarungNummer = 0;
        int i = 0;

        for (int h = 1; h < anzahl; h++) {
            Team lastTeam = teams.get(anzahl - h);
            Team firstTeam = teams.get(h);
            Match match = new Match(lastTeam, firstTeam);
            teams.remove(anzahl - h);

        }

        // if(count($teams) % 2 ){
        // array_push($teams , 'FREI');
        // }
        //
        // $anz = count($teams); // Anzahl der Teams im Array $teams
        // $paare = $anz/2; // Anzahl der moeglichen Spielpaare
        // $tage = $anz-1; // Anzahl der Spieltage pro Runde
        // $spiele = $paare*$tage; // Anzahl der Spiele pro Hin-/Roeck-Runde
        // $plan = array(); // Array foer den kompletten Spielplan
        // $xpos = $anz-1; // hoechster Key im Array $teams
        // $tag = 0; // Zoehler foer Spieltag
        // $spnr = 0; // Zoehler foer Spielnummer
        // $sppaar = 0; // Zoehler foer Spielpaar
        // $i = 0; // Schleifenzoehler
        // //
        // ================================================================================
        // for ($tag=1; $tag<$anz; $tag++) {
        // array_splice ($teams, 1, 1, array(array_pop($teams),$teams[1]));
        // for ($sppaar=0; $sppaar<$paare; $sppaar++) {
        // $spnr++;
        // // wechseln zwischen G und H -Spiel:
        // if (($spnr%$anz!=1) and ($sppaar%2==0)) {
        // $hteam = $teams[$sppaar];
        // $gteam = $teams[$xpos-$sppaar];
        // } else {
        // $gteam = $teams[$sppaar];
        // $hteam = $teams[$xpos-$sppaar];
        // }
        // $plan[$tag][$spnr]["G"] = $gteam; // foer Hin-Runde
        // $plan[$tag][$spnr]["H"] = $hteam; // foer Hin-Runde
        // $plan[$tag+$tage][$spnr+$spiele]["G"] = $hteam; // foer Roeck-Runde
        // $plan[$tag+$tage][$spnr+$spiele]["H"] = $gteam; // foer Roeck-Runde
        // }
        // }
        // ksort($plan); // nach Spieltagen sortieren

        return timeTable;
    }
}