package containers.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;

public class JsonUtils {
	public static <T> T fromJson(Path path, Class<T> cls) throws UnsupportedEncodingException, IOException {
		try (InputStream is = new FileInputStream(path.toFile());
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			GsonBuilder gson = new GsonBuilder();

			return gson.create().fromJson(rd.lines().collect(Collectors.joining()), cls);
		}
	}
}
