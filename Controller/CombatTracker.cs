/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using System.Diagnostics;
using System.Threading;

namespace SDPS.Controller
{
    internal class CombatTracker
    {

        internal void run(object cancellationToken)
        {
            while (!((CancellationToken)cancellationToken).IsCancellationRequested)
            {
                Trace.WriteLine("Pretending to monitor combat log file...");
                Thread.Sleep(500);
            }

            Trace.WriteLine("Combat tracker thread exited.");
        }
    }
}
