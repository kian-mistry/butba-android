package com.kian.butba.database.sqlite;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.kian.butba.database.server.AsyncDelegate;
import com.kian.butba.database.server.QueryTag;
import com.kian.butba.database.server.TablesFetcher;
import com.kian.butba.database.sqlite.entities.AcademicYear;
import com.kian.butba.database.sqlite.entities.Bowler;
import com.kian.butba.database.sqlite.entities.BowlerSeason;
import com.kian.butba.database.sqlite.entities.EventCode;
import com.kian.butba.database.sqlite.entities.RankingStatus;
import com.kian.butba.database.sqlite.entities.StudentStatus;
import com.kian.butba.database.sqlite.entities.University;
import com.kian.butba.database.sqlite.tables.TableAcademicYear;
import com.kian.butba.database.sqlite.tables.TableBowler;
import com.kian.butba.database.sqlite.tables.TableBowlerSeason;
import com.kian.butba.database.sqlite.tables.TableEventCode;
import com.kian.butba.database.sqlite.tables.TableRankingStatus;
import com.kian.butba.database.sqlite.tables.TableStudentStatus;
import com.kian.butba.database.sqlite.tables.TableUniversity;

import java.util.List;

/**
 * Created by Kian Mistry on 03/11/16.
 */

public class DatabaseOperations {

	public static final boolean[] isCompleted = {false, false, false, false, false, false, false};

	/**
	 * Set up a query fetcher to retrieve all academic years, starting from the 2015/16 academic year
	 * and store in a local SQLite database.
	 * @param context
	 */
    public static void getAllAcademicYears(final Context context) {

        TablesFetcher academicYearsFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<?> results) {
                List<String[]> res = (List<String[]>) results;

                for(int i = 0; i < results.size(); i++) {
                    TableAcademicYear tableAcademicYear = new TableAcademicYear(context);
                    tableAcademicYear.addAcademicYear(new AcademicYear(
                            Integer.valueOf(res.get(i)[0]),
                            res.get(i)[1]
                    ));
                }

	            isCompleted[0] = true;
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
     * Set up a query fetcher to retrieve all the BUTBA members and store in a local SQLite database.
     * @param context
     */
    public static void getAllBowlers(final Context context) {
        TablesFetcher bowlersFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<?> results) {
                List<String[]> res = (List<String[]>) results;

                for(int i = 0; i < results.size(); i++) {
                    TableBowler tableBowler = new TableBowler(context);
                    tableBowler.addBowler(new Bowler(
                            Integer.valueOf(res.get(i)[0]),
                            res.get(i)[1],
                            res.get(i)[2],
                            res.get(i)[3].charAt(0)
                    ));
                }

	            isCompleted[1] = true;
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
     * @param context
     */
    public static void getAllBowlersSeasons(final Context context) {
        TablesFetcher bowlersSeasonsFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<?> results) {
                List<String[]> res = (List<String[]>) results;

                for(int i = 0; i < results.size(); i++) {
                    TableBowlerSeason tableBowlerSeason = new TableBowlerSeason(context);
                    tableBowlerSeason.addBowlerToSeason(new BowlerSeason(
                            i + 1,
                            Integer.valueOf(res.get(i)[1]),
                            Integer.valueOf(res.get(i)[2]),
                            Integer.valueOf(res.get(i)[3]),
                            Integer.valueOf(res.get(i)[4]),
                            Integer.valueOf(res.get(i)[5])
                    ));
                }

	            isCompleted[2] = true;
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

	/**
	 * Set up a query fetcher to all the BUTBA events and store in a local SQLite database.
	 * @param context
	 */
    public static void getAllEvents(final Context context) {
        TablesFetcher eventsFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<?> results) {
                List<String[]> res = (List<String[]>) results;

                for(int i = 0; i < results.size(); i++) {
                    TableEventCode tableEventCode = new TableEventCode(context);
                    tableEventCode.addEvent(new EventCode(
                            Integer.valueOf(res.get(i)[0]),
                            res.get(i)[1]
                    ));
                }

	            isCompleted[3] = true;
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

	/**
	 * Set up a query fetcher to retrieve all the ranking statuses and store in a local SQLite database.
	 * @param context
	 */
    public static void getAllRankingStatuses(final Context context) {
        TablesFetcher rankingsFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<?> results) {
                List<String[]> res = (List<String[]>) results;

                for(int i = 0; i < results.size(); i++) {
                    TableRankingStatus tableRankingStatus = new TableRankingStatus(context);
                    tableRankingStatus.addRankingStatus(new RankingStatus(
                            Integer.valueOf(res.get(i)[0]),
                            res.get(i)[1]
                    ));
                }

	            isCompleted[4] = true;
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

	/**
	 * Set up a query fetcher to retrieve all the student statuses and store in a local SQLite database.
	 * @param context
	 */
    public static void getAllStudentStatuses(final Context context) {
        TablesFetcher studentsFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<?> results) {
                List<String[]> res = (List<String[]>) results;

                for(int i = 0; i < results.size(); i++) {
                    TableStudentStatus tableStudentStatus = new TableStudentStatus(context);
                    tableStudentStatus.addStudentStatus(new StudentStatus(
                            Integer.valueOf(res.get(i)[0]),
                            res.get(i)[1]
                    ));
                }

	            isCompleted[5] = true;
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

	/**
	 * Set up a query fetcher to retrieve all the universities bowling on the BUTBA tour and store
	 * in a local SQLite database.
	 * @param context
	 */
    public static void getAllUniversities(final Context context) {
        TablesFetcher universityFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<?> results) {
                List<String[]> res = (List<String[]>) results;

                for(int i = 0; i < results.size(); i++) {
                    TableUniversity tableUniversity = new TableUniversity(context);
                    tableUniversity.addUniversity(new University(
                            Integer.valueOf(res.get(i)[0]),
                            res.get(i)[1]
                    ));
                }

	            isCompleted[6] = true;
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
