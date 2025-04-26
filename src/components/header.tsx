import { cva } from "class-variance-authority";
import { FC, HTMLAttributes } from "react";
import { twMerge } from "tailwind-merge";

const headerVariants = cva(
  'self-center text-center border-b border-cyan-600',
  {
    variants: {
      'level': {
        '1': 'text-2xl',
        '2': 'text-xl',
        '3': 'text-lg',
        '4': 'text-md',
        '5': 'text-md',
        '6': 'text-md',
      }
    },
    defaultVariants: {}
  }
);

interface HeaderProps extends HTMLAttributes<HTMLHeadingElement> {
  level: '1' | '2' | '3' | '4' | '5' | '6';
}

export const H: FC<HeaderProps> = ({ level, className, ...props }) => {
  const cn = twMerge(headerVariants({ level }), className);
  switch (level) {
    case '1': return (<h1 className={cn} {...props} />);
    case '2': return (<h2 className={cn} {...props} />);
    case '3': return (<h3 className={cn} {...props} />);
    case '4': return (<h4 className={cn} {...props} />);
    case '5': return (<h5 className={cn} {...props} />);
    case '6': return (<h6 className={cn} {...props} />);
  }
}
