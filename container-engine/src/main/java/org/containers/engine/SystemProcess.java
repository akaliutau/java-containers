package org.containers.engine;

import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper around JVM's standard ProcessBuilder
 * 
 * Allows to re-use standard functionality:
 * 
 * 1) change the working directory our shell command is running in using
 * builder.directory()
 * 
 * 2) set-up a custom key-value map as environment using builder.environment()
 * 
 * 3) redirect input and output streams to custom replacements
 * 
 * 4) inherit both of them to the streams of the current JVM process using
 * builder.inheritIO()
 * 
 * @author akaliutau
 *
 */
public class SystemProcess {
	private static final Logger log = LoggerFactory.getLogger(SystemProcess.class);

	private final Path workingDir;
	private final ProcessBuilder builder = new ProcessBuilder();
	private final Command cmd;

	private final Date created = new Date();
	private Date started;
	private Date finished;
	private int exitCode;
	private boolean isError;
	private String errMessage;

	public SystemProcess(Path workingDir, Command cmd) {
		this.cmd = cmd;
		this.workingDir = workingDir;
	}

	public void exec() {
		try {
			builder.command(cmd.params());
			builder.directory(workingDir.toFile());
			Process process = builder.start();
			this.started = new Date();
			StreamRunner streamGobbler = new StreamRunner(process.getInputStream(), log::info);
			Executors.newSingleThreadExecutor().submit(streamGobbler);
			this.exitCode = process.waitFor();
		} catch (Exception e) {
			this.exitCode = -1;
			errMessage = e.getMessage();
		} finally {
			this.finished = new Date();
		}
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

	public int getExitCode() {
		return exitCode;
	}

	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	public Date getCreated() {
		return created;
	}
	
	public String duration() {
		if (started == null || finished == null) {
			return "N/A";
		}
		return String.format(" %.4f sec", (float) (finished.getTime() - started.getTime()) / 1000);
	}
 
	
	public void printStat() {
		log.debug("process {}", String.join(" ", this.cmd.args));
		log.debug("started {}", started);
		log.debug("finished {}", finished);
		log.debug("duration {}", duration());
		log.debug("exit code {}", exitCode);
	}

}
