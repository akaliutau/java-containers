package org.containers.logger;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.MetadataNotFoundException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;

import static org.containers.util.StringUtils.*;

/**
 * A simple transfer listener that logs uploads/downloads to the console.
 * 
 * @author akaliutau
 *
 */
public class ConsoleTransferListener extends AbstractTransferListener {

	private final PrintStream out;

	private Map<TransferResource, Long> downloads = new ConcurrentHashMap<>();

	private int lastLength;

	public ConsoleTransferListener() {
		this(null);
	}

	public ConsoleTransferListener(PrintStream out) {
		this.out = (out != null) ? out : System.out;
	}

	@Override
	public void transferInitiated(TransferEvent event) {
		TransferStatus message = event.getRequestType() == TransferEvent.RequestType.PUT ? TransferStatus.UPLOAD_INPROGRESS : TransferStatus.DOWNLOAD_INPROGRESS;

		out.println(message + ": " + event.getResource().getRepositoryUrl() + event.getResource().getResourceName());
	}

	@Override
	public void transferProgressed(TransferEvent event) {
		TransferResource resource = event.getResource();
		downloads.put(resource, event.getTransferredBytes());

		StringBuilder buffer = new StringBuilder(64);

		for (Map.Entry<TransferResource, Long> entry : downloads.entrySet()) {
			long total = entry.getKey().getContentLength();
			long complete = entry.getValue();

			buffer.append(getStatus(complete, total)).append("  ");
		}

		int pad = lastLength - buffer.length();
		lastLength = buffer.length();
		pad(buffer, pad);
		buffer.append('\r');

		out.print(buffer);
	}


	@Override
	public void transferSucceeded(TransferEvent event) {
		transferCompleted(event);

		TransferResource resource = event.getResource();
		long contentLength = event.getTransferredBytes();
		if (contentLength >= 0) {
			TransferStatus type = event.getRequestType() == TransferEvent.RequestType.PUT ? TransferStatus.UPLOAD_COMPLETE : TransferStatus.DOWNLOAD_COMPLETE;
			String len = contentLength >= 1024 ? toKB(contentLength) + " KB" : contentLength + " B";

			String throughput = "";
			long duration = System.currentTimeMillis() - resource.getTransferStartTime();
			if (duration > 0) {
				long bytes = contentLength - resource.getResumeOffset();
				DecimalFormat format = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH));
				double kbPerSec = (bytes / 1024.0) / (duration / 1000.0);
				throughput = " at " + format.format(kbPerSec) + " KB/sec";
			}

			out.println(type + ": " + resource.getRepositoryUrl() + resource.getResourceName() + " (" + len + throughput
					+ ")");
		}
	}

	@Override
	public void transferFailed(TransferEvent event) {
		transferCompleted(event);

		if (!(event.getException() instanceof MetadataNotFoundException)) {
			event.getException().printStackTrace(out);
		}
	}

	private void transferCompleted(TransferEvent event) {
		downloads.remove(event.getResource());

		StringBuilder buffer = new StringBuilder(64);
		pad(buffer, lastLength);
		buffer.append('\r');
		out.print(buffer);
	}

	public void transferCorrupted(TransferEvent event) {
		event.getException().printStackTrace(out);
	}



}
