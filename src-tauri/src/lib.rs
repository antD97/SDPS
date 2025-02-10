use serde::Serialize;
use std::{
    ffi::OsString,
    fs::{self, File},
    io::{BufRead, BufReader},
    path::Path,
    sync::{
        mpsc::{self, Receiver, Sender},
        Mutex,
    },
    thread::{self, JoinHandle},
    time::Duration,
};
use tauri::{path::BaseDirectory, AppHandle, Emitter, Manager, State};

enum CombatLogMonitorCommand {
    Stop,
}

struct CombatLogMonitorState {
    log_dir: Option<Box<Path>>,
    handle: Option<JoinHandle<()>>,
    send: Sender<CombatLogMonitorCommand>,
    recv: Option<Receiver<CombatLogMonitorCommand>>,
}

impl CombatLogMonitorState {
    fn new(log_dir: Option<Box<Path>>) -> Self {
        let (send, recv) = mpsc::channel();
        Self {
            log_dir,
            handle: None,
            send,
            recv: Some(recv),
        }
    }
}

#[derive(Clone, Serialize)]
enum MonitorUpdate {
    Combat(String, Vec<(i32, String)>), // filename, (line_number, line_text)[]
}

const MONITOR_UPDATE: &str = "sdps-monitor-update";

#[tauri::command]
fn monitor_start(app: AppHandle, state: State<'_, Mutex<CombatLogMonitorState>>) {
    let mut state = state.lock().unwrap();

    // don't do anything if the thread was already started
    if state.handle.is_some() {
        return;
    }

    // take combat log directory and monitor channel receiver
    let log_dir = state.log_dir.take().expect("Log directory path missing");
    let recv = state
        .recv
        .take()
        .expect("Combat monitor mpsc receiver missing");

    // spawn monitor thread
    let handle = thread::spawn(move || {
        // loaded file path, loaded file file reader, current line number
        let mut loaded_file_state: Option<(OsString, BufReader<File>, i32)> = None;

        loop {
            // println!("--- loop start! ---");

            let recv_result = recv.recv_timeout(Duration::from_millis(500));

            match recv_result {
                // received stop command
                Ok(CombatLogMonitorCommand::Stop) => break,

                // no command received
                Err(_) => {
                    // println!("no command received!");
                    // println!("loaded file state: {:?}", loaded_file_state);

                    // check for new combat log file
                    let newest_log = fs::read_dir(&log_dir)
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
                        .filter(|newest_log| {
                            if let Some((loaded_file_name, _, _)) = &loaded_file_state {
                                *loaded_file_name != newest_log.file_name()
                            } else {
                                true
                            }
                        });

                    // println!("search for newer log result: {:?}", newest_log);

                    // load newest combat log file
                    if let Some(newest_file) = newest_log {
                        let file = File::open(newest_file.path()).expect(
                            format!(
                                "Failed to open combat log: {}",
                                newest_file.path().display()
                            )
                            .as_str(),
                        );
                        loaded_file_state =
                            Some((newest_file.file_name(), BufReader::new(file), 0));
                    }

                    // println!("loaded file state 2: {:?}", loaded_file_state);

                    // if a file is open...
                    if let Some((loaded_file_name, reader, current_line)) = &mut loaded_file_state {
                        // read next lines
                        let mut lines_read: Vec<(i32, String)> = Vec::new();
                        let mut line = String::new();

                        loop {
                            line.clear();
                            reader
                                .read_line(&mut line)
                                .expect("Failed to read combat log line");
                            if line == "" {
                                break;
                            }
                            lines_read.push((*current_line, line.clone()));
                            *current_line += 1;
                        }

                        // println!("lines read: {:?}", &lines_read);

                        // send lines read
                        if lines_read.len() > 0 {
                            let payload = MonitorUpdate::Combat(
                                loaded_file_name.clone().into_string().unwrap(),
                                lines_read,
                            );
                            app.emit(MONITOR_UPDATE, payload).unwrap();
                        }
                    }
                }
            }
        }
    });

    state.handle = Some(handle);
}

#[tauri::command]
fn monitor_stop(state: State<'_, Mutex<CombatLogMonitorState>>) {
    let mut state = state.lock().unwrap();

    // send stop command to combat log monitor thread
    state
        .send
        .send(CombatLogMonitorCommand::Stop)
        .expect("No combat log monitor thread to receive \"stop\" command");

    // join combat log monitor thread
    let handle = state
        .handle
        .take()
        .expect("No combat log monitor thread to join");
    handle
        .join()
        .expect("Failed to join combat lgo monitor thread");
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_process::init())
        .plugin(tauri_plugin_log::Builder::new().build())
        .plugin(tauri_plugin_single_instance::init(|_, _, _| {}))
        .plugin(tauri_plugin_fs::init())
        .plugin(tauri_plugin_opener::init())
        .setup(|app| {
            let log_dir = app
                .path()
                .resolve("My Games/Smite/BattleGame/Logs", BaseDirectory::Document);
            app.manage(Mutex::new(CombatLogMonitorState::new(Some(
                log_dir.expect("Failed to get documents directory").into(),
            ))));
            Ok(())
        })
        .invoke_handler(tauri::generate_handler![monitor_start, monitor_stop,])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
