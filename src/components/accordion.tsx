import { cva } from "class-variance-authority";
import { motion } from "framer-motion";
import { ReactNode } from "react";
import { VscAdd, VscRemove } from "react-icons/vsc";
import { twMerge } from "tailwind-merge";
import { useImmer } from "use-immer";

const accordionHeaderVariants = cva(
  'relative text-lg text-center rounded-t-md transition-colors duration-300',
  {
    variants: {
      color: {
        blue1: 'bg-cyan-600 text-black',
        blue2: 'bg-cyan-900 text-black',
        gray1: 'bg-neutral-700 text-white',
        gray2: 'bg-neutral-800 text-white'
      },
      isHovered: { true: '', false: '' }
    },
    defaultVariants: {
      color: 'blue1',
      isHovered: false
    },
    compoundVariants: [
      {
        color: 'blue1',
        isHovered: true,
        class: 'bg-cyan-700'
      },
      {
        color: 'blue2',
        isHovered: true,
        class: 'bg-cyan-950'
      },
      {
        color: 'gray1',
        isHovered: true,
        class: 'bg-neutral-800'
      },
      {
        color: 'gray2',
        isHovered: true,
        class: 'bg-neutral-800/50'
      }
    ]
  }
);

const accordionContentVariants = cva(
  'overflow-hidden border-x border-b rounded-b-md transition-colors duration-300',
  {
    variants: {
      color: {
        blue1: 'border-cyan-600',
        blue2: 'border-cyan-900',
        gray1: 'border-neutral-700',
        gray2: 'border-neutral-800'
      },
      isHovered: { true: '', false: '' }
    },
    defaultVariants: {
      color: 'blue1'
    },
    compoundVariants: [
      {
        color: 'blue1',
        isHovered: true,
        class: 'border-cyan-700'
      },
      {
        color: 'blue2',
        isHovered: true,
        class: 'border-cyan-950'
      },
      {
        color: 'gray1',
        isHovered: true,
        class: 'border-neutral-800'
      },
      {
        color: 'gray2',
        isHovered: true,
        class: 'border-neutral-800/50'
      }
    ]
  }
);

interface AccordionProps {
  title: ReactNode;
  children: ReactNode;
  className?: string;
  contentClassName?: string;
  color?: 'blue1' | 'blue2' | 'gray1' | 'gray2'
}

export const Accordion = ({ title, children, color, className, contentClassName }: AccordionProps) => {
  const [isOpen, setIsOpen] = useImmer(false);
  const [isHovered, setIsHovered] = useImmer(false);

  return (
    <div className={twMerge('flex flex-col', className)}>
      <button
        onClick={() => setIsOpen(!isOpen)}
        onPointerEnter={() => setIsHovered(true)}
        onPointerLeave={() => setIsHovered(false)}
        className={accordionHeaderVariants({ color, isHovered })}
      >
        {title}
        <div className="absolute right-0 top-0 h-full flex items-center pr-1.5 text-sm">
          {isOpen ? (<VscRemove />) : (<VscAdd />)}
        </div>
      </button>
      <motion.div
        initial={{ height: '1rem' }}
        animate={{
          height: isOpen ? 'auto' : '1rem',
        }}
        className={accordionContentVariants({ color, isHovered })}
      >
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: isOpen ? 1 : 0 }}
          className={twMerge('flex flex-col p-4', contentClassName)}
        >
          {children}
        </motion.div>
      </motion.div>
    </div>
  )
}
