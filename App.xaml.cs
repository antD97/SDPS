/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using SDPS.Controller;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;

namespace SDPS
{
    public partial class App : Application
    {
        internal static string Version = "v3.0";

        private readonly CancellationTokenSource cts = new();
        private Task combatTrackerTask;

        // Starts all parallel tasks.
        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);

            combatTrackerTask = new Task(new CombatTracker().run, cts.Token);
            combatTrackerTask.Start();
        }

        // Kills all running parallel tasks and waits for them to close.
        internal void killTasksAndWait()
        {
            cts.Cancel();
            combatTrackerTask.Wait();
        }
    }
}
