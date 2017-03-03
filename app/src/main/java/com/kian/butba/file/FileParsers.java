package com.kian.butba.file;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.kian.butba.entities.Bowler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kian Mistry on 03/03/17.
 */

public class FileParsers {

	/**
	 * Retrieves a list of BUTBA members, according to their gender, student status and academic year.
	 *
	 * @param genderIndex The index of the gender.
	 * @param studentStatusIndex The index of the student status.
	 * @param academicYearIndex The index of the academic year.
	 *
	 * @return A list of BUTBA members.
	 */
	public static List<Bowler> parseBowlersList(Activity activity, int genderIndex, int studentStatusIndex, int academicYearIndex) {
		String genderCategory = (genderIndex == 1) ? "M" : (genderIndex == 2) ? "F" : "";

		List<Bowler> bowlersList = new ArrayList<>();

		try {
			String result = FileOperations.readFile(
					activity.getFilesDir() + FileOperations.INTERNAL_SERVER_DIR,
					FileOperations.ALL_BOWLERS_FILE,
					".json"
			);

			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("result");

			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject bowlerObject = jsonArray.getJSONObject(i);

				int id = Integer.parseInt(bowlerObject.getString("Id"));
				String forename = bowlerObject.getString("Forename");
				String surname = bowlerObject.getString("Surname");
				String gender = bowlerObject.getString("Gender");
				int studentStatusId = Integer.parseInt(bowlerObject.getString("StudentStatusId"));
				int academicYearId = Integer.parseInt(bowlerObject.getString("YearId"));


				if(
						genderCategory.equals(gender) &&
						studentStatusIndex == studentStatusId &&
						academicYearIndex == academicYearId
				) {
					Bowler bowler = new Bowler(id, forename, surname, gender.charAt(0));
					bowlersList.add(bowler);
				}
			}

			return bowlersList;
		}
		catch(IOException | JSONException e) {
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * Obtains the latest status of a bowler.
	 *
	 * @param bowlerId The ID of the bowler.
	 */
	public static void parseBowlersStatus(Activity activity, int bowlerId) {
		try {
			String result = FileOperations.readFile(
					activity.getFilesDir() + FileOperations.INTERNAL_SERVER_DIR,
					FileOperations.LATEST_STATUS_FILE + "_" + bowlerId,
					".json"
			);

			if(!result.equals("null")) {
				JSONObject jsonObject = new JSONObject(result);

				int academicYearId = Integer.parseInt(jsonObject.getString("YearId"));
				int studentStatusId = Integer.parseInt(jsonObject.getString("StudentStatusId"));
				int rankingStatusId = Integer.parseInt(jsonObject.getString("RankingStatusId"));

				SharedPreferences sharedPreferences = activity.getSharedPreferences("bowler_details", Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();

				if(academicYearId == 2) {
					editor.putInt("student_status", studentStatusId);
					editor.putInt("ranking_status", rankingStatusId);
				}
				else {
					editor.putInt("student_status", 0);
					editor.putInt("ranking_status", 0);
				}

				editor.commit();
			}
		}
		catch(IOException | JSONException e) {
			e.printStackTrace();
		}
	}
}