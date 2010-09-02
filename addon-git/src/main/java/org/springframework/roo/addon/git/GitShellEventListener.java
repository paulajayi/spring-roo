package org.springframework.roo.addon.git;

import java.io.File;
import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.file.monitor.event.FileEvent;
import org.springframework.roo.file.monitor.event.FileEventListener;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.shell.Shell;
import org.springframework.roo.shell.event.ShellStatus;
import org.springframework.roo.shell.event.ShellStatus.Status;
import org.springframework.roo.shell.event.ShellStatusListener;

/**
 * Listener for Shell events to support automatic Git repository commits.
 * 
 * @author Stefan Schmidt
 * @since 1.1
 */
@Component(immediate=true)
@Service
public class GitShellEventListener implements ShellStatusListener, FileEventListener {
	
	private Logger log = Logger.getLogger(GitShellEventListener.class.getName());
	private static String ANT_PATH_ROO_LOG = "**" + File.separator + "roo.log";
	
	@Reference private GitOperations revisionControl;
	
	@Reference private Shell shell;
	
	@Reference FileManager fileManager;
	
	@Reference PathResolver pathResolver;
	
	private boolean isDirty = false;
	   
	protected void activate(ComponentContext context) {
		shell.addShellStatusListener(this);
	}

	public void onShellStatusChange(ShellStatus oldStatus, ShellStatus newStatus) {
		if (isDirty && fileManager.exists(pathResolver.getIdentifier(Path.ROOT, ".git")) && newStatus.getStatus().equals(Status.EXECUTION_SUCCESS)) {
			GitCommandResult commandResult = revisionControl.commitAllChanges(newStatus.getMessage());
			log.info("Git commit " + commandResult.getCommitId() + " completed (" + commandResult.getResult() + ")");
			isDirty = false;
		}	
	}

	public void onFileEvent(FileEvent fileEvent) {
		if (!fileEvent.getFileDetails().matchesAntPath(ANT_PATH_ROO_LOG)) {
			isDirty = true;
		}
	}
}