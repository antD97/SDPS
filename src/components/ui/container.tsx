import { FC, HTMLAttributes } from 'react';
import { twMerge } from 'tailwind-merge';

interface ContainerProps extends HTMLAttributes<HTMLDivElement> {
  innerProps?: HTMLAttributes<HTMLDivElement>
}

const Container: FC<ContainerProps> = ({ innerProps, className, children, ...props }) => (
  <div className={twMerge('flex justify-center', className)} {...props}>
    <div className="grow max-w-screen-lg flex flex-col items-stretch gap-y-8 p-8" {...innerProps}>
      {children}
    </div>
  </div >
);

export default Container;
