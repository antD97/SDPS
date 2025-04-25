// import { exists, readDir, readTextFileLines, stat } from '@tauri-apps/plugin-fs';
import { useState } from 'react';

// const logsDir = await join(await path.documentDir(), 'My Games', 'Smite', 'BattleGame', 'Logs');

export const CombatTableSettings = () => {
  // const [logsDirExists, setLogsDirExists] = useState<boolean | null>(null);
  // const [mostRecentLogFile, setMostRecentLogFile] = useState<{ filename: string, itr: AsyncIterableIterator<string> } | null>(null);
  const [lines, setLines] = useState<string[]>([]);

  // const findMostRecentDir = useCallback(async () => {
  //   const files = (await readDir(logsDir))
  //     .filter((f) => f.isFile && f.name.toLowerCase().startsWith('combatlog') && f.name.endsWith('.log'));

  //   let newestFile = files[0];
  //   for (const f of files) {
  //     const a = (await stat(await join(logsDir, f.name))).mtime!;
  //     const b = (await stat(await join(logsDir, newestFile.name))).mtime!;
  //     if (a > b) { newestFile = f; }
  //   }

  //   setMostRecentLogFile({
  //     filename: newestFile.name,
  //     itr: await readTextFileLines(await join(logsDir, newestFile.name))
  //   });
  // }, []);

  // const nextLine = useCallback(async () => {
  //   if (mostRecentLogFile) {
  //     const a = await mostRecentLogFile.itr.next();
  //     const line = `${a.value}${a.done === undefined ? '' : a.done === false ? ' | FALSE' : ' | TRUE'}`;
  //     setLines([...lines, line]);
  //   }
  // }, [mostRecentLogFile, lines]);

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-2xl self-center border-b border-cyan-600">Combat Table</h1>
      combat table

      {/* <div>logsdir: <code>{logsDir}</code></div> */}

      {/* <div>
        <button
          onClick={async () => { setLogsDirExists(await exists(logsDir)); }}
          className="bg-cyan-600"
        >
          logsDir Exists?
        </button>
        {`${logsDirExists}`}
      </div> */}

      {/* <div>
        <button
          onClick={findMostRecentDir}
          className="bg-cyan-600"
        >
          find most recent log file
        </button>
        {`${mostRecentLogFile}`}
      </div> */}

      {/* <div>
        <button
          onClick={() => { invoke('monitor_file', { filename: mostRecentLogFile?.filename }) }}
          className="bg-cyan-600"
        >
          update rust file
        </button>
      </div> */}

      {/* <div>
        <button
          onClick={nextLine}
          className="bg-cyan-600"
        >
          next line
        </button>
        <div className="bg-neutral-700 text-nowrap">
          {lines.map((line, i) => (
            <div key={i}>{line}</div>
          ))}
        </div>
      </div> */}
    </div>
  );
}
