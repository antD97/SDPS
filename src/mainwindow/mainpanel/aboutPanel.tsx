import { resolveResource } from '@tauri-apps/api/path';
import { readTextFile } from '@tauri-apps/plugin-fs';
import { useEffect, useState } from 'react';
import { SiDiscord, SiKofi } from 'react-icons/si';
import A from '../../components/ui/anchor';
import Container from '../../components/ui/container';
import { useMainWindowContext } from '../mainWindowContext';

export default function AboutPanel() {
  const { version } = useMainWindowContext();

  const [attribution, setAttribution] = useState('');
  useEffect(() => {
    resolveResource('resources/oss-attribution/attribution.txt').then(async attributionPath => {
      setAttribution(await readTextFile(attributionPath));
    })
  }, []);

  const copyrightYear = `2021-${new Date().getFullYear()}`;

  return (
    <Container>
      <div className="flex flex-col items-stretch">
        <div className="relative self-center">
          <h1 className="text-2xl text-center border-b border-cyan-600">About SDPS</h1>
          <div className="absolute text-sm bottom-0 right-0 translate-x-full pl-1 text-cyan-600">
            v3.0.0
          </div>
        </div>
        <div className="flex justify-around font-mono">- antD -</div>
      </div>
      <div className="flex flex-col items-stretch gap-4">
        <p>
          SDPS is a handy tool for the game <A href="https://www.smitegame.com/play-for-free/">Smite</A> that lets you
          create overlays from your your real-time in-game combat. SDPS uses the in-game combat log, so it will work
          seamlessly with future Smite updates containing balance changes, new gods, and new items.
        </p>
        <p>
          Some of the overlays included are:
        </p>
        <ul className="list-disc ml-6">
          <li>An adjustable combat table to compare and experiment with item builds</li>
          <li>A match damage tracker to keep track of who is primarily dealing damage</li>
          <li>A fun combo counter to see how long you can continuously deal damage to enemy gods</li>
        </ul>

        <h2 className="self-center text-lg border-b border-cyan-600">How to Use</h2>
        <p>TODO more</p>
        <ol className="list-decimal ml-6">
          <li>
            Once in a match,
            enter <code className="bg-neutral-600/50 px-1 rounded">/combatlog toggle piped</code> to let SDPS see your
            combat log. This command has to be typed in <i>once per game launch</i>.
          </li>
          <li>
            Your first tick of damage
          </li>
        </ol>
        <p>TODO more</p>

        <h2 className="self-center text-lg border-b border-cyan-600">Community/Support</h2>
        <p>
          If you're looking for help with the tool or would like to otherwise talk about the project, be it feature
          recommendations, fixes, or pull requests, please join the Discord using the button below. If you enjoy my
          work, consider supporting me on Ko-fi.
        </p>
      </div>

      <div className="flex flex-col items-center gap-y-2 justify-around sm:flex-row">
        <A
          href="https://discord.gg/4P2TnBzr"
          className="flex items-center gap-1 bg-[#7289da] hover:bg-[#4f5f99] px-2 rounded no-underline hover:text-inherit"
        >
          <SiDiscord className="inline" />
          Join the Discord
        </A>
        <A
          href="https://ko-fi.com/antd_"
          className="flex items-center gap-1 bg-[#FF6433] hover:bg-[#bf4926] px-2 rounded no-underline hover:text-inherit"
        >
          <SiKofi className="inline" />
          Support me on Ko-fi
        </A>
      </div>

      <div className="flex flex-col items-stretch gap-2 select-text opacity-50 text-sm font-mono">
        <div className="self-center flex flex-col">
          <div>Version: {version}</div>
          <div>License: MIT</div>
          <div>Source: <A href="https://github.com/antD97/SDPS">https://github.com/antD97/SDPS</A></div>
        </div>
        <div className="flex flex-col gap-1">
          <div>Legal notices:</div>
          <div className="grid">
            <p className="min-h-48 h-48 resize-y border p-1 border-neutral-700 overflow-auto whitespace-pre">
              SDPS<br />
              {version} <A href="https://github.com/antD97/SDPS">https://github.com/antD97/SDPS</A><br />
              authors: antD97<br />
              The MIT License (MIT)<br />
              <br />
              Copyright © {copyrightYear} antD<br />
              <br />
              Permission is hereby granted, free of charge, to any person obtaining a copy of<br />
              this software and associated documentation files (the “Software”), to deal in<br />
              the Software without restriction, including without limitation the rights to<br />
              use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of<br />
              the Software, and to permit persons to whom the Software is furnished to do so,<br />
              subject to the following conditions:<br />
              <br />
              The above copyright notice and this permission notice shall be included in all<br />
              copies or substantial portions of the Software.<br />
              <br />
              THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br />
              IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS<br />
              FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR<br />
              COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER<br />
              IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN<br />
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.<br />
              <br />
              ******************************<br />
              <br />
              {attribution}
            </p>
          </div>
        </div>
      </div>
    </Container>
  );
}
