use crate::data::{
    CombatLogMonitorCommand, CombatLogMonitorState, MonitorThreadLoadedFileState, MonitorUpdate,
    MONITOR_UPDATE,
};
use std::{
    fs::{self, File},
    io::{BufRead, BufReader},
    sync::Mutex,
    thread,
    time::Duration,
};
use tauri::{AppHandle, Emitter, State};

#[tauri::command]
pub fn monitor_start(app: AppHandle, state: State<'_, Mutex<CombatLogMonitorState>>) {
    let mut state = state.lock().unwrap();

    // don't do anything if the thread was already started
    if state.monitor_thread_handle.is_some() {
        return;
    }

    // take combat log directory and monitor channel receiver
    let log_dir = state.log_dir.take().expect("Log directory path missing");
    let recv: std::sync::mpsc::Receiver<CombatLogMonitorCommand> = state
        .recv
        .take()
        .expect("Combat monitor mpsc receiver missing");

    // spawn monitor thread
    let handle = thread::spawn(move || {
        let mut loaded_file_state: Option<MonitorThreadLoadedFileState> = None;

        loop {
            let recv_result = recv.recv_timeout(Duration::from_millis(500));

            match recv_result {
                // received stop command
                Ok(CombatLogMonitorCommand::Stop) => break,

                // no command received
                Err(_) => {
                    // check for new combat log file
                    let newest_dir_entry = fs::read_dir(&log_dir)
                        .expect("Failed to read combat log directory")
                        .map(|f| f.unwrap())
                        // combat log files only
                        .filter(|f| {
                            let file_name = f.file_name().into_string().unwrap();
                            f.file_type().unwrap().is_file()
                                && file_name.starts_with("CombatLog_")
                                && file_name.ends_with(".log")
                        })
                        // find newest file
                        .reduce(|acc, f| {
                            let acc_time = acc.metadata().unwrap().modified().unwrap();
                            let f_time = f.metadata().unwrap().modified().unwrap();
                            if f_time < acc_time {
                                acc
                            } else {
                                f
                            }
                        })
                        // ignore if newest found file is the one already loaded
                        .filter(|newest_dir_entry| {
                            if let Some(loaded_file_state) = &loaded_file_state {
                                if let Some(reached_end_time) = loaded_file_state.reached_end_time {
                                    reached_end_time
                                        != File::open(newest_dir_entry.path())
                                            .unwrap()
                                            .metadata()
                                            .unwrap()
                                            .modified()
                                            .unwrap()
                                } else {
                                    false
                                }
                            } else {
                                true
                            }
                        });

                    // load most recently updated ("newest") combat log file
                    if let Some(newest_dir_entry) = newest_dir_entry {
                        let file = File::open(newest_dir_entry.path()).expect(
                            format!(
                                "Failed to open combat log: {}",
                                newest_dir_entry.path().display()
                            )
                            .as_str(),
                        );

                        loaded_file_state = Some(MonitorThreadLoadedFileState {
                            reached_end_time: None,
                            file_name: newest_dir_entry.file_name(),
                            reader: BufReader::new(file),
                            current_line: 0,
                        });
                    }

                    // if a file is open...
                    if let Some(loaded_file_state) = &mut loaded_file_state {
                        // if log end hasn't been reached...
                        if loaded_file_state.reached_end_time.is_none() {
                            // read next lines
                            let mut lines_read: Vec<(i32, String)> = Vec::new();
                            let mut line: String = String::new();

                            loop {
                                // read next line
                                line.clear();
                                loaded_file_state
                                    .reader
                                    .read_line(&mut line)
                                    .expect("Failed to read combat log line");

                                // check for log end
                                if line.trim() == ",{\"eventType\":\"end\"}" {
                                    loaded_file_state.reached_end_time = Some(
                                        loaded_file_state
                                            .reader
                                            .get_ref()
                                            .metadata()
                                            .unwrap()
                                            .modified()
                                            .unwrap(),
                                    );
                                }

                                // check for file end
                                if line.trim() == "" {
                                    break;
                                }

                                // save line
                                lines_read.push((loaded_file_state.current_line, line.clone()));
                                loaded_file_state.current_line += 1;
                            }

                            // send lines read
                            if lines_read.len() > 0 {
                                let payload = MonitorUpdate::Combat(
                                    loaded_file_state.file_name.to_str().unwrap().to_string(),
                                    lines_read,
                                );
                                app.emit(MONITOR_UPDATE, payload).unwrap();
                            }
                        }
                    }
                }
            }
        }
    });

    state.monitor_thread_handle = Some(handle);
}

#[tauri::command]
pub fn monitor_stop(state: State<'_, Mutex<CombatLogMonitorState>>) {
    let mut state = state.lock().unwrap();

    // send stop command to combat log monitor thread
    state
        .send
        .send(CombatLogMonitorCommand::Stop)
        .expect("No combat log monitor thread to receive \"stop\" command");

    // join combat log monitor thread
    let monitor_thread_handle = state
        .monitor_thread_handle
        .take()
        .expect("No combat log monitor thread to join");
    monitor_thread_handle
        .join()
        .expect("Failed to join combat lgo monitor thread");
}
