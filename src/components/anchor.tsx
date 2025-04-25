import { cva } from 'class-variance-authority';
import { AnchorHTMLAttributes, FC } from 'react';
import { twMerge } from 'tailwind-merge';

export const anchorVariants = cva(
  'underline hover:text-cyan-600',
  {
    variants: {},
    defaultVariants: {}
  });

export const A: FC<AnchorHTMLAttributes<HTMLAnchorElement>> = ({ className, ...props }) => (
  <a
    target="_blank"
    className={twMerge(anchorVariants(), className)}
    {...props}
  />
);
