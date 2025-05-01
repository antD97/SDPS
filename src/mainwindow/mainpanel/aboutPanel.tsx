import { resolveResource } from '@tauri-apps/api/path';
import { readTextFile } from '@tauri-apps/plugin-fs';
import { useEffect } from 'react';
import { SiDiscord, SiKofi } from 'react-icons/si';
import { useImmer } from 'use-immer';
import { A } from '../../components/anchor';
import { Container } from '../../components/container';
import { H } from '../../components/header';
import { useMainWindowContext } from '../mainWindowContext';

export const AboutPanel = () => {
  const { version } = useMainWindowContext();

  const [sdpsLicense, setSdpsLicense] = useImmer('');
  const [attribution, setAttribution] = useImmer('');

  useEffect(() => {
    resolveResource('resources/oss-attribution/sdps-license.txt').then(async licensePath => {
      setSdpsLicense(await readTextFile(licensePath));
    })
    resolveResource('resources/oss-attribution/attribution.txt').then(async attributionPath => {
      setAttribution(await readTextFile(attributionPath));
    })
  }, []);

  const copyrightYear = new Date().getFullYear() === 2025
    ? '2025'
    : `2025-${new Date().getFullYear()}`;

  return (
    <Container>
      <div className="flex flex-col items-stretch">
        <div className="relative self-center">
          <H level="1">About SDPS</H>
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

        <H level="2">How to Use</H>
        <p>TODO more</p>
        <ol className="list-decimal ml-6">
          <li>
            Once in a match,
            enter <code className="bg-neutral-600/50 px-1 rounded">/combatlog toggle</code> to let SDPS see your
            combat log. This command has to be typed in <i>once per game launch</i>.
          </li>
          <li>
            Your first tick of damage
          </li>
        </ol>
        <p>TODO more</p>

        <H level="2">Community, Support, & Contributing</H>
        <p>
          If you're looking for help with the tool or would like to otherwise talk about the project, be it feature
          recommendations, fixes, or pull requests, please join the Discord using the button below. If you enjoy my
          work, consider supporting me on Ko-fi.
        </p>
      </div>

      <div className="flex flex-col items-center gap-y-2 justify-around sm:flex-row">
        <A
          href="https://discord.gg/4P2TnBzr"
          className="flex items-center gap-1 bg-[#7289da] hover:bg-[#4f5f99] px-2 rounded no-underline hover:text-inherit transition-colors duration-300"
        >
          <SiDiscord className="inline" />
          Join the Discord
        </A>
        <A
          href="https://ko-fi.com/antd_"
          className="flex items-center gap-1 bg-[#FF6433] hover:bg-[#bf4926] px-2 rounded no-underline hover:text-inherit transition-colors duration-300"
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
              SDPS v{version}<br />
              <A href="https://github.com/antD97/SDPS">https://github.com/antD97/SDPS</A><br />
              Copyright © {copyrightYear} antD<br />
              <br />
              {sdpsLicense}
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
