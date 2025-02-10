import { invoke } from "@tauri-apps/api/core";
import { getCurrentWindow } from "@tauri-apps/api/window";
import { exit } from "@tauri-apps/plugin-process";
import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);

const window = getCurrentWindow();

if (window.label === 'main') {

  invoke('monitor_start');

  window.onCloseRequested(() => {
    invoke('monitor_stop');
    exit();
  });
}
