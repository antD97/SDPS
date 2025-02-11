use std::{
    ffi::OsString,
    fs::File,
    io::BufReader,
    path::Path,
    sync::mpsc::{Receiver, Sender},
    thread::JoinHandle,
    time::SystemTime,
};

use serde::Serialize;

// commands received by monitor thread
pub enum CombatLogMonitorCommand {
    Stop,
}

// events emitted by monitor thread
#[derive(Clone, Serialize)]
pub enum MonitorUpdate {
    Combat(String, Vec<(i32, String)>), // filename, (line_number, line_text)[]
}

// app state
pub struct CombatLogMonitorState {
    pub log_dir: Option<Box<Path>>,
    pub monitor_thread_handle: Option<JoinHandle<()>>,
    pub send: Sender<CombatLogMonitorCommand>,
    pub recv: Option<Receiver<CombatLogMonitorCommand>>,
}

impl CombatLogMonitorState {
    pub fn new(
        log_dir: Box<Path>,
        mpsc_channel: (
            Sender<CombatLogMonitorCommand>,
            Receiver<CombatLogMonitorCommand>,
        ),
    ) -> Self {
        let (send, recv) = mpsc_channel;
        Self {
            log_dir: Some(log_dir),
            monitor_thread_handle: None,
            send,
            recv: Some(recv),
        }
    }
}

// monitor thread loaded file state
pub struct MonitorThreadLoadedFileState {
    // if some, file "end" line reached at stored SystemTime. if none, file "end" line not yet reached
    pub reached_end_time: Option<SystemTime>,
    pub file_name: OsString,
    pub reader: BufReader<File>,
    pub current_line: i32,
}

pub const MONITOR_UPDATE: &str = "sdps-monitor-update";
