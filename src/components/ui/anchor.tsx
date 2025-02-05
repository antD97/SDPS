import { AnchorHTMLAttributes, FC } from "react";
import { twMerge } from "tailwind-merge";

const A: FC<AnchorHTMLAttributes<HTMLAnchorElement>> = ({ className, ...props }) => (
  <a
    target="_blank"
    className={twMerge('underline hover:text-cyan-600', className)}
    {...props}
  />
);

export default A;