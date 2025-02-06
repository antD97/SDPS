import { ButtonHTMLAttributes, FC, useState } from "react";
import { VscChromeClose } from "react-icons/vsc";
import { twMerge } from "tailwind-merge";

interface NavButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  selected?: boolean;
  onClose?: () => void;
}

const NavButton: FC<NavButtonProps> = ({ selected, onClose, children, className, ...props }) => {
  const [hovered, setIsHovered] = useState(false);
  return (
    <div
      onMouseEnter={() => { setIsHovered(true) }}
      onMouseLeave={() => { setIsHovered(false) }}
      className="relative grid"
    >
      <button
        className={twMerge(
          `px-4 py-0 ${selected ? 'bg-neutral-700' : 'active:bg-neutral-700'} ${hovered && 'bg-neutral-800'}`,
          className
        )}
        {...props}
      >
        {children}
      </button>
      {onClose && hovered && (
        <button
          onClick={onClose}
          className="absolute right-0 top-0 bottom-0 px-1 opacity-50 hover:opacity-100 hover:text-red-600"
        >
          <VscChromeClose />
        </button>
      )}
    </div>
  );
}

export default NavButton;
