package com.kian.butba.database.sqlite;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;

import com.kian.butba.database.server.AsyncDelegate;
import com.kian.butba.database.server.QueryTag;
import com.kian.butba.database.server.TablesFetcher;
import com.kian.butba.database.sqlite.entities.AcademicYear;
import com.kian.butba.database.sqlite.entities.Bowler;
import com.kian.butba.database.sqlite.entities.BowlerSeason;
import com.kian.butba.database.sqlite.entities.EventAverage;
import com.kian.butba.database.sqlite.entities.EventCode;
import com.kian.butba.database.sqlite.entities.RankingStatus;
import com.kian.butba.database.sqlite.entities.StudentStatus;
import com.kian.butba.database.sqlite.entities.University;
import com.kian.butba.database.sqlite.tables.TableAcademicYear;
import com.kian.butba.database.sqlite.tables.TableBowler;
import com.kian.butba.database.sqlite.tables.TableBowlerSeason;
import com.kian.butba.database.sqlite.tables.TableEventAverage;
import com.kian.butba.database.sqlite.tables.TableEventCode;
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

        //Run this thread in parallel with the next thread.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            academicYearsFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, QueryTag.GET_ACADEMIC_YEARS);
        }
        else {
            academicYearsFetcher.execute(QueryTag.GET_ACADEMIC_YEARS);
        }
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

        //Run this thread in parallel with the next thread.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            bowlersFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, QueryTag.GET_ALL_BOWLERS);
        }
        else {
            bowlersFetcher.execute(QueryTag.GET_ALL_BOWLERS);
        }
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

        //Run this thread in parallel with the next thread.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            bowlersSeasonsFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, QueryTag.GET_ALL_BOWLERS_SEASONS);
        }
        else {
            bowlersSeasonsFetcher.execute(QueryTag.GET_ALL_BOWLERS_SEASONS);
        }
    }

    public static void getAllEventAverages(final Activity activity) {
        TablesFetcher eventAveragesFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<String[]> results) {
                for(int i = 0; i < results.size(); i++) {
                    Integer rankingPinfall = (results.get(i)[4] == "") ? 0 : Integer.valueOf(results.get(i)[4]);
                    Integer hcpRankingPinfall = (results.get(i)[5] == "") ? 0 : Integer.valueOf(results.get(i)[5]);

                    TableEventAverage tableEventAverage = new TableEventAverage(activity);
                    tableEventAverage.addEventAverage(new EventAverage(
                            i + 1,
                            Integer.valueOf(results.get(i)[1]),
                            Integer.valueOf(results.get(i)[2]),
                            Integer.valueOf(results.get(i)[3]),
                            rankingPinfall,
                            hcpRankingPinfall,
                            Integer.valueOf(results.get(i)[6]),
                            Integer.valueOf(results.get(i)[7])
                    ));
                }

                Snackbar.make(activity.getCurrentFocus(), "Retrieved all event averages", Snackbar.LENGTH_SHORT).show();
            }
        });

        //Run this thread in parallel with the next thread.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            eventAveragesFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, QueryTag.GET_EVENT_AVERAGES);
        }
        else {
            eventAveragesFetcher.execute(QueryTag.GET_EVENT_AVERAGES);
        }
    }

    public static void getAllEvents(final Activity activity) {
        TablesFetcher eventsFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<String[]> results) {
                for(int i = 0; i < results.size(); i++) {
                    TableEventCode tableEventCode = new TableEventCode(activity);
                    tableEventCode.addEvent(new EventCode(
                            Integer.valueOf(results.get(i)[0]),
                            results.get(i)[1]
                    ));
                }

                Snackbar.make(activity.getCurrentFocus(), "Retrieved all events", Snackbar.LENGTH_SHORT).show();
            }
        });

        //Run this thread in parallel with the next thread.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            eventsFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, QueryTag.GET_EVENT_CODES);
        }
        else {
            eventsFetcher.execute(QueryTag.GET_EVENT_CODES);
        }
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

        //Run this thread in parallel with the next thread.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            rankingsFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, QueryTag.GET_RANKING_STATUSES);
        }
        else {
            rankingsFetcher.execute(QueryTag.GET_RANKING_STATUSES);
        }
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

        //Run this thread in parallel with the next thread.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            studentsFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, QueryTag.GET_STUDENT_STATUSES);
        }
        else {
            studentsFetcher.execute(QueryTag.GET_STUDENT_STATUSES);
        }
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

        //Run this thread in parallel with the next thread.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            universityFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, QueryTag.GET_UNIVERSITIES);
        }
        else {
            universityFetcher.execute(QueryTag.GET_UNIVERSITIES);
        }
    }
}
