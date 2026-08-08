import { TitleBar } from '../components/titleBar';
import { MainPanel } from './mainPanel/mainPanel';
import { NavPanel } from './navPanel/navPanel';

export const MainWindow = () => {
  return (
    <main className="min-h-screen h-screen max-h-screen min-w-screen w-screen max-w-screen flex flex-col bg-neutral-900/97 text-white select-none">
      <TitleBar />
      <div className="basis-0 grow flex overflow-hidden">
        <NavPanel />
        <div className="grow grid overflow-y-auto">
          <MainPanel />
        </div>
      </div>
    </main>
  );
}
