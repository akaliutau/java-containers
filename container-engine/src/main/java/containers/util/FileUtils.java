package containers.util;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple utility class
 * 
 * @author akaliutau
 *
 */
public class FileUtils {
	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

	private final static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	public static final String SHA1_EXT = ".sha1";
	
	/**
	 * 
	 * @param newFiles - list of files collected by maven
	 * @param container - container's working dir
	 * @throws IOException 
	 */
	public static List<Path> diff(final List<Path> newFiles, final Path container) throws IOException{
		// first get a list of jars in the container
		Map<String, Path> containerMap = Files.list(container).filter(f -> f.endsWith(SHA1_EXT)).collect(Collectors.toMap(FileUtils::name, Function.identity(), (k1, k2) -> k1));
		List<Path> toCopy = new ArrayList<>();
		for (Path path : newFiles) {
			if (!containerMap.containsKey(name(path))) {
				toCopy.add(path);
			}
			Path dest = containerMap.remove(name(path));
			if (!areFilesIdenticalMemoryMapped(path, dest)) {
				toCopy.add(path);
			}
		}
		return toCopy;
	}

	public static boolean areFilesIdenticalMemoryMapped(final Path a, final Path b) throws IOException {
		try (final FileChannel fca = FileChannel.open(a, StandardOpenOption.READ);
				final FileChannel fcb = FileChannel.open(b, StandardOpenOption.READ)) {
			final MappedByteBuffer mbba = fca.map(FileChannel.MapMode.READ_ONLY, 0, fca.size());
			final MappedByteBuffer mbbb = fcb.map(FileChannel.MapMode.READ_ONLY, 0, fcb.size());
			return mbba.equals(mbbb);
		}
	}
	
	public static String name(Path p) {
		return p.getName(p.getNameCount() - 1).toString();
	}
	
	public static Path stripMame(Path p) {
		return Path.of(p.toString().replace(SHA1_EXT, ""));
	}
	
	public static String getM2Directory() {
		String homeDir = isWindows ? System.getenv("userprofile") : System.getProperty("user.home");
		String m2Path = homeDir + File.separator + ".m2" + File.separator + "repository";
		log.info("trying to figure out .m2 directory location");
		log.info("using default location {}, exists: {}", m2Path, exists(m2Path));
	    return "file:" + m2Path;
	}
	
	public static Path getCurrentWorkingDirectory() {
	    Path currentRelativePath = Paths.get("");
	    return Paths.get(currentRelativePath.toAbsolutePath().toString());
	}
	
	public static Path getDefaultWorkingDirectory() {
	    Path currentRelativePath = Paths.get("");
	    return Paths.get(currentRelativePath.toAbsolutePath().toString(), "container");
	}
	
	/**
	 * Used to validate path (existence)
	 * @param path
	 * @return
	 */
	public static boolean exists(String path) {
		File f = new File(path);
		return f.exists();
	}
}
