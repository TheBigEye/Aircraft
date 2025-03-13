package minicraft.network;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import minicraft.core.Game;
import minicraft.core.VersionInfo;
import minicraft.entity.Entity;
import minicraft.level.Level;
import minicraft.util.Action;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.tinylog.Logger;

import java.util.Random;

public class Network extends Game {
	private Network() {}

	private static final Random random = new Random();
	private static VersionInfo latestVersion = null;

	// Obviously, this can be null.
	@Nullable
	public static VersionInfo getLatestVersion() {
		return latestVersion;
	}

	public static void findLatestVersion(Action callback) {
		new Thread(() -> {
			Logger.debug("Fetching release list from GitHub..."); // Fetch the latest version from GitHub
			try {
				HttpResponse<JsonNode> response = Unirest.get("https://api.github.com/repos/TheBigEye/Aircraft/releases").asJson();
				if (response.getStatus() != 200) {
					Logger.error("Version request returned status code " + response.getStatus() + ": " + response.getStatusText());
					Logger.error("Response body: " + response.getBody());
					latestVersion = new VersionInfo(VERSION, "", "");
				} else {
					latestVersion = new VersionInfo(new JSONObject(response.getBody().getArray().getJSONObject(0).toString()));
				}
			} catch (UnirestException exception) {
				exception.printStackTrace();
				latestVersion = new VersionInfo(VERSION, "", "");
			}

			callback.act(); // finished.
		}, "Version check Thread").start();
	}

	@Nullable
	public static Entity getEntity(int eid) {
		for (Level level: levels) {
			if (level == null) continue;
			for (Entity e: level.getEntityArray()){
				if (e.eid == eid){
					return e;
				}
			}
		}

		return null;
	}

	public static int generateUniqueEntityId() {
		int eid;
		int tries = 0; // Just in case it gets out of hand.
		do {
			tries++;
			if (tries == 1000)
				System.out.println("Note: Trying 1000th time to find valid entity id...(Will continue)");

			eid = random.nextInt();
		} while (!idIsAvailable(eid));

		return eid;
	}

	public static boolean idIsAvailable(int eid) {
		if (eid == 0) return false; // This is reserved for the main player... kind of...
		if (eid < 0) return false; // ID's must be positive numbers.

		for (Level level: levels) {
			if (level == null) continue;
			for (Entity e: level.getEntityArray()) {
				if (e.eid == eid){
					return false;
				}
			}
		}

		return true;
	}
}
