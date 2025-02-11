use command::{monitor_start, monitor_stop};
use data::CombatLogMonitorState;
use std::sync::{mpsc, Mutex};
use tauri::{path::BaseDirectory, Manager};

pub mod command;
pub mod data;

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
                .resolve("My Games/Smite/BattleGame/Logs", BaseDirectory::Document)
                .expect("Failed to get documents directory");

            app.manage(Mutex::new(CombatLogMonitorState::new(
                log_dir.into(),
                mpsc::channel(),
            )));

            Ok(())
        })
        .invoke_handler(tauri::generate_handler![monitor_start, monitor_stop])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
