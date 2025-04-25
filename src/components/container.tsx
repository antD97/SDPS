import { FC, HTMLAttributes } from 'react';
import { twMerge } from 'tailwind-merge';

interface ContainerProps extends HTMLAttributes<HTMLDivElement> {
  innerProps?: HTMLAttributes<HTMLDivElement>;
  innerClassname?: string;
}

export const Container: FC<ContainerProps> = ({ innerProps, innerClassname, className, children, ...props }) => (
  <div className={twMerge('flex justify-center', className)} {...props}>
    <div
      className={twMerge('grow max-w-screen-lg flex flex-col items-stretch gap-y-8 p-8', innerClassname)}
      {...innerProps}
    >
      {children}
    </div>
  </div >
);
