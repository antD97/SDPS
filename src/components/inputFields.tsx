import React, { ReactNode } from "react";

const LabelledField = ({ label, children }: { label: string, children: ReactNode }) => (
  <>
    {label}
    <div className="flex flex-col w-40 items-stretch justify-center">
      {children}
    </div>
  </>
);

export const FieldContainer = ({ children }: { children: ReactNode }) => (
  <div className="flex">
    <div className="grid grid-cols-[auto_auto] gap-x-4 gap-y-2">
      {children}
    </div>
  </div>
);

interface TextFieldProps {
  label: string;
  value: string;
  onChange?: React.ChangeEventHandler<HTMLInputElement>;
}

export const TextField = ({ label, value, onChange }: TextFieldProps) => (
  <LabelledField label={label}>
    <input
      value={value}
      onChange={onChange}
      className="bg-black/50 rounded px-1"
    />
  </LabelledField>
);

interface SelectFieldProps {
  label: string;
  options: string[];
  value: string;
  onChange?: React.ChangeEventHandler<HTMLSelectElement>;
}

export const SelectField = ({ label, options, value, onChange }: SelectFieldProps) => (
  <LabelledField label={label}>
    <select
      value={value}
      onChange={onChange}
      className="bg-black/50 rounded px-1"
    >
      {options.map((option, i) => (<option key={i}>{option}</option>))}
    </select>
  </LabelledField>
);

interface NumFieldProps {
  label: string;
  min: number;
  max: number;
  step?: number;
  value: number;
  onChange?: React.ChangeEventHandler<HTMLInputElement>;
}

export const NumField = ({ label, min, max, step = 1, value, onChange }: NumFieldProps) => (
  <LabelledField label={label}>
    <input
      type="number"
      min={min}
      max={max}
      step={step}
      value={value}
      onChange={onChange}
      className="bg-black/50 rounded px-1"
    />
  </LabelledField>
);

interface CheckboxFieldProps {
  label: string;
  checked: boolean;
  onChange?: React.ChangeEventHandler<HTMLInputElement>;
}

export const CheckboxField = ({ label, checked, onChange }: CheckboxFieldProps) => (
  <LabelledField label={label}>
    <div>
      <input
        type="checkbox"
        checked={checked}
        onChange={onChange}
      />
    </div>
  </LabelledField>
);