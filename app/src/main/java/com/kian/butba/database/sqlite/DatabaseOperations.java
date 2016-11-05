package com.kian.butba.database.sqlite;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.kian.butba.database.server.AsyncDelegate;
import com.kian.butba.database.server.QueryTag;
import com.kian.butba.database.server.TablesFetcher;
import com.kian.butba.database.sqlite.entities.AcademicYear;
import com.kian.butba.database.sqlite.entities.Bowler;
import com.kian.butba.database.sqlite.entities.BowlerSeason;
import com.kian.butba.database.sqlite.entities.RankingStatus;
import com.kian.butba.database.sqlite.entities.StudentStatus;
import com.kian.butba.database.sqlite.entities.University;
import com.kian.butba.database.sqlite.tables.TableAcademicYear;
import com.kian.butba.database.sqlite.tables.TableBowler;
import com.kian.butba.database.sqlite.tables.TableBowlerSeason;
import com.kian.butba.database.sqlite.tables.TableRankingStatus;
import com.kian.butba.database.sqlite.tables.TableStudentStatus;
import com.kian.butba.database.sqlite.tables.TableUniversity;

import java.util.List;

/**
 * Created by Kian Mistry on 03/11/16.
 */

public class DatabaseOperations {

    public static void getAllAcademicYears(final Activity activity) {
        TablesFetcher academicYearsFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<String[]> results) {
                for(int i = 0; i < results.size(); i++) {
                    TableAcademicYear tableAcademicYear = new TableAcademicYear(activity);
                    tableAcademicYear.addAcademicYear(new AcademicYear(
                            Integer.valueOf(results.get(i)[0]),
                            results.get(i)[1]
                    ));
                }

                Snackbar.make(activity.getCurrentFocus(), "Retrieved all academic years", Snackbar.LENGTH_SHORT).show();
            }
        });
        academicYearsFetcher.execute(QueryTag.GET_ACADEMIC_YEARS);
    }

    /**
     * Set up a query fetcher to retrieve all the BUTBA members and to store in a local SQLite database.
     * @param activity
     */
    public static void getAllBowlers(final Activity activity) {
        TablesFetcher bowlersFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<String[]> results) {
                for(int i = 0; i < results.size(); i++) {
                    TableBowler tableBowler = new TableBowler(activity);
                    tableBowler.addBowler(new Bowler(
                            Integer.valueOf(results.get(i)[0]),
                            results.get(i)[1],
                            results.get(i)[2],
                            results.get(i)[3].charAt(0)
                    ));
                }

                Snackbar.make(activity.getCurrentFocus(), "Retrieved all bowlers", Snackbar.LENGTH_SHORT).show();
            }
        });
        bowlersFetcher.execute(QueryTag.GET_ALL_BOWLERS);
    }

    /**
     * Set up a query fetcher to retrieve all the BUTBA members bowled in each season, starting from the 2015/16 season,
     * and store in a local SQLite database.
     * @param activity
     */
    public static void getAllBowlersSeasons(final Activity activity) {
        TablesFetcher bowlersSeasonsFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<String[]> results) {
                for(int i = 0; i < results.size(); i++) {
                    TableBowlerSeason tableBowlerSeason = new TableBowlerSeason(activity);
                    tableBowlerSeason.addBowlerToSeason(new BowlerSeason(
                            i + 1,
                            Integer.valueOf(results.get(i)[1]),
                            Integer.valueOf(results.get(i)[2]),
                            Integer.valueOf(results.get(i)[3]),
                            Integer.valueOf(results.get(i)[4]),
                            Integer.valueOf(results.get(i)[5])
                    ));
                }

                Snackbar.make(activity.getCurrentFocus(), "Retrieved all bowlers seasons", Snackbar.LENGTH_SHORT).show();
            }
        });
        bowlersSeasonsFetcher.execute(QueryTag.GET_ALL_BOWLERS_SEASONS);
    }

    public static void getAllRankingStatuses(final Activity activity) {
        TablesFetcher rankingsFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<String[]> results) {
                for(int i = 0; i < results.size(); i++) {
                    TableRankingStatus tableRankingStatus = new TableRankingStatus(activity);
                    tableRankingStatus.addRankingStatus(new RankingStatus(
                            Integer.valueOf(results.get(i)[0]),
                            results.get(i)[1]
                    ));
                }

                Snackbar.make(activity.getCurrentFocus(), "Retrieved all ranking statuses", Snackbar.LENGTH_SHORT).show();
            }
        });
        rankingsFetcher.execute(QueryTag.GET_RANKING_STATUSES);
    }

    public static void getAllStudentStatuses(final Activity activity) {
        TablesFetcher studentsFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<String[]> results) {
                for(int i = 0; i < results.size(); i++) {
                    TableStudentStatus tableStudentStatus = new TableStudentStatus(activity);
                    tableStudentStatus.addStudentStatus(new StudentStatus(
                            Integer.valueOf(results.get(i)[0]),
                            results.get(i)[1]
                    ));
                }

                Snackbar.make(activity.getCurrentFocus(), "Retrieved all student statuses", Snackbar.LENGTH_SHORT).show();
            }
        });
        studentsFetcher.execute(QueryTag.GET_STUDENT_STATUSES);
    }

    public static void getAllUniversities(final Activity activity) {
        TablesFetcher universityFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<String[]> results) {
                for(int i = 0; i < results.size(); i++) {
                    TableUniversity tableUniversity = new TableUniversity(activity);
                    tableUniversity.addUniversity(new University(
                            Integer.valueOf(results.get(i)[0]),
                            results.get(i)[1]
                    ));
                }

                Snackbar.make(activity.getCurrentFocus(), "Retrieved all universities", Snackbar.LENGTH_SHORT).show();
            }
        });
        universityFetcher.execute(QueryTag.GET_UNIVERSITIES);
    }
}